<!-- # 🍏 NutriScan AI — Your Intelligent Food Evaluator   -->

<div align="center">

![NutriScan Logo](https://i.ibb.co/LhDjnjmG/Nutri-Scan-Banner.png)

![Hackathon Badge](https://img.shields.io/badge/Hackathon-Ready-brightgreen) 
![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg) 
![Version](https://img.shields.io/badge/Version-1.0.0-orange)

</div>


---

## 🚀 Getting Started  

This project contains both an **Android Application** and a **Web Application**.  
Pick your platform below and get started in just one click 👇  

---

<div align="center">

### 📱 Android App  

[![Download APK](https://img.shields.io/badge/⬇️_Download-APK-success?style=for-the-badge&logo=android)](app/build/outputs/apk/debug/NutriScan.apk)  
[![View App Code](https://img.shields.io/badge/💻_View-App_Code-brightgreen?style=for-the-badge&logo=kotlin)](app/)  

</div>

---

<div align="center">

### 🌐 Web App  

[![Launch Web App](https://img.shields.io/badge/🚀_Launch-Web_App-blue?style=for-the-badge&logo=google-chrome)](https://nutri-scan-ai-azure.vercel.app/)  
[![View Website Code](https://img.shields.io/badge/💻_View-Website_Code-lightgrey?style=for-the-badge&logo=react)](Website/my-app)  

</div>

---

✨ **Pro Tip:**  
- Install the APK on your Android device to try the app.  
- Use the hosted **Web App** link for instant access in your browser.  

---

<div align="center">

# 🌍 NutriScan Overview 
 
*AI + ML Powered Nutrition Intelligence*
</div>

NutriScan AI is not just a nutrition scanner — it’s a **full-stack AI/ML system** that transforms raw food data into **actionable health intelligence**.  

---

## 🧠 The AI/ML Brain Behind NutriScan  

NutriScan is powered by a **next-gen hybrid AI engine** — blending **rule-based formulas, probabilistic models, and machine learning** into a single intelligent pipeline.  

- ⚡ **Real-Time Computation** → Instantly calculates Calories, GI, NutriScore, and EcoScore using transparent formulas + statistical sampling.  
- 🧠 **Adaptive ML Layer** → Learns from user profiles (age, diabetes status, diet type) and refines scoring dynamically.  
- 🔍 **Risk Pattern Detector** → Flags high-risk combos (e.g., sugar + carbs → ⚠️ diabetes warning).  
- 🗣️ **Dynamic Narration** → Generates **multilingual insights + natural TTS** (English, Hindi, Bengali), so results feel human, not robotic.  
- 📊 **Explainable AI** → Every recommendation comes with a **clear “why”**, bridging trust between raw numbers and health advice.  

✅ **End result:** A scanner that doesn’t just show numbers — it **thinks, adapts, and explains** like a real nutrition expert.  


---

<div align="center">

# 🚀 What It Does  

</div>

✔️ Converts **raw nutrition facts → meaningful recommendations** in real time  
✔️ Flags **diabetes risks** with instant alerts  
✔️ Provides **multilingual text + natural TTS** (English, Hindi, Bengali)  
✔️ Explains the **why** via an **Education Module**  
✔️ Enables **Care Mode** (remote monitoring + alerts for caregivers)  

---


<div align="center">

# 👥 Who Benefits?  

</div>

- 🩸 **People with Diabetes** → Instant sugar/carbs alerts  
- 👨‍👩‍👧 **Caregivers & Parents** → Remote oversight & safety alerts  
- 🛒 **Everyday Shoppers** → Healthier, faster supermarket decisions  
- 📚 **Educators** → Interactive nutrition learning tool  

---

<div align="center">

# 🧮 Core Formulas Behind NutriScan

</div>

### 🔥 Calories Calculation
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{Calories}=(\text{Protein}\times4)+(\text{Carbohydrates}\times4)+(\text{Fat}\times9)" alt="Calories Formula"/>
</p>

---

### 🥗 Nutri-Score
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{NutriScore}=(\text{EnergyPts}+\text{SugarPts}+\text{FatPts})-(\text{FiberPts}+\text{ProteinPts})" alt="Nutri-Score Overall"/>
</p>

**Where:**
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{EnergyPts}=\min(10,\max(0,\lfloor\frac{\text{Calories}}{335}\rfloor))" alt="Energy Points"/>
</p>
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{SugarPts}=\min(10,\max(0,\lfloor\frac{\text{Sugar}}{4.5}\rfloor))" alt="Sugar Points"/>
</p>
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{FatPts}=\min(10,\max(0,\lfloor\frac{\text{Fat}}{1}\rfloor))" alt="Fat Points"/>
</p>
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{FiberPts}=\min(5,\max(0,\lfloor\frac{\text{Fiber}}{0.9}\rfloor))" alt="Fiber Points"/>
</p>
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}\text{ProteinPts}=\min(5,\max(0,\lfloor\frac{\text{Protein}}{1.6}\rfloor))" alt="Protein Points"/>
</p>

---

### 🍬 Glycemic Index (GI)
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}GI=\min\!\Big(100,\max\!\big(0,\mathcal{N}(55,15)+\frac{\text{Sugar}}{10}+\frac{\text{Carbohydrates}}{50}\big)\Big)" alt="Glycemic Index"/>
</p>

---

### 🌱 Eco-Score
<p align="center">
  <img src="https://latex.codecogs.com/svg.image?\bg{transparent}\color{white}EcoScore=\min\!\Big(100,\max\!\big(0,\mathcal{N}(50,20)+\frac{\text{Protein}}{5}\big)\Big)" alt="Eco-Score"/>
</p>

---

## 🛠️ Tech Stack  

**Frontend**: React, Vite, Tailwind CSS  
**Backend**: Python, Flask (AI/ML model)  
**Mobile**: Kotlin, Jetpack Compose, CameraX, ML Kit  
**Database**: (Add here if using)  
**APIs**: Local API  

---

## 📱 App & Website  

### 📲 Android App  
Built with **Kotlin + Jetpack Compose**  
- Barcode Scanner (CameraX + ML Kit)  
- Detailed Results Screen  
- Profiles + Learning Center  

### 💻 Website  
Responsive **React + Tailwind** site  
- Project Info Hub  
- Portal for **Care Mode** feature
- 
---

## 📂 Project Structure  
```
NutriScan/
│
├── app/ # Android App Source Code
│ └── build/
│ └── outputs/
│ └── apk/
│ └── debug/
│ └── NutriScan.apk # Installable APK
│
└── Website/ # Website Source Code
└── my-app/
├── public/
├── src/
└── index.html
```
---

## 🖼️ Screenshots & Demos  

📱 **App Screenshots**: (Coming soon)  
🌐 **Website Preview**: (Coming soon)  
<centre> [![View App Code](https://img.shields.io/badge/View-DEMO-brightgreen?style=for-the-badge&logo=youtube)](app/) <!-- Replace with real link -->  

---

## 👨‍💻 Our Team<br>
Nakshatra Naskar – Project Lead & AI/ML Developer<br>
(Add teammates & roles here)

## 📜 License
This project is licensed under the MIT License – see LICENSE
 for details.
