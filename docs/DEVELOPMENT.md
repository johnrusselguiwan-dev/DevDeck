# Developing DevDeck

## Requirements

- JDK 21 and the checked-in Gradle wrapper. The daemon criteria accept any installed JDK 21 vendor, matching CI.
- Node.js 20+ for the generated web text version and web validation scripts.
- Android SDK platform 36 for Android builds; minimum supported Android API is 24.
- A browser with Wasm support for the web app. Headless browser tests require Chrome or `CHROME_BIN`.
- macOS and Xcode for iOS builds and simulator tests.

Wasm incremental compilation is explicitly disabled in `gradle.properties` after an IR metadata failure with reused compiler state. A fresh compile with it disabled passed.

The version catalog is `gradle/libs.versions.toml`. The project uses Kotlin 2.3.20, Compose Multiplatform 1.9.3 and Gradle 9.1.0. Follow the checked-in build configuration when updating tooling.

## Build and run

Run commands from the repository root:

```bash
# Build or install the Android development debug application
./gradlew :androidApp:assembleDevDebug
./gradlew :androidApp:installDevDebug

# Start the browser development server
./gradlew :shared:wasmJsBrowserDevelopmentRun

# Build the static browser distribution
./gradlew :shared:wasmJsBrowserDistribution
```

Android uses an `env` flavor dimension combined with the standard `debug` and `release` build types:

| Variant | Application ID | Version name | Assemble task |
| --- | --- | --- | --- |
| `devDebug` | `com.example.devdeck.dev` | `1.0-dev` | `:androidApp:assembleDevDebug` |
| `devRelease` | `com.example.devdeck.dev` | `1.0-dev` | `:androidApp:assembleDevRelease` |
| `prodDebug` | `com.example.devdeck` | `1.0` | `:androidApp:assembleProdDebug` |
| `prodRelease` | `com.example.devdeck` | `1.0` | `:androidApp:assembleProdRelease` |

Development and production can be installed together. Debug/release builds within the same flavor share an application ID; they are alternate builds of the same app. Select the variant in Android Studio's Build Variants panel. `assembleDebug` and `assembleRelease` now assemble both flavors; use explicit variant tasks for a single APK. The shared KMP library retains `debug`/`release` variants because both environments currently use the same bundled content and runtime behavior. iOS and Wasm do not acquire Android flavors.

APKs are under `androidApp/build/outputs/apk/<flavor>/<buildType>/`. For example, `devDebug` produces `androidApp/build/outputs/apk/dev/debug/androidApp-dev-debug.apk`. Static browser files are generated in `shared/build/dist/wasmJs/productionExecutable/`; serve the complete directory over HTTP rather than opening the HTML file directly.

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and select a supported simulator or device. iOS compilation and runtime checks require macOS/Xcode and have not been verified in the Linux environment used for the current changes.

## Portfolio content

Edit `shared/src/commonMain/composeResources/files/portfolio.json`. It contains the profile, skills, experience, projects and contact links used by the shared app.

- Keep IDs unique within each collection and preserve required fields.
- Skill categories must match `SkillCategory` in `PortfolioModels.kt`.
- Use `null` for unavailable project links; their action buttons stay hidden. One action fills the card width; multiple actions share the row equally.
- Project order follows the JSON. `isFeatured: true` entries use the main cards, with the first spanning the content width; other entries appear in the compact Other languages & tools grid. Reserve the main cards for Kotlin/Compose mobile projects; the supporting cards show project names, languages/tools and available source links. Keep MLBB first, followed by LoL and EarnPH.
- Include confirmed personal contributions and accurate project release status. Distinguish team functionality from individual work. Optional project `contributions` entries appear under “My contribution” on featured cards; keep the description focused on purpose and context.
- Mark education entries with `isEducation: true` for the HTML text version. The shared app displays employment and education in one connected timeline under “Experience & Education”. Employment entries default to `false`.
- Biographies, project descriptions, skill descriptions and all experience highlights are visible directly. Skill filters are optional.
- `profile.resumeUrl` is an optional web URL. The current URL points to `resume/John_Russel_Guiwan_Resume.pdf` on the portfolio website. Resume buttons appear in the desktop sidebar and phone profile, with an additional link in Contact. The PDF and its editable HTML source are in `shared/src/wasmJsMain/resources/resume/`. Keep resume facts aligned with the portfolio content and update the PDF after editing its HTML. Set the URL to `null` to hide profile buttons and show the non-interactive Contact placeholder.
- `profile.androidDownloadUrl` is an optional APK download or release-page URL. The current value points to the v1.0 GitHub release page, where visitors can download the APK.
- `profile.websiteUrl` is the optional portfolio website URL. The contact card shows the Android download only on the web; native Android/iOS apps show the website link instead. This uses the runtime platform, so mobile browsers still show the Android download. All resource URLs are validated before display.

