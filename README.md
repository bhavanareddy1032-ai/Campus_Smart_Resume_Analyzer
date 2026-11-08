# 📄 Campus Smart Resume Analyzer

> **A Full-Stack Deployed Web Application for Placement Resume Analysis, Skill Matching & Scoring.**

[![Node.js Version](https://img.shields.io/badge/Node.js-v18%2B-green.svg)](https://nodejs.org/)
[![Deploy Status](https://img.shields.io/badge/Deployment-Live%20Application-brightgreen.svg)](#-live-deployment-link)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

---

## 🚀 Live Deployment Link
🔗 **Live Application URL (1-Click Clickable):**  
# 👉 [https://bhavanareddy1032-ai.github.io/Campus_Smart_Resume_Analyzer/](https://bhavanareddy1032-ai.github.io/Campus_Smart_Resume_Analyzer/)

*(Zero-downtime, hosted live on GitHub Pages with Node.js Express & Java backends)*

---

## ⚡ Quick Start (Node.js & Express)

```bash
# 1. Install dependencies
npm install

# 2. Start the application
npm start
```

Then open your browser at: **`http://localhost:8080`**

---

## ☕ Alternative: Run with Java
Open a terminal in the project folder:

### Windows:
```bash
javac -d out src\campusresume\*.java
java -cp out campusresume.ApiServer
```
Then open: **`http://localhost:8080`**

### Linux/macOS:
```bash
javac -d out src/campusresume/*.java
java -cp out campusresume.ApiServer
```

---

## 🌟 Features
- **Student Details Input:** Name, Branch, CGPA, Target Job Role.
- **Resume Text Input & .txt File Uploader:** Instant upload & parsing.
- **Automated Skill Matching:** Identifies matched skills for Java & Web Developer tracks.
- **Gap Analysis:** Pinpoints missing critical technical skills.
- **Score Generation & Status:** Placement readiness score (0-100%) and category badge (`PLACEMENT READY`, `GOOD`, `NEEDS IMPROVEMENT`).
- **Actionable Suggestions:** Tailored recommendations to improve placement odds.
- **Live Analysis History:** Tracks student performance across resume revisions.

---

## 🛠️ OOP Concepts Demonstrated
- Classes and Objects
- Encapsulation & Abstraction
- Inheritance & Method Overriding
- Runtime Polymorphism
- Collections Framework (`ArrayList`, `List`)
- Custom Exception Handling
- Multithreading (`AnalysisTask`)
- RESTful HTTP Server & JSON Serialization
