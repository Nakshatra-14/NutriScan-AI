import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
import fs from 'fs';
import cors from 'cors';
import morgan from 'morgan';
import XLSX from 'xlsx';
import fetch from 'node-fetch';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());
app.use(cors());
app.use(morgan('dev'));

// Data directories
const dataDir = path.join(__dirname, 'data');
const usersFile = path.join(dataDir, 'users.json');
const productsFile = path.join(dataDir, 'products.json');
const excelPath = path.join(__dirname, '..', 'Database', 'food_db.xlsx');
const barcodeMapFile = path.join(dataDir, 'barcode_map.json');

function ensureDataFiles() {
  if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir, { recursive: true });
  }
  if (!fs.existsSync(usersFile)) {
    fs.writeFileSync(usersFile, JSON.stringify({ users: [] }, null, 2));
  }
  if (!fs.existsSync(productsFile)) {
    fs.writeFileSync(productsFile, JSON.stringify({ products: [] }, null, 2));
  }
}

ensureDataFiles();

function readJson(filePath) {
  try {
    const raw = fs.readFileSync(filePath, 'utf-8');
    return JSON.parse(raw);
  } catch (e) {
    return null;
  }
}

function writeJson(filePath, data) {
  fs.writeFileSync(filePath, JSON.stringify(data, null, 2));
}

// Health
app.get('/api/health', (req, res) => {
  res.json({ ok: true, timestamp: new Date().toISOString() });
});

// Users API
app.get('/api/users', (req, res) => {
  const data = readJson(usersFile) || { users: [] };
  res.json(data);
});

app.post('/api/users', (req, res) => {
  const { name, age, phone, email } = req.body || {};
  if (!name || !email) {
    return res.status(400).json({ error: 'name and email are required' });
  }
  const data = readJson(usersFile) || { users: [] };
  const id = Date.now().toString(36);
  const user = { id, name, age, phone, email, createdAt: new Date().toISOString() };
  data.users.push(user);
  writeJson(usersFile, data);
  res.status(201).json(user);
});

// Products API (basic, with optional barcode lookup)
app.get('/api/products', (req, res) => {
  const data = readJson(productsFile) || { products: [] };
  const { barcode } = req.query;
  if (barcode) {
    const product = data.products.find(p => p.barcode === String(barcode));
    if (!product) return res.status(404).json({ error: 'Not found' });
    return res.json(product);
  }
  res.json(data);
});

app.post('/api/products', (req, res) => {
  const { name, brand, barcode, nutrition } = req.body || {};
  if (!name || !barcode) {
    return res.status(400).json({ error: 'name and barcode are required' });
  }
  const data = readJson(productsFile) || { products: [] };
  if (data.products.find(p => p.barcode === String(barcode))) {
    return res.status(409).json({ error: 'Product with this barcode already exists' });
  }
  const product = {
    id: Date.now().toString(36),
    name,
    brand: brand || null,
    barcode: String(barcode),
    nutrition: nutrition || {},
    createdAt: new Date().toISOString()
  };
  data.products.push(product);
  writeJson(productsFile, data);
  res.status(201).json(product);
});

