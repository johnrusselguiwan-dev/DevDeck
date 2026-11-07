# DevDeck

John Russel A. Guiwan’s developer portfolio, built with Kotlin Multiplatform and Compose Multiplatform. Available on the web and Android, with an iOS target in the source tree.

[View portfolio](https://johnrusselguiwan.web.app) · [Download Android v1.0](https://github.com/johnrusselguiwan-dev/DevDeck/releases/tag/v1.0) · [LinkedIn](https://www.linkedin.com/in/john-russel-guiwan-202391183/)

## Features

- About, Experience, Projects, Skills and Contact in one scrolling feed.
- Responsive phone and desktop layouts with section navigation.
- Remembered system, light and dark themes with an iOS-inspired monochrome design.
- Fully visible biographies, project descriptions, experience highlights and skill descriptions.
- Optional skill-category filters, direct project/contact links and portfolio sharing.
- Bundled JSON content, with no backend or sign-in required.
- Downloadable one-page Android resume and a readable HTML portfolio generated from the same content.
- Separate education details and project contribution lists with published/internal-testing status.

## Projects

I’m an Android Developer Trainee at HeroApps Software Development Services, based in Camarines Norte, Philippines. My OJT / Android Developer Intern engagement ran from February 12 to May 8, 2026; I continued as a trainee from May 9, 2026. The portfolio describes scoped contributions within existing team codebases:

| Project | Status | My contributions |
| --- | --- | --- |
| [MLBB Game Guide](https://play.google.com/store/apps/details?id=com.heroapps.mlbbguide) | Published team Android app | Firebase sign-in/sign-up, Figma-guided Compose UI and localization contributions; patch-note tooling |
| [LoL Game Guide](https://play.google.com/store/apps/details?id=com.heroapps.lolguide) | Published team Android app | Browsing/search/details and scoped Hilt bindings; created one-time purchase products and integrated billing/ads using existing components; personally handled Play Console testing through production |
| EarnPH | Internal testing; not publicly released | Shared Kotlin event joining, client REST APIs, existing KScan-based QR validation and client-side polygon ray casting; extended inherited OTP flows and Crashlytics reporting; Android FCM/client notifications |

DevDeck completes the main Kotlin/Compose project cards, with a live website and an Android APK distributed through GitHub Releases. A compact Other languages & tools grid includes [CN-Karigusan](https://github.com/johnrusselguiwan-dev/CN_Karigusan_Last), [LoL data scripts](https://github.com/johnrusselguiwan-dev/lolguidescript), the [MLBB patch-note scraper](https://github.com/johnrusselguiwan-dev/mlbbguidepatchnotescript), the team [Museo Bulawan MIS capstone](https://github.com/johnrusselguiwan-dev/Museo-Bulawan-MIS) and a Certificate Maker System. I’m continuing to learn KMP/CMP; I have not personally tested iOS.

I work in a ticket-driven team process with sprint planning, weekly coordination, dedicated feature branches, PM/QA feedback and pull-request review. My development environment includes Linux Mint, Android Studio, Git and Gradle. I use Gemini as my primary AI assistant and recently added Codex; these are development aids, not claims of independent authorship or automatically validated code.

## Development

DevDeck shares Compose UI, ViewModel state, domain models and bundled content across platform hosts. Its dependencies include Koin, Coroutines/StateFlow, Material 3 and kotlinx.serialization.

| Module | Purpose |
| --- | --- |
| `shared/` | Shared UI, domain, data, resources and platform adapters |
| `androidApp/` | Android application host |
| `iosApp/` | Xcode/SwiftUI host |
| `shared/src/wasmJsMain/` | Browser Wasm entry point and HTML shell |

Use JDK 21 and the checked-in Gradle wrapper. Android requires the Android SDK. iOS builds require macOS and Xcode; an iOS release and runtime verification are not confirmed.

```bash
# Android development debug APK
./gradlew :androidApp:assembleDevDebug

# Local browser development server
./gradlew :shared:wasmJsBrowserDevelopmentRun

# Lint, Detekt, unit tests and devDebug assembly
bash scripts/check.sh

# Full CI gate: all four Android variants and web
bash scripts/check.sh --all-variants --browser
```

See [development instructions](docs/DEVELOPMENT.md) for setup, content editing, architecture and platform builds, and [quality checks](QUALITY_CHECKS.md) for test coverage and limitations.

## Contact

[Email](mailto:russelguiwan@gmail.com) · [LinkedIn](https://www.linkedin.com/in/john-russel-guiwan-202391183/) · [GitHub](https://github.com/johnrusselguiwan-dev)
