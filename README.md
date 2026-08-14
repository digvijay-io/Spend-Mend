# SpendMend 💸

### Modern Android Expense Tracker — Kotlin · Jetpack Compose · AI-Powered Insights

---

## 📋 Overview

SpendMend is a modern Android expense tracking application built using **Kotlin** and **Jetpack Compose**. It automatically tracks expenses by reading bank SMS messages, helps manage monthly budgets, monitors spending habits, and delivers AI-powered financial insights.

The app is designed with a clean, premium **Material 3** interface inspired by Google Wallet, Pixel apps, and modern fintech products — prioritizing minimal design, smooth animations, and a fast, intuitive experience.

> 🚧 **Status**: Actively in development. Core features (auth, SMS import, budgeting) are functional; Analytics, Goals, History, and Settings are being finalized for first release.

---

## ✨ Features

- 🔐 **Secure user authentication** via Firebase
- 📩 **Automatic expense detection** from bank SMS messages
- ✍️ **Manual income and expense** management
- 📊 **Monthly budget tracking** with progress indicators
- 🤖 **AI-powered spending insights** (TensorFlow Lite)
- 🧾 **Transaction history**
- 📈 **Analytics dashboard**
- 🎯 **Savings goals**
- ⚙️ **Personalization & security settings**

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository Pattern + Clean Architecture |
| **Local Database** | Room + SQLCipher (encrypted storage) |
| **Authentication** | Firebase Authentication |
| **Cloud Sync** | Firebase Firestore |
| **Background Tasks** | WorkManager |
| **AI / ML** | TensorFlow Lite (on-device insights) |
| **SMS Parsing** | Android SMS APIs |

---

## 📁 Folder Structure

```
SpendMend/
├── app/
│   ├── src/main/java/.../
│   │   ├── data/              # Room entities, DAOs, repositories
│   │   ├── domain/             # Use cases, business logic
│   │   ├── ui/                 # Jetpack Compose screens & components
│   │   │   ├── auth/
│   │   │   ├── dashboard/
│   │   │   ├── budget/
│   │   │   ├── analytics/
│   │   │   └── settings/
│   │   ├── sms/                 # SMS reading & parsing logic
│   │   ├── workers/              # WorkManager background jobs
│   │   └── di/                    # Dependency injection modules
│   └── src/main/res/               # Resources (themes, strings, drawables)
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## ⚙️ Installation

### Prerequisites
- Android Studio (latest stable)
- JDK 17+
- Android SDK 26+ (min) / 34+ (target)
- A Firebase project (for Authentication + Firestore)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/digvijay-io/Spend-Mend.git
   cd Spend-Mend
   ```

2. **Add Firebase configuration**
   - Create a project on the [Firebase Console](https://console.firebase.google.com/)
   - Enable **Authentication** and **Firestore**
   - Download `google-services.json` and place it in the `app/` directory

3. **Open in Android Studio**
   - Open the project and let Gradle sync

4. **Run the app**
   - Connect a device/emulator (API 26+)
   - Click **Run ▶** or use:
   ```bash
   ./gradlew installDebug
   ```

---

## 📱 Usage

1. Sign up / log in with your account
2. Grant SMS read permission to enable automatic transaction detection
3. Set a monthly budget and category limits
4. View auto-imported transactions on the dashboard, or add entries manually
5. Check the Analytics tab for spending trends and AI-generated insights
6. Set savings goals and track progress over time

---

## 📸 Screenshots

| Home Dashboard | Budget Tracking | Transaction History |
|---|---|---|
| *[placeholder]* | *[placeholder]* | *[placeholder]* |

> Screenshots coming soon — first release build in progress.

---

## 🚀 Roadmap

- AI Financial Insights
- Advanced Expense Analytics
- Cloud Backup & Sync
- PDF/CSV Export
- Biometric Authentication
- Savings Goals
- Dark Mode
- Receipt Scanner

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

License: TBD. All rights reserved for now — a formal license will be added before public release.

---

**Built with ❤️ by [Digvijay Deshmukh](https://github.com/digvijay-io)**