// Scan endpoint: lookup by barcode in Excel and return enriched insights
app.get('/api/scan', async (req, res) => {
  const raw = req.query.barcode;
  if (!raw) {
    return res.status(400).json({ error: 'barcode is required' });
  }
  const barcode = String(raw).trim().replace(/[^0-9A-Za-z]/g, '');
  if (!barcode) {
    return res.status(400).json({ error: 'Invalid barcode' });
  }

  try {
    // Load Excel (optional)
    let rows = [];
    try {
      const wb = XLSX.readFile(excelPath);
      const sheet = wb.SheetNames[0];
      rows = XLSX.utils.sheet_to_json(wb.Sheets[sheet], { defval: null });
    } catch (err) {
      rows = [];
    }

    // Try local match
    let match = null;
    if (rows.length > 0) {
      match = rows.find(function(r) {
        const val = String(r.barcode || r.Barcode || r.bar_code || '').trim();
        return val === barcode;
      });
      if (!match) {
        let map = null;
        if (fs.existsSync(barcodeMapFile)) {
          try {
            map = JSON.parse(fs.readFileSync(barcodeMapFile, 'utf-8'));
          } catch (e) {
            map = null;
          }
        }
        if (map && Array.isArray(map.mappings)) {
          const entry = map.mappings.find(function(m) { return String(m.barcode) === barcode; });
          if (entry) {
            if (entry.match_by === 'product_name') {
              match = rows.find(function(r) { return String(r.product_name || r.name || '').toLowerCase() === String(entry.value).toLowerCase(); });
            } else if (entry.match_by === 'image_filename') {
              match = rows.find(function(r) { return String(r.image_filename || '').toLowerCase().indexOf(String(entry.value).toLowerCase()) !== -1; });
            }
          }
        }
        if (!match) {
          match = rows.find(function(r) { return String(r.image_filename || '').indexOf(barcode) !== -1; });
        }
      }
    }

    if (!match) {
      // Fallback to Open Food Facts
      const url = 'https://world.openfoodfacts.org/api/v2/product/' + encodeURIComponent(barcode) + '.json';
      let off = null;
      try {
        const r = await fetch(url);
        off = await r.json();
      } catch (e) {
        off = null;
      }
      if (!off || !off.product) {
        return res.status(404).json({ error: 'Product not found in local DB or Open Food Facts' });
      }
      const p = off.product;
      const nutr = p.nutriments || {};
      const levels = p.nutrient_levels || {};
      const nutrients = {
        product_name: p.product_name || null,
        brand: (p.brands || '').split(',')[0] || null,
        quantity: p.quantity || null,
        energy_kcal_100g: numberOrNull(nutr['energy-kcal_100g'] || nutr['energy-kcal_100ml']),
        carbohydrates_100g: numberOrNull(nutr.carbohydrates_100g || nutr.carbohydrates_100ml),
        sugars_100g: numberOrNull(nutr.sugars_100g || nutr.sugars_100ml),
        proteins_100g: numberOrNull(nutr.proteins_100g || nutr.proteins_100ml),
        fat_100g: numberOrNull(nutr.fat_100g || nutr.fat_100ml),
        saturated_fat_100g: numberOrNull(nutr['saturated-fat_100g'] || nutr['saturated-fat_100ml']),
        fiber_100g: numberOrNull(nutr.fiber_100g || nutr.fiber_100ml),
        salt_100g: numberOrNull(nutr.salt_100g || nutr.salt_100ml),
        nova_group: p.nova_group || null,
        fat_level: (levels.fat != null ? levels.fat : null),
        salt_level: (levels.salt != null ? levels.salt : null),
        sugars_level: (levels.sugars != null ? levels.sugars : null),
        saturated_fat_level: (levels['saturated-fat'] != null ? levels['saturated-fat'] : null),
        image: p.image_front_small_url || p.image_url || null
      };
      const gi = estimateGI(nutrients);
      const eco = estimateEcoScore(nutrients);
      const rating = rateAE(nutrients, gi, eco);
      const insights = buildInsights(nutrients, gi, eco, rating);
      return res.json({ barcode: String(barcode), source: 'openfoodfacts', nutrients, glycemic_index: gi, ecoscore: eco, rating, insights });
    }

    // Build from local match
    const nutrients = {
      product_name: match.product_name || match.name || null,
      brand: match.Brand || match.brand || null,
      quantity: match.quantity || null,
      energy_kcal_100g: numberOrNull(match['energy-kcal_100g']),
      carbohydrates_100g: numberOrNull(match['carbohydrates_100'] || match['carbohydrates_100g']),
      sugars_100g: numberOrNull(match['sugars_100g']),
      proteins_100g: numberOrNull(match['proteins_100g']),
      fat_100g: numberOrNull(match['fat_100g']),
      saturated_fat_100g: numberOrNull(match['saturated-fat_100g']),
      fiber_100g: numberOrNull(match['fiber_100g']),
      salt_100g: numberOrNull(match['salt_100g']),
      nova_group: (match['nova_group'] != null ? match['nova_group'] : null),
      fat_level: (match['fat_level'] != null ? match['fat_level'] : null),
      salt_level: (match['salt_level'] != null ? match['salt_level'] : null),
      sugars_level: (match['sugars_level'] != null ? match['sugars_level'] : null),
      saturated_fat_level: (match['saturated-fat_level'] != null ? match['saturated-fat_level'] : null),
      image: match['image_filename'] || null
    };
    const gi = estimateGI(nutrients);
    const eco = estimateEcoScore(nutrients);
    const rating = rateAE(nutrients, gi, eco);
    const insights = buildInsights(nutrients, gi, eco, rating);
    return res.json({ barcode: String(barcode), nutrients, glycemic_index: gi, ecoscore: eco, rating, insights });
  } catch (e) {
    console.error(e);
    return res.status(500).json({ error: 'Failed to process scan' });
  }
});

