# FoodSense AI Website

A complete FoodSense AI landing page with backend functionality for nutrition analysis.

## Setup

1. Open a terminal in this folder:
   ```powershell
   cd Website-2
   npm install
   npm run start
   ```
2. Open `http://localhost:3000` in your browser.

## Features

- **Modern Landing Page**: Clean FoodSense AI design with mobile mockup
- **User Registration**: Sign up with name, age, phone, email
- **Barcode Scanning**: Scan products for nutrition analysis
- **AI Insights**: Get personalized recommendations
- **Nutrition Analysis**: Complete breakdown with GI, EcoScore, A-E rating
- **Theme Toggle**: Dark/light mode support

## APIs

- `GET /api/health` → `{ ok: true }`
- `GET /api/users` → `{ users: [...] }`
- `POST /api/users` → create user `{ name, age, phone, email }`
- `GET /api/products` → `{ products: [...] }` or with query `?barcode=XXXX`
- `POST /api/products` → create product `{ name, brand?, barcode, nutrition? }`
- `GET /api/scan?barcode=XXXX` → full nutrition analysis with AI insights

## Data

JSON files are stored under `data/`:
- `data/users.json`
- `data/products.json`
- `data/barcode_map.json`

The system reads from `Database/food_db.xlsx` and falls back to Open Food Facts API for barcode lookups.
