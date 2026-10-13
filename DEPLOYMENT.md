# DevDeck deployment setup

## Browser

The production browser app is served at https://johnrusselguiwan.web.app/.
`.firebaserc` selects Firebase project `devdeck-607ea`; `firebase.json` selects
Hosting site `johnrusselguiwan` within that project. The original default site
remains available. Hosting only serves the generated Wasm distribution and
does not require a Firebase client SDK, Analytics, Auth, or Firestore.

Build and publish from the repository root:

```sh
./gradlew :shared:wasmJsBrowserDistribution
firebase login
firebase deploy --only hosting --project devdeck-607ea
```

Login is only needed if the CLI is not already authenticated. Install the
Firebase CLI through its official installation instructions if unavailable.
The deploy command publishes publicly; inspect the distribution first.
Hosting excludes source maps, hidden files, and node_modules. HTML, JavaScript,
and portfolio JSON use `Cache-Control: no-cache`. There is no SPA fallback:
missing assets return 404.

## Android Firebase configuration

Download `google-services.json` for package `com.example.devdeck` from Firebase
Project settings and place it in `androidApp/`. The file is ignored by Git.
Google Services Gradle plugin 4.5.0 processes it when present; builds without
this local file skip the plugin. No Firebase product SDK or telemetry is added.
If a future feature requires Firebase at runtime, make its config requirement
explicit rather than relying on this optional setup.

Firebase client IDs and client API keys are intended to be public when properly
restricted; they do not replace API restrictions, IAM, or data security rules.
Those cloud protections have not been audited by this change.

## Android signing

Release signing reads local root `secret.properties` with these property names:

```properties
RELEASE_STORE_FILE=<path relative to the repository root>
RELEASE_STORE_PASSWORD=<local password>
RELEASE_KEY_ALIAS=<local alias>
RELEASE_KEY_PASSWORD=<local password>
```

Do not commit real values or the keystore. Signing is configured when the file
exists; incomplete properties or a missing keystore fail configuration.
Without it, debug builds work and release output is unsigned. Verify a release
APK's signature before publishing. Updates must use the same signing identity
and a higher versionCode. Back up signing material privately.

Ignore rules also cover environment files, service-account JSON files, common
private-key formats, Firebase local state/logs, and generated release outputs.
Ignore rules do not erase credentials already committed or restrict cloud access.

The signed Android 1.0 APK remains available from the
[GitHub release](https://github.com/johnrusselguiwan-dev/DevDeck/releases/tag/v1.0).
The published tag is preserved; these PRs do not rewrite release history.