function numberOrNull(val) {
  const n = Number(val);
  return Number.isFinite(n) ? n : null;
}

function estimateGI(n) {
  // Simple heuristic: higher sugars and lower fiber increase GI; fat lowers a bit.
  const sugars = n.sugars_100g ?? 0;
  const fiber = n.fiber_100g ?? 0;
  const fat = n.fat_100g ?? 0;
  let gi = 50 + sugars * 0.8 - fiber * 2 - fat * 0.5;
  gi = Math.max(20, Math.min(95, gi));
  return Math.round(gi);
}

function estimateEcoScore(n) {
  // Heuristic using NOVA: ultra-processed tends to have worse eco; quantity as proxy.
  const nova = Number(n.nova_group) || 4;
  const qty = Number(n.quantity) || 100;
  let score = 80 - (nova - 1) * 10 - Math.min(30, Math.max(0, (qty - 100) / 10));
  score = Math.max(0, Math.min(100, score));
  return Math.round(score);
}

function rateAE(n, gi, eco) {
  // Combine nutrition (sugars, sat fat, salt), GI and Eco into letter grade
  const sugars = n.sugars_100g ?? 0;
  const sat = n.saturated_fat_100g ?? 0;
  const salt = n.salt_100g ?? 0;
  // Lower is better for these three
  let nutritionPenalty = sugars * 0.5 + sat * 2 + salt * 10;
  let giPenalty = (gi - 50) * 1.0; // around zero center
  let ecoBonus = (eco - 50) * 0.8; // higher eco is better
  let composite = 100 - nutritionPenalty - giPenalty + ecoBonus;
  if (composite >= 80) return 'A';
  if (composite >= 65) return 'B';
  if (composite >= 50) return 'C';
  if (composite >= 35) return 'D';
  return 'E';
}

function buildInsights(n, gi, eco, rating) {
  const tips = [];
  if ((n.sugars_100g ?? 0) > 10) tips.push('High sugars: prefer low-sugar alternatives or smaller portions.');
  if ((n.saturated_fat_100g ?? 0) > 5) tips.push('High saturated fat: consider options with unsaturated fats.');
  if ((n.salt_100g ?? 0) > 1) tips.push('High salt: watch total daily sodium intake.');
  if ((n.fiber_100g ?? 0) < 3) tips.push('Low fiber: pair with fiber-rich foods (vegetables, whole grains).');
  tips.push(gi >= 70 ? 'High GI: may spike blood sugar; pair with protein/fiber.' : gi <= 55 ? 'Low GI: steadier blood sugar response.' : 'Medium GI: moderate impact on blood sugar.');
  tips.push(eco >= 70 ? 'Good EcoScore: relatively lower environmental impact.' : eco <= 40 ? 'Low EcoScore: consider more sustainable alternatives.' : 'Average EcoScore.');
  return { rating_reasoning: `Composite assessment resulted in grade ${rating}.`, recommendations: tips };
}

// Serve static site
app.use(express.static(__dirname));

// Fallback to index for top-level pages
app.get(['/', '/index.html'], (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});


