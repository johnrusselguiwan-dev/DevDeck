<div align="center">

# DevDeck — Multiplatform Developer Showcase

**An interactive, cross-platform portfolio application built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP) — running on Android, iOS, and Web.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-KMP-purple?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-CMP-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Android-Native_SDK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Design](https://img.shields.io/badge/Design-iOS_Monochrome_Dark-1E1E2E?style=for-the-badge)](https://developer.apple.com/design/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)](#how-to-build--run)

<p align="center">
  <a href="#how-to-build--run"><strong>🚀 Live Web Demo</strong></a> •
  <a href="#featured-projects--production-experience"><strong>📱 Google Play Store</strong></a> •
  <a href="https://www.linkedin.com/in/john-russel-guiwan-4188b8352/"><strong>💼 LinkedIn</strong></a> •
  <a href="https://github.com/johnrusselguiwan-dev"><strong>📄 Resume</strong></a>
</p>

</div>

---

## 💡 Project Highlights (Why This Exists)

**DevDeck** is an architectural reference and portfolio application engineered to demonstrate production-grade Kotlin Multiplatform capabilities. Rather than building separate native applications or using web wrappers, DevDeck achieves high code reusability across platforms:

* 📱 **Single-Source Declarative UI Across Mobile & Web**: 100% of the UI design system, state management, and business logic are shared across Android, iOS, and Web (Wasm) inside `commonMain` using **Compose Multiplatform**.
* 🏛️ **Production-Grade Clean Architecture & UDF**: Built with explicit separation of concerns into **Presentation**, **Domain**, and **Data** layers, leveraging `StateFlow`, `Kotlin Coroutines`, and `Koin` for dependency injection.
* 🛡️ **Zero Platform-Specific UI Leaks**: Strictly agnostic shared logic inside `commonMain` with clean `expect`/`actual` platform handlers (e.g., native URL execution on iOS via `UIApplication` and Android via `Intent`).

---

## 📸 App Previews & Screenshots

> *Design Theme: iOS-inspired minimalist monochrome UI (dark/zinc system styling, rounded containers, high contrast typography, and interactive tactile micro-feedback).*

| Web (Wasm / Desktop Browser) | Android (Native Mobile) | iOS (SwiftUI Host) |
| :---: | :---: | :---: |
| <img src="https://via.placeholder.com/600x380/121212/FFFFFF?text=DevDeck+Web+Wasm+Preview" width="100%" alt="Web Preview"/> | <img src="https://via.placeholder.com/300x600/121212/FFFFFF?text=Android+App+Preview" width="100%" alt="Android Preview"/> | <img src="https://via.placeholder.com/300x600/121212/FFFFFF?text=iOS+App+Preview" width="100%" alt="iOS Preview"/> |

---

## 🏛️ Architectural Overview

DevDeck follows **Clean Architecture** principles coupled with **Unidirectional Data Flow (UDF)**:

```
                          ┌───────────────────────────┐
                          │   Presentation Layer      │
                          │ (Compose UI + ViewModels) │
                          └─────────────┬─────────────┘
                                        │ (State / Intent)
                                        ▼
                          ┌───────────────────────────┐
                          │       Domain Layer        │
                          │  (UseCases + Data Models) │
                          └─────────────┬─────────────┘
                                        │
                                        ▼
                          ┌───────────────────────────┐
                          │        Data Layer         │
                          │(Repository Implementations│
                          │   & Data Source Specs)    │
                          └───────────────────────────┘
```

### Module Structure

```
DevDeck/
├── androidApp/               # Native Android Application entry point & manifest
├── iosApp/                   # Xcode iOS project & SwiftUI wrapper entry point
└── shared/                   # Core Kotlin Multiplatform module
    └── src/
        ├── commonMain/       # Shared Compose UI, ViewModels, UseCases, Repositories, & Models
        ├── androidMain/      # Android platform actuals & lifecycle bindings
        ├── iosMain/          # iOS ComposeUIViewController bridge & native URL launcher
        └── wasmJsMain/       # Web (Wasm) entry point & Webpack configuration
```

---

## 🛠️ Technical Skills Matrix

| Category | Skills & Technologies |
| :--- | :--- |
| **Architecture & Patterns** | Multi-Module Architecture, Clean Architecture (Domain/Data/Presentation), MVVM, Unidirectional Data Flow (UDF), Dependency Injection (Koin) |
| **Core Mobile & Multiplatform** | Idiomatic Kotlin, Kotlin Multiplatform (KMP), Compose Multiplatform (CMP), Jetpack Compose, Native Android SDK, SwiftUI Integration |
| **Networking & Data** | Retrofit, Ktor Client, OkHttp, RESTful API Integration, Web Scraping / Data Ingestion, Local Cache & Persistence |
| **Concurrency & Reactive** | Kotlin Coroutines, StateFlow, SharedFlow, Reactive Streams |
| **DevOps & Production** | Google Play Console Release Pipelines (Internal, Closed, Open, Production Tracks), Firebase App Distribution, Gradle Version Catalogs, Git Workflows |

---

## 💼 Featured Projects & Production Experience

### 🚀 Production Startup Execution (Core Mobile Contributor)
* **Tenure & Growth**: Accelerated from trainee to core mobile contributor within **6 months of intense startup execution**.
* **Impact & Delivery**:
  * Shipped **2 live production apps to the Google Play Store**.
  * Engineered scalable multi-module Android architectures and KMP shared libraries.
  * Implemented RESTful API integration, web scraping pipelines, and robust data caching.
  * Managed automated release pipelines on Google Play Console (internal testing through production deployment).
* *Note: Production application codebases are proprietary company IP. **DevDeck** serves as my public, open-source architectural reference project demonstrating these identical production standards.*

### ⚡ DevDeck — Cross-Platform Developer Portfolio (This Repository)
* **Scope**: Open-source cross-platform application showcasing real-world Kotlin Multiplatform architecture.
* **Tech Stack**: KMP, Compose Multiplatform, Koin, Coroutines StateFlow, Material 3 Monochrome Theme, WasmJS & iOS targets.

---

## 🚀 How to Build & Run

### Prerequisites
* **Android Studio** (Ladybug or newer recommended) with KMP plugin installed.
* **JDK 17+** configured in environment.
* **Xcode 15+** (for building the iOS application target on macOS).

### 📱 Android Application
To build and run the Android debug APK:
```bash
./gradlew :androidApp:assembleDebug
```
To install directly on a connected device/emulator:
```bash
./gradlew :androidApp:installDebug
```

### 🌐 Web Application (Wasm)
To launch the interactive WebAssembly development server:
```bash
./gradlew :shared:wasmJsBrowserDevelopmentRun
```
Access the running web app at `http://localhost:8080` in any modern WebAssembly-compatible browser.

### 🍎 iOS Application
1. Open `iosApp/iosApp.xcodeproj` in **Xcode**.
2. Select your target simulator (e.g., iPhone 15 Pro) or connected iOS device.
3. Press **Cmd + R** to compile and run.

---

## 📬 Contact & Connect

* **Developer**: John Russel Guiwan
* **Role**: Junior+ / Associate Android & Kotlin Multiplatform (KMP) Engineer
* **LinkedIn**: [linkedin.com/in/john-russel-guiwan-4188b8352](https://www.linkedin.com/in/john-russel-guiwan-4188b8352/)
* **GitHub**: [@johnrusselguiwan-dev](https://github.com/johnrusselguiwan-dev)
* **Email**: [russelguiwan@gmail.com](mailto:russelguiwan@gmail.com)

---

<div align="center">
  <sub>Built with ❤️ using <strong>Kotlin Multiplatform</strong> & <strong>Compose Multiplatform</strong></sub>
</div>