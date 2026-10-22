# DevDeck

John Russel A. Guiwan’s developer portfolio, built with Kotlin Multiplatform and Compose Multiplatform. Available on the web and Android, with an iOS target in the source tree.

[View portfolio](https://johnrusselguiwan.web.app) · [Download Android v1.0](https://github.com/johnrusselguiwan-dev/DevDeck/releases/tag/v1.0) · [LinkedIn](https://www.linkedin.com/in/john-russel-guiwan-202391183/)

## Features

- About, Experience, Projects, Skills and Contact in one scrolling feed.
- Responsive phone and desktop layouts with section navigation.
- System, light and dark themes with an iOS-inspired monochrome design.
- Fully visible biographies, project descriptions, experience highlights and skill descriptions.
- Optional skill-category filters and direct project/contact links.
- Bundled JSON content, with no backend or sign-in required.
- Downloadable one-page Android resume and a readable HTML portfolio generated from the same content.
- Separate education details and project contribution lists with published/internal-testing status.

## Projects

I’m a Junior Android Developer at HeroApps Software Development Services, based in Camarines Norte, Philippines. The portfolio describes my contributions within team projects:

| Project | Status | My contributions |
| --- | --- | --- |
| [MLBB Game Guide](https://play.google.com/store/apps/details?id=com.heroapps.mlbbguide) | Flagship; published Android app | Firebase login/sign-up, UI redesign contributions and a patch-note scraper |
| [LoL Game Guide](https://play.google.com/store/apps/details?id=com.heroapps.lolguide) | Published Android app | Champion browsing, search, filters and details; data scripts; purchases and ads; Play Console testing through production |
| EarnPH | Internal testing; not publicly released | Phone OTP, GPS/geofencing, QR check-in, REST API integration, push notifications and Crashlytics |

DevDeck completes the main Kotlin/Compose project cards. A compact Other languages & tools grid includes [CN-Karigusan](https://github.com/johnrusselguiwan-dev/CN_Karigusan_Last), [LoL data scripts](https://github.com/johnrusselguiwan-dev/lolguidescript), the [MLBB patch-note scraper](https://github.com/johnrusselguiwan-dev/mlbbguidepatchnotescript), the award-winning [Museo Bulawan MIS capstone](https://github.com/johnrusselguiwan-dev/Museo-Bulawan-MIS), [MangaMarket](https://github.com/johnrusselguiwan-dev/MangaMarket) and a Certificate Maker System. I’m continuing to learn KMP/CMP through EarnPH and DevDeck.

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
