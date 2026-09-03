# Project Specification: DevDeck (Kotlin Multiplatform Developer Portfolio)

## 1. Project Overview

DevDeck is a modern, interactive, cross-platform developer portfolio built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. It targets **Android, iOS, and Web (Wasm/JS)** from a unified codebase. The app showcases the developer's technical skill set, startup experience, live applications, and professional background.

## 2. Target Platforms & Source Sets

* **Common Logic & UI:** `composeApp/src/commonMain` (Shared Compose UI, ViewModels, Business Logic, and Data Models)
* **Android Target:** `composeApp/src/androidMain`
* **iOS Target:** `composeApp/src/iosMain`
* **Web Target:** `composeApp/src/wasmJsMain` (or `jsMain`)

## 3. Tech Stack & Architecture

* **Language:** Idiomatic Kotlin
* **UI Framework:** Compose Multiplatform (CMP) for shared UI across Android, iOS, and Web
* **Architecture Pattern:** Clean Architecture (Presentation, Domain, Data) + MVVM
* **State Management:** StateFlow / SharedFlow with Kotlin Coroutines
* **Navigation:** Navigation Compose Multiplatform
* **Design System:** Material 3 (with dynamic Light/Dark mode support)

## 4. Key App Sections & Content Scope

### A. Hero / Profile Header

* Developer Name, Title (*Strong Junior / Junior+ Android & KMP Engineer*)
* Bio highlighting 6 months of startup execution, production Play Store delivery, and cross-platform capability.

### B. Technical Skills Inventory

Categorized display of technical capabilities:

1. **Architecture & System Design:** Multi-Module Architecture, Clean Architecture, MVVM, Code Reusability.
2. **Core Mobile & Network:** Native Android SDK, Idiomatic Kotlin, Retrofit/Ktor API Integration, Web Scraping & Ingestion.
3. **Cross-Platform & Modern Stack:** Kotlin Multiplatform (KMP), Compose Multiplatform (CMP), Jetpack Compose.
4. **Lifecycle & DevOps:** Google Play Console Release Management (Internal, Testing, Production), Firebase App Distribution, SDLC.

### C. Experience & Production Impact

* Timeline showing startup experience as a Core Mobile Contributor.
* Highlights: Multi-module setups, KMP shared modules, and Play Store release pipelines.

### D. Featured Projects Showcase

* Cards displaying public projects, live Play Store links, and GitHub repositories.
* Includes key tech badges (e.g., `KMP`, `Jetpack Compose`, `Retrofit`, `Clean Architecture`).

### E. Socials & Contact

* Interactive links to LinkedIn, GitHub, Email, and Google Play developer profile.

## 5. Coding & Development Rules for AI

1. **Strict Platform Agnosticism in `commonMain`:** Do NOT import `android.*` or platform-specific dependencies inside `commonMain`. Use CMP abstractions or `expect`/`actual` declarations if platform-specific code is required.
2. **Clean Code & Decoupling:** Keep UI Composables separate from state management logic. Use ViewModels to expose state via `StateFlow`.
3. **Material 3 Design:** Use Material 3 components, clean spacing tokens, and responsive layouts suitable for mobile (Android/iOS) and desktop/web screens.
