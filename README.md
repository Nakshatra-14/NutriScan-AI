#  NutriScan AI — AI-powered Food Health Evaluator 

[![Hackathon Ready](https://img.shields.io/badge/Hackathon-ready-blue.svg)](#)
[![License MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0-orange.svg)](#)

**Prototype**: Machine Learning-based food suitability tracker & guardian Care Mode app.

---

##  Quick Links

| Action | Button |
|--------|--------|
| Download APK | [![Download APK](https://img.shields.io/badge/APK-green?logo=android)](app/app-release.apk) |
| Launch Web App | [![Open Website](https://img.shields.io/badge/Web_App-blue?logo=chrome)](website/index.html) |
| View App Code | [![App Code](https://img.shields.io/badge/View_App–Directory-brightgreen)](app/) |
| View Website Code | [![Website Code](https://img.shields.io/badge/View_Website–Directory-lightgrey)](website/) |

---

##  Overview

**NutriScan AI** is an AI-powered food evaluator that uses machine learning to analyze scanned food based on nutrient content, age group, and health status (like diabetes risk), delivering clear feedback. It generates a label ("Good", "OK", or "Bad") and 5–6 easy-to-read sentences (e.g., **"Sugar is high — may be risky if you're diabetic"**), with multilingual text and voice output (English, Hindi, Bengali).

Your guardian can activate **Care Mode** to monitor in real time what you're scanning and instantly view health suitability and alerts on a separate app or web panel.

---

## 📂 Project Structure
```plaintext
NutriScan/
│
├── app/                # Android app source code
│   ├── nutriscan.apk   # One-click install APK
│   └── src/            # App source files
│
├── website/            # Website frontend
│   ├── index.html
│   └── assets/
│
└── README.md           # Project documentation
```

| Directory | Purpose |
|-----------|---------|
| `app/` | Contains mobile app files, including the signed APK and app source code. |
| `website/` | Contains the frontend web dashboard for Care Mode and education hub. |

---

##  Demo Preview

*(Insert image below showcasing app UI or web dashboard in action)*

![App Demo](docs/demo-screenshot.png)

---

##  Installation & Usage

###  Mobile App (Android)
1. **Click the “Download APK” button above** to download.
2. Install `app-release.apk` on your Android device.
3. Open the app, scan a product, and receive real-time AI feedback.

###  Web Dashboard (Care Mode)
1. Click **“Launch Web App”**.
2. View real-time scans from mobile users in your network and their AI-driven food assessments.

---

##  Core Features

- **Machine Learning Suitability Assessment**  
  Classifies a scanned food item as **Good / OK / Bad**.

- **Nutrient Insight Sentences**  
  Personalized feedback (e.g., balanced carbs, high sugar, low fiber).

- **Diabetes Risk Identification**  
  Alerts for high-sugar or high-carb items for diabetic users.

- **Multilingual & Accessible**  
  Outputs available in English, Hindi, Bengali — with TTS support.

- **Health & Sustainability Scores**  
  Estimates Glycemic Index (GI), NutriScore, and EcoScore for quick insights.

- **Care Mode**  
  Guardians can monitor real-time scan data via mobile or web.

- **Education Module**  
  Users learn how scores (GI, NutriScore, EcoScore) are calculated and used.
