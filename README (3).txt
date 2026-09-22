markdown
# PulseFit 🏃‍♂️

> **Train. Earn. Rise.**

PulseFit is a gamified Android fitness tracking application built with **Jetpack Compose**, **Firebase**, and a custom **REST API**. It combines accurate workout tracking with social accountability squads, XP-based progression, and AI-driven coaching insights to solve the critical problem of user retention in fitness apps.

---

## 📌 Table of Contents

- [Features](#-features)
- [Screenshots](#-screenshots)
- [App Architecture](#-app-architecture)
- [Tech Stack](#-tech-stack)
- [REST API](#-rest-api)
- [Database Design](#-database-design)
- [Getting Started](#-getting-started)
- [Testing](#-testing)
- [GitHub & GitHub Actions](#-github--github-actions)
- [Demo Video](#-demo-video)
- [Release Notes](#-release-notes)
- [AI Usage Declaration](#-ai-usage-declaration)
- [Team](#-team)
- [License](#-license)

---

## ✨ Features

### 🔐 Authentication & Account Management
- **Email / Password Registration** with client-side validation
- **Password Encryption** — Firebase hashes passwords using bcrypt server-side
- **Google Sign-In (SSO)** via Credential Manager API
- **Password Reset & Change** with re-authentication
- **Account Deletion** with password confirmation
- **Data Export** — download all user data as a text file

### 👤 User Profile & Settings
- Personal information management
- Units toggle (Metric / Imperial)
- Dark / Light theme
- Notification preferences
- **Multi-language support**: English, isiZulu, Afrikaans, Setswana
- Terms & Conditions viewer

### 🏋️ Core Fitness Features
- **Activity Logger** — log Running, Cycling, Weightlifting, Walking
- Track duration, distance, calories burned, and notes
- **Offline-first design** with RoomDB caching
- **Automatic sync** when connectivity is restored
- Historical workout data

### 🎮 Gamification
- **Dynamic XP System** — earn XP for every logged workout
- **Tiered Badges** — Bronze → Silver → Gold → Platinum
- **Achievements** — Iron Runner, Consistency Master, Streak Shield, and more
- **Streak Shields** — protect streaks from single missed days

### 👥 Social Features
- **Squads** — mini-groups of up to 6 members
- **Group Challenges** — collective distance goals
- **Shared Leaderboards** — see how you rank against squadmates

### 🤖 AI Coach Insights
- Rule-based intelligent engine analyzing workout data
- Proactive rest-day recommendations
- Fatigue alerts and training intensity adjustments

### 🌐 Multi-Language Support
- Full UI localisation across 4 languages
- Persistent language selection via SharedPreferences
- Dynamically re-loaded on app restart

---

## 📱 Screenshots

| Splash | Login | Home |
|---|---|---|
| ![Splash](screenshots/splash.png) | ![Login](screenshots/login.png) | ![Home](screenshots/home.png) |

| Activity Logger | Achievements | Squads |
|---|---|---|
| ![Logger](screenshots/logger.png) | ![Achievements](screenshots/achievements.png) | ![Squads](screenshots/squads.png) |

| Profile | Language Picker | Terms & Conditions |
|---|---|---|
| ![Profile](screenshots/profile.png) | ![Language](screenshots/language.png) | ![TC](screenshots/terms.png) |

> **Note:** Add your own screenshots in a `screenshots/` folder at the repo root.

---

## 🏗️ App Architecture

PulseFit follows **MVVM (Model-View-ViewModel)** with a **Repository pattern** for data access:
┌──────────────────────────────────────┐
│ Jetpack Compose UI (Screens) │
└──────────────┬───────────────────────┘
│
┌──────────────▼───────────────────────┐
│ ViewModels (AuthVM, HomeVM, etc.) │
└──────────────┬───────────────────────┘
│
┌──────────────▼───────────────────────┐
│ Repository Layer │
│ - AuthRepository │
│ - PulseFitRepository │
└──────┬─────────────────────┬──────────┘
│ │
┌──────▼──────┐ ┌──────▼──────────┐
│ RoomDB │ │ Retrofit + │
│ (offline) │ │ REST API │
└─────────────┘ └─────────────────┘
│ │
└───────┬─────────────┘
│
┌──────▼──────┐
│ Firebase │
│ Auth + FCM │
└─────────────┘


**Key architectural decisions:**
- **Offline-first** — all writes go to RoomDB first, then sync to the API
- **Single source of truth** — UI observes Flow from the repository
- **Conflict resolution** — timestamp-based "last write wins"
- **Separation of concerns** — UI, business logic, and data layers cleanly divided

---

## 🛠️ Tech Stack

### Android (Client)
| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.24 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.8.2 |
| State | ViewModel + StateFlow |
| Local DB | RoomDB 2.6.1 |
| Networking | Retrofit 2.11 + OkHttp |
| Auth | Firebase Authentication |
| Push | Firebase Cloud Messaging (FCM) |
| SSO | Credential Manager 1.2.2 |
| Async | Coroutines 1.8.1 |
| Build | AGP 8.5.2, Gradle 8.7, JDK 17 |

### Backend (API)
| Layer | Technology |
|---|---|
| Runtime | Node.js 20 |
| Framework | Express.js |
| Language | TypeScript 5.6 |
| ORM | Prisma 5.20 |
| Database | PostgreSQL (Neon) |
| Auth | JWT Bearer tokens |
| Password | bcrypt (10 salt rounds) |
| Validation | Zod |
| Security | Helmet, CORS, rate limiting |

---

## 🌐 REST API

Base URL: `https://pulsefit-api.onrender.com`

All endpoints (except `/api/auth/*`) require a Bearer token in the `Authorization` header.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/auth/register` | Create a new account |
| `POST` | `/api/auth/login` | Log in and receive JWT |
| `GET` | `/api/users/me` | Fetch current user profile |
| `PUT` | `/api/users/me/settings` | Update user preferences |
| `GET` | `/api/activities?since=` | Fetch activities |
| `POST` | `/api/activities` | Log a workout |
| `POST` | `/api/activities/sync` | Batch-upload offline activities |
| `GET` | `/api/badges` | List all badges + earned status |
| `GET` | `/api/squads` | List user's squads |
| `POST` | `/api/squads` | Create a squad |
| `POST` | `/api/squads/:id/join` | Join a squad |
| `GET` | `/api/squads/:id/leaderboard` | Get squad leaderboard |
| `GET` | `/api/ai/insights` | Get AI coaching recommendations |

### Example: Register Request
```bash
curl -X POST https://pulsefit-api.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "username": "runner01",
    "password": "securePass123"
  }'
Example: Offline Sync Request
POST /api/activities/sync
{
  "deviceId": "android-abc123",
  "lastSync": "2026-08-24T08:00:00Z",
  "activities": [
    {
      "localId": "11",
      "type": "running",
      "durationSeconds": 1920,
      "distanceKm": 5.2,
      "calories": 340,
      "timestamp": "2026-08-24T09:10:00Z"
    }
  ],
  "deletedIds": []
}
🗄️ Database Design
Remote (PostgreSQL via Prisma)
users ──────┬────── user_settings
            │
            ├────── activities
            │
            ├────── user_badges ──── badges
            │
            └────── squad_members ── squads
Core tables:

users — id, email, username, password_hash, xp, tier

user_settings — units, theme, language, notifications

activities — type, duration, distance, calories, timestamp

badges — name, description, criteria

user_badges — earned timestamp per user

squads — name, max_members, owner_id

squad_members — squad_id, user_id, joined_at

Local (RoomDB)
activities — pending sync queue + cached history

profile — cached user profile

settings — cached preferences

badges — cached badge data

🚀 Getting Started
Prerequisites
Android Studio Hedgehog (2023.1.1) or newer

JDK 17

Android SDK 24–35

A Firebase project with:

Email/Password authentication enabled

Google Sign-In enabled

google-services.json downloaded

Setup
Clone the repository:

git clone https://github.com/<your-username>/pulsefit.git
cd pulsefit
Add your Firebase config:

Place google-services.json in app/

Set your Web Client ID (for Google Sign-In):

Open ui/auth/LoginScreen.kt

Replace WEB_CLIENT_ID with your OAuth 2.0 Web client ID from Google Cloud Console

Point to your API (if you've deployed the backend):

Open data/remote/RetrofitInstance.kt

Replace BASE_URL with your Render / Railway / local URL

Build and run:

./gradlew assembleDebug
Or click Run ▶️ in Android Studio.

🧪 Testing
PulseFit includes both unit tests and instrumented Compose UI tests.

Run Unit Tests
./gradlew testDebugUnitTest
Covers:

AuthViewModelTest — login, register, error handling, logout

ActivityViewModelTest — save workflow, repo interaction

SettingsViewModelTest — password change validation, error states

Run Instrumented Tests
./gradlew connectedAndroidTest
Covers:

LoginScreenTest — UI rendering, button clicks, navigation callbacks

Test Report
After running, open:

app/build/reports/tests/testDebugUnitTest/index.html
🤖 GitHub & GitHub Actions
Repository Structure
pulsefit/
├── .github/
│   └── workflows/
│       └── build.yml          ← CI/CD pipeline
├── app/                        ← Android app
│   ├── src/main/
│   ├── src/test/              ← Unit tests
│   └── src/androidTest/       ← Instrumented tests
├── pulsefit-api/              ← Node.js backend
│   ├── prisma/
│   ├── src/
│   └── package.json
└── README.md
CI Pipeline (.github/workflows/build.yml)
Every push to main triggers:

✅ Checkout code

✅ Set up JDK 17

✅ Run all unit tests (./gradlew testDebugUnitTest)

✅ Build debug APK (./gradlew assembleDebug)

✅ Upload test results as artifacts

✅ Upload debug APK as artifact

Live status: https://github.com/%3Cyour-username%3E/pulsefit/actions/workflows/build.yml/badge.svg

Git Practices
Multiple meaningful commits (not one dump)

Feature branches for each major addition

Descriptive commit messages (e.g., feat: add Google Sign-In via Credential Manager)

Final commit tagged as final-poe for the PoE submission

🎥 Demo Video
📺 Watch the full demonstration on YouTube (unlisted):

👉 PulseFit Demo — OPSC6312 PoE

The video demonstrates:

Registration with email and password

Google Sign-In (SSO)

Password change flow

Offline activity logging + sync

Multi-language switching (English → isiZulu → Afrikaans → Setswana)

Push notification

Data export & account deletion

Live view of Firebase Console + Neon Postgres showing persisted data

📋 Release Notes
v1.0.0 — Final PoE (September 2026)
New Features since Prototype:

🆕 Google Sign-In (SSO) via Credential Manager API

🆕 Multi-language support — English, isiZulu, Afrikaans, Setswana

🆕 Real-time push notifications via Firebase Cloud Messaging

🆕 Offline mode with automatic sync — RoomDB queue with timestamp-based conflict resolution

🆕 AI Coach Insights — rule-based training load and fatigue analysis

🆕 Streak Shields — gamified streak protection

🆕 Squads & Leaderboards — social accountability groups

🆕 Account management — change password, export data, delete account

🆕 Terms & Conditions viewer

🆕 App icon with adaptive icon support

🆕 Splash screen with branded logo

Improvements:

⚡ Migrated from MVVM to Repository pattern for offline-first data flow

⚡ Added comprehensive input validation across all forms

⚡ Improved error handling with user-friendly messages

⚡ Added 10+ automated unit tests and 3 Compose UI tests

v0.5.0 — Prototype (August 2026)
Initial working prototype

Email/password registration and login

Basic activity logging

Home screen with XP and streak display

Bottom navigation with 5 tabs

Settings screen with theme toggle

🤖 AI Usage Declaration
In accordance with the OPSC6312 intellectual integrity policy, we declare the following use of generative AI during this project:

Gosego — UI/UX Prototyping
Used Figma's generative AI to brainstorm wireframes and layout mockups. All component placement, visual hierarchy, and user experience decisions were manually refined. DeepSeek assisted with formatting the REST API documentation.

Onnelle — Technical Phrasing & System Architecture
Used Google Gemini to articulate complex data-flow concepts. Backend architecture, sync logic, and real-time tracking decisions were all human-driven.

Thandiwe — Project Planning & Endpoints
Consulted AI tools to outline the project plan and brainstorm API endpoint naming conventions. All milestone planning was reviewed and adapted to PulseFit's specific requirements.

General Development
Claude (Anthropic) was used as a debugging and code-generation assistant throughout the build phase. All generated code was:

✅ Reviewed line by line

✅ Adapted to fit the project's architecture

✅ Tested on real devices

✅ Commented to explain its purpose

All core logic, critical thinking, and final implementation remain entirely human-driven. Every AI-generated output was treated as a foundational draft, not a final product.

👥 Team
Name	Role
Gosego	UI/UX Design & Documentation
Onnelle	System Architecture & Technical Writing
Thandiwe	Project Planning & API Design
Module: OPSC6312 — Open Source Coding (Intermediate)
Institution: The Independent Institute of Education (IIE)
Year: 2026

📜 License
This project is submitted as academic coursework for OPSC6312 at IIE. Not licensed for commercial use.

🔗 References
Android Developers — Room

Firebase Authentication

Credential Manager

Jetpack Compose

Prisma ORM

Neon Serverless Postgres

Render Cloud Hosting
