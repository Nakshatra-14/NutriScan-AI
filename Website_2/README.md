### Website_2 backend

Basic Node/Express backend that serves the static site and exposes simple APIs.

#### Run

```powershell
cd Website_2
npm install
npm run start
```

Open `http://localhost:3000`.

#### APIs

- GET `/api/health` → `{ ok: true }`
- GET `/api/users` → `{ users: [...] }`
- POST `/api/users` → body: `{ name, age, phone, email }`
- GET `/api/products` → `{ products: [...] }` or `?barcode=XXXX`
- POST `/api/products` → body: `{ name, brand?, barcode, nutrition? }`

Data is stored in `data/users.json` and `data/products.json`.


