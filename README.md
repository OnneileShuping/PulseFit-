# PulseFit 🏃‍♂️

> **Train. Earn. Rise.**

PulseFit is a gamified Android fitness tracking application built with **Jetpack Compose**, **Firebase**, and a custom **REST API**. It combines accurate workout tracking with social accountability squads, XP-based progression, customizable habit tracking, and AI-driven coaching insights to solve the critical problem of user retention in fitness apps.

---

## 📌 Table of Contents

- [Features](#-features)
- [Theme & Visual Identity](#-theme--visual-identity)
- [Own Features (for marking)](#-own-features-for-marking)
- [Screenshots](#-screenshots)
- [App Architecture](#-app-architecture)
- [Tech Stack](#-tech-stack)
- [REST API](#-rest-api)
- [Database Design](#-database-design)
- [Getting Started](#-getting-started)
- [Testing](#-testing)
- [Release Notes](#-release-notes)
- [AI Usage Declaration](#-ai-usage-declaration)
- [Team](#-team)
- [License](#-license)

---

## ✨ Features

### 🎨 App Icon Theme & Visual Identity
- **Neon Dark Aesthetic** — dark charcoal-navy UI matching the app launcher icon's glowing neon ring
- **Gradient Accents** — `PulseGradient` horizontal brush (`#00B8F0` cyan to `#28B878` green) on primary buttons, progress bars, title headers, and goal rings
- **Dark & Light Mode Support** — full support for both dark and light modes, toggled in Profile Settings

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
- Dark / Light theme selection
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
- **Dynamic XP System** — earn 1 XP for every 2 calories burned
- **Tiered Badges** — Bronze → Silver → Gold → Platinum
- **7 Derived Badges** — First Steps, Iron Runner, Consistency Master, Streak Saver, Century Cyclist, Goal Getter, Habit Hero
- **Streak Shields** — protect streaks from single missed days

### 👥 Social Features
- **Squads** — mini-groups of up to 6 members
- **Group Challenges** — collective distance goals
- **Shared Leaderboards** — see how you rank against squadmates

### 🤖 50-Tips-Per-Activity AI Coach
- **Comprehensive On-Device Tip Library** — 50 distinct, safe, non-medical tips per activity type (Running, Cycling, Weightlifting, Walking, and General fallback)
- **Interactive Exploration** — horizontally scrollable category chips, tip cards with lightbulb icons, and a tip shuffle button
- **Tip of the Day** — deterministic day-index tip featured on the Home screen
- Proactive rest-day recommendations based on training load and fatigue

### ✅ Daily Habit Tracker
- **Custom Habit Creation** — name, emoji picker (24 presets), color selector (6 theme colors), weekday mask scheduling
- **Streak & 7-Day History** — 🔥 consecutive day streaks (skipping unscheduled days) and 7-day visual status dots
- **Interactive Checkbox** — animated scale check toggle with instant progress updating
- **Inline Validation & Management** — duplicate name prevention, max 40 chars, long-press edit/archive/delete
- **Gamification Link** — "Habit Hero" badge unlocked after completing all scheduled habits on 7 different days

---

## 🎨 Theme & Visual Identity

The UI palette directly mirrors the dark launcher icon with neon cyan/green glow:

| Role | Hex | Color Name / Usage |
|---|---|---|
| Background | `#0F1A21` | Near-black navy |
| Surface | `#17252E` | Card surface |
| Surface Variant | `#22343F` | Progress tracks & chip containers |
| Primary | `#00B8F0` | Icon cyan accent |
| Secondary | `#28B878` | Icon green accent / Success |
| Tertiary | `#7CF2C0` | Soft mint highlight |
| Primary Container | `#0B3A4D` | Selected navigation & banner fill |
| On Primary Container | `#BFEFFF` | Light cyan banner text |
| On Background / Surface | `#E6F2F7` | Primary text |
| On Surface Variant | `#9FB6C2` | Subtitle text |
| Error / Danger | `#FF6B6B` | High fatigue & deletion text |
| Warning | `#FFB347` | Moderate level / Target lines |
| Gradient | `#00B8F0` → `#28B878` | `PulseGradient` horizontal brush |

---

## 🏆 Own Features (for marking)

### 1. Achievements & Streak Shields
- **Description**: Real-time gamification system computing XP, 4 progression tiers (Bronze → Silver → Gold → Platinum), 7 derived badges (including Habit Hero), and a streak system protected by Streak Shields. Users earn 1 shield for every 7 consecutive active days (max 2). When a workout day is missed, a shield is automatically consumed to preserve the streak.
- **Where to find**: Tap the **Progress** tab on the bottom navigation bar or tap **Achievements →** on the Home screen.
- **How to test**:
  - Log workouts to earn XP (1 XP per 2 calories burned).
  - Run unit tests: `./gradlew testDebugUnitTest --tests com.example.pulsefit.domain.GamificationEngineTest` to verify streak calculation, shield consumption, and tier progression.

### 2. Weekly Goal & Trends
- **Description**: Interactive activity tracking and trend analysis. Displays a Canvas-rendered circular progress ring showing current weekly active minutes against a user-customizable target (30 to 600 mins), a 7-day Canvas bar chart with daily target threshold line and highlighted today-bar, and week-over-week comparisons.
- **Where to find**: Tap **Weekly Goal →** on the Home screen or **Weekly Goal & Trends →** on the Achievements screen.
- **How to test**:
  - Adjust the goal slider at the bottom of the screen to change your weekly target.
  - Log workouts on different days to observe the 7-day bar chart and progress ring update in real time.
  - Run unit tests: `./gradlew testDebugUnitTest --tests com.example.pulsefit.domain.WeeklyStatsTest` to test goal clamping, daily target calculation, and bar bucketing.

### 3. Customizable Daily Habit Tracker
- **Description**: Fully integrated daily habit tracking feature powered by RoomDB (`habits` and `habit_checks` tables with foreign key constraints and real SQL Migration 1→2). Supports custom habit creation with emoji selection (24 presets), color accents (6 theme colors), weekday mask scheduling, streak calculations skipping unscheduled days, 7-day dot status rows, interactive checkboxes with scale animations, 3 starter habits, and "Habit Hero" badge integration.
- **Where to find**: Tap the **Habits** tab on the bottom navigation bar or tap **Habits →** on the Home screen.
- **How to test**:
  - Tap "+ New habit" to create a custom habit or tap "Add 3 Starter Habits" on the empty state.
  - Check off habits to observe progress updates and streak increments.
  - Edit or delete habits using the overflow menu on each habit card.
  - Run unit tests: `./gradlew testDebugUnitTest --tests com.example.pulsefit.domain.HabitStatsTest` to verify bitmask scheduling, streak calculations, 7-day statuses, and completion rates.

---

## 📱 Screenshots

| Splash | Login | Home |
|---|---|---|
| ![Splash](screenshots/splash.png) | ![Login](screenshots/login.png) | ![Home](screenshots/home.png) |

| Activity Logger | Habits | AI Coach |
|---|---|---|
| ![Logger](screenshots/logger.png) | ![Habits](screenshots/habits.png) | ![Coach](screenshots/coach.png) |

| Profile | Language Picker | Terms & Conditions |
|---|---|---|
| ![Profile](screenshots/profile.png) | ![Language](screenshots/language.png) | ![TC](screenshots/terms.png) |

---

## 🏗️ App Architecture

PulseFit follows **MVVM (Model-View-ViewModel)** with a **Repository pattern** for data access:

```
┌────────────────────────────────────────┐
│      Jetpack Compose UI (Screens)      │
└───────────────┬────────────────────────┘
                │
┌───────────────▼────────────────────────┐
│ ViewModels (AuthVM, HomeVM, HabitsVM)  │
└───────────────┬────────────────────────┘
                │
┌───────────────▼────────────────────────┐
│           Repository Layer             │
│   - AuthRepository                     │
│   - PulseFitRepository                 │
└───────┬──────────────────────┬─────────┘
        │                      │
┌───────▼──────┐        ┌──────▼──────────┐
│    RoomDB    │        │   Retrofit +    │
│  (offline)   │        │    REST API     │
└──────────────┘        └─────────────────┘
```

---

## 🛠️ Tech Stack

### Android (Client)
| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.8.2 |
| State | ViewModel + StateFlow |
| Local DB | RoomDB 2.6.1 (SQL Migration 1→2) |
| Networking | Retrofit 2.11 + OkHttp |
| Auth | Firebase Authentication |
| SSO | Credential Manager 1.2.2 |
| Async | Coroutines 1.8.1 |
| Build | AGP 8.5.2, Gradle 9.1 |

---

## Youtube link
https://youtube.com/shorts/9sJyFXCdrxQ?si=6vVcoorsC-px4eon

PulseFit includes unit tests for domain engines, ViewModels, and UI components.

### Run All Unit Tests
```bash
./gradlew testDebugUnitTest assembleDebug
```

Covers:
- `CoachTipsTest` — 50 tips per activity type, deterministic tip of the day, random tip count & uniqueness
- `HabitStatsTest` — Weekday bitmask scheduling, streak calculation with skipped unscheduled days, 7-day status mapping, completion rates
- `CoachEngineTest` — AI Coach insights, fatigue detection, training load, ramp-up warnings
- `GamificationEngineTest` — XP, Tiers, Streaks, Streak Shields, Badges including Habit Hero
- `WeeklyStatsTest` — Weekly progress, goal clamping, 7-day bar chart bucketing
- `AuthViewModelTest`, `ActivityViewModelTest`, `SettingsViewModelTest`