`BundledPortfolioLoader` reads the JSON through Compose resources, strictly decodes UTF-8, validates fields and URLs, and propagates errors and cancellation. The repository emits one validated snapshot per collection. Rebuild and redeploy the web distribution or release a new Android APK to deliver content changes; editing the JSON does not update an existing published release.

## Web text version and resume

The interactive web app remains at `index.html`. Its native HTML resource bar links to the text version, PDF resume and email even if Wasm cannot start. Compose renders inside `#portfolio-app` below that bar.

`generatePortfolioOverview` runs `scripts/generate-portfolio-overview.mjs` with Node.js 20+ before Wasm resources are processed. It generates `overview.html` from the same bundled JSON into `shared/build/generated/portfolioOverview/`; do not edit the generated page. The semantic HTML page includes experience, project contributions, skills, education and contact links without JavaScript. Own-site resource links use paths so local previews can open the bundled PDF. Kotlin app links use the configured public HTTPS URL, so that resume destination becomes available after deploying the new web distribution.

To check generation without compiling Kotlin:

```bash
node --test scripts/portfolio-overview.test.mjs
node scripts/generate-portfolio-overview.mjs /tmp/devdeck-overview.html
```

The PDF must have selectable text and fit one page with 10–11pt body text. Center the name, role and contact block; keep section headings and body content left-aligned. Re-export with LibreOffice and visually inspect it before publication. Text-version/native HTML links do not dispatch the Compose resource-click analytics callback; existing `resume_open` reports cover the shared UI buttons.

## Runtime and architecture

1. Android `MainActivity`, iOS `MainViewController` or the Wasm entry point calls `App`.
2. Koin provides `BundledPortfolioLoader`, `PortfolioRepositoryImpl`, `GetPortfolioDataUseCase` and `HomeViewModel`. Android initializes Koin in `DevDeckApplication`; the other hosts initialize it through `App` when needed.
3. The repository reads one bundled snapshot. There is no remote backend, content cache or database in DevDeck.
4. `HomeViewModel` exposes loading, success and error states through StateFlow. Reload cancels the previous load; theme and skill-filter preferences survive retry in memory.
5. `HomeScreen` renders the shared Compose interface. The visible section controls the navigation selection; navigation scrolls the same feed. About stays selected at the top of the feed, even when Experience is also visible. After scrolling, selection follows the reading position; reaching the bottom selects Contact.

Shared Kotlin is under `shared/src/commonMain/kotlin/com/example/devdeck/`:

| Directory | Responsibility |
| --- | --- |
| `presentation/` | Compose UI, theme and ViewModel state |
| `domain/` | Portfolio models, repository interface and use case |
| `data/` | Bundled JSON loading, validation and repository implementation |
| `di/` | Koin bindings |

The phone layout applies below 600 dp. Wider layouts use a 240–360 dp sidebar and a centered layout up to 1440 dp wide. The profile sidebar is 360 dp on wide desktops and 320 dp at intermediate widths. Project cards use a two-column grid only when their content pane has at least 720 dp. Each pair uses measured content heights so wrapped pills and action buttons fit; pills and links sit at the bottom of the cards. The interface uses neutral light/dark surfaces and grouped skill rows. Skill category cards use two equal columns when the content pane has at least 720 dp and stack below that threshold. Skills within each category stay in one vertical list; selecting a category shows that card at full width. The profile header and sidebar use icon buttons for GitHub, LinkedIn, email and phone. Contact groups GitHub, LinkedIn, email and phone as rows with icons, titles and destination URLs/addresses. Clickable rows include an arrow; phone opens the dialer. Location and an unconfigured resume remain non-interactive. A configured resume uses the same clickable row and arrow. The contact card displays location details, the resume, and a link to the other version of the portfolio (Android from the web; website from native apps). Contact fills the same content width as the sections above it; the four contact links and the location/resume/app details sit side by side when the card's inner width reaches 680 dp, and stack below that. On desktop, sidebar social icons slide right and fade out in order from right to left when Contact enters the visible scroll area, then return when it leaves. Fixed icon slots keep sidebar layout stable; exiting links are disabled and removed after the transition. Supporting project cards use three columns at 900 dp of content width, two at 560 dp, and one below that. The appearance control shows the current theme icon and a System/Light/Dark menu.

