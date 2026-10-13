# Local content refactor: Phase 2 results

Date: 2026-10-12. Status: implemented and verified on Android/Wasm.
The user authorized proceeding after Phase 1. Work stayed on the existing
`feat/ui-and-theme-refactor` branch, preserving pre-existing tracked/untracked
changes. No branch switch, commit, push, stash, or merge was performed.

## Completed

- The repository exposes one cold `Flow<PortfolioData>`. Each collection reads
  one validated bundled snapshot; failures/cancellation propagate without fallback.
- Koin explicitly registers `BundledPortfolioLoader()` and injects it into the
  repository. The existing use case delegates to the repository.
- Hardcoded production portfolio records and stale profile defaults are removed.
- The ViewModel fake emits complete snapshots, preserving live-update,
  preference, reload cancellation, retry, and cleanup checks.
- New tests cover cold reads, complete emissions, read/decode failures, DI wiring,
  and real loader failures/cancellation through the ViewModel.
- Temporary legacy parity assertions and ID-specific content tests are retired.
  Actual Android/Wasm packaged-resource tests resolve the production use case
  through Koin, compare its output against decoded package bytes, and check
  repository-link structure without freezing portfolio record counts.
- UI, ViewModel, host startup, dependencies, versions, and JSON content are unchanged.

The JSON SHA-256 remains
`851a3026859b70de49c709dbff2a37be37cd5a753dc86baf94f5461e1a8de3ab`,
matching the complete repository-produced snapshot proven during Phase 1.
The packaged APK and Wasm distribution were also checked against these bytes.
There is no second production or test copy of the full legacy portfolio.

## Verification

| Check | Result |
| --- | --- |
| Android common/unit tests | 30 passed |
| Wasm browser tests | 31 passed, including real packaged read through production DI |
| Android packaged-resource/DI test | 1 passed on the read-only Medium_Phone emulator |
| Android debug APK | Passed |
| Wasm production distribution | Passed |
| Android/shared lint | Passed |
| Shared Detekt | 71 remaining findings; none in Phase 2 changed/new code, down from 183 before removal of legacy content |
| Normal browser smoke | HTTP 200 for JSON; profile/projects/contact render; null-link actions hidden; skill filter, light theme, navigation and breakpoint state retention verified |
| iOS compile/runtime/resource read | Unverified: Linux host without macOS/Xcode |
| Android visual smoke | Not performed; packaged read and production DI verified by instrumentation |

Commands:

```bash
./gradlew :shared:testDebugUnitTest :shared:detekt --continue
./gradlew :shared:testDebugUnitTest :shared:wasmJsBrowserTest \
  :shared:connectedDebugAndroidTest :androidApp:assembleDebug \
  :shared:wasmJsBrowserDistribution :androidApp:lintDebug :shared:lintDebug \
  :shared:detekt --continue
./gradlew :shared:connectedDebugAndroidTest
CHROME_BIN=/tmp/devdeck-local-content-phase2/chrome/opt/google/chrome/chrome \
  ./gradlew :shared:wasmJsBrowserTest
graphify update .
git diff --check
```

The initial combined command failed for shared Detekt and two environment issues:
the physical Android device was disconnected and Chrome was unavailable. The
existing Medium_Phone emulator was booted headlessly with `-read-only`; the
Android test then passed. Google's official Chrome package was downloaded and
extracted into a temporary directory, without system installation; the browser
suite then passed. Future runners need an installed Chrome binary or an explicit
`CHROME_BIN` path. No project browser/security configuration was weakened.

The built Wasm app was served locally for smoke testing. Its actual JSON request
returned HTTP 200 at the generated Compose namespace. The sole favicon 404 is
unrelated to content loading. The temporary browser tab, viewport override,
server, and emulator were cleaned up after verification.

## Next actions

Release packaging and local Firebase Hosting checks were subsequently completed.
See `RELEASE_READINESS.md` for evidence, the inactive hosting example, and remaining
provider/Git/iOS decisions. No publication has occurred.

The local content migration is complete for Android/Wasm. Before an iOS release,
compile and exercise resource reads/UI on macOS/Xcode. Before deployment, publish
JS, Wasm, and JSON together and verify the intended hosting base path and cache
headers; do not mark the unversioned JSON URL immutable across content releases.
Review the scoped changes and decide how existing checkout work should be
committed/integrated before requesting a PR to `development`. Remaining Detekt
cleanup is separate from this refactor.
