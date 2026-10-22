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

## Android variants

Android builds do not use the Google Services plugin or a Firebase SDK and do
not require `google-services.json`. Firebase Hosting remains independent of
the Android application.

Use `:androidApp:assembleDevDebug` for the development app
(`com.example.devdeck.dev`) and `:androidApp:assembleProdRelease` for a production
release (`com.example.devdeck`). Both flavors have debug and release variants;
development and production can be installed together. APKs are generated under
`androidApp/build/outputs/apk/<flavor>/<buildType>/`.

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