Platform hosts handle external URLs: Android uses `ACTION_VIEW`, iOS uses `UIApplication.openURL`, and Wasm opens web links with `noopener,noreferrer`. Email and phone links use the platform’s appropriate URL handler. Technologies listed in portfolio project entries describe showcased work; they are not necessarily dependencies of DevDeck.

## Firebase and release signing

Firebase Hosting serves the static Wasm distribution independently of the Android application. DevDeck does not require a Firebase backend or a web Firebase SDK.

The Android app has no Firebase SDK dependency or runtime initialization and does not apply the Google Services plugin. No `google-services.json` is needed for any variant. Firebase Hosting and private web analytics remain independent of the Android build. Local Firebase and signing files remain ignored by Git.

Release signing reads an ignored root `secret.properties` file with `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS` and `RELEASE_KEY_PASSWORD`. The keystore path is relative to the repository root. Both release flavors use that signing configuration when present. Without that file, debug builds use the standard debug key and release APKs are unsigned (`*-release-unsigned.apk`); unsigned CI artifacts are build validation, not installable releases. Signing is never replaced with the debug key for release builds.

`firebase.json` configures the existing Hosting site. Building a distribution does not deploy it. Publication and signed Android releases are separate actions from local validation.

## Validation

```bash
# devDebug lint, unit tests and assembly; shared debug checks and Detekt
bash scripts/check.sh

# CI gate: all Android variants, shared debug/release lint, web and analytics
bash scripts/check.sh --all-variants --browser

# Add web checks to the default development gate
bash scripts/check.sh --browser

# Read bundled content through production DI on an Android emulator/device
./gradlew :shared:connectedDebugAndroidTest

# On macOS/Xcode only
bash scripts/check.sh --ios
```

See [QUALITY_CHECKS.md](../QUALITY_CHECKS.md) for the quality gate and existing Detekt debt. Build success does not replace visual checks: review both themes, phone/desktop layouts, section navigation, skill filters and external links after UI changes.

## Private web analytics

The existing Firebase web app has Google Analytics measurement ID `G-X3ECWQBXT6`. `shared/src/wasmJsMain/resources/analytics.js` loads the Google tag on the existing live Firebase Hosting domains. It does not load Analytics on localhost, unknown domains, or when the browser signals Global Privacy Control or Do Not Track. Advertising signals are disabled, and page query strings and referrers are excluded from the configured page view.

The web host receives typed resource-click callbacks from the shared UI. Native Android/iOS hosts keep their existing URL handling and do not send these web analytics events. No public statistics panel is shown.

| Measurement | Source | Meaning |
| --- | --- | --- |
| Page views and users | Google Analytics in the existing Firebase project | Website visits and browser-based user metrics from the deployed site |
| `apk_download_click` | Android resource action | Clicks on the portfolio's APK link; not installs or proof that a download completed |
| `resume_open` | Configured resume resource action | Clicks to open the resume link; unavailable while `resumeUrl` is `null` |
| APK asset downloads | GitHub release asset `download_count` | GitHub's download total, including downloads reached outside the portfolio; not unique people or installs |

View the web events privately in [Firebase Analytics](https://console.firebase.google.com/project/devdeck-607ea/analytics). Event collection starts after deploying the updated web build; report ingestion must be checked there after publication. Resource events send only their event type and measurement destination, not phone numbers, email addresses or resume URLs.

Run the analytics privacy/dispatch checks with Node.js 20 or later:

```bash
node --test scripts/analytics.test.mjs
```

To inspect the existing APK's separate GitHub download total:

```bash
gh api repos/johnrusselguiwan-dev/DevDeck/releases/tags/v1.0 \
  --jq '.assets[] | select(.name | endswith(".apk")) | {name, download_count}'
```
