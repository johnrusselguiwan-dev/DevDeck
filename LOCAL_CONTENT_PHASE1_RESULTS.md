# Local content refactor: Phase 1 results

Date: 2026-10-12. Historical Phase 1 evidence: implemented and verified on
Android/Wasm. The user subsequently authorized Phase 2; current production state
and verification are recorded in `LOCAL_CONTENT_PHASE2_RESULTS.md`.

## Scope and preservation

Work stays on `feat/ui-and-theme-refactor`. Before editing, all 103 existing
tracked/untracked files were copied with hashes to
`/tmp/devdeck-local-content-baseline/files/`. The baseline inspection confirmed
the current branch is six commits ahead of local `development`; no branches,
commits, pushes, stashes, or merges were made.

The temporary snapshot-capture test constructed the portfolio through the
existing repository/use case and serialized all fields with defaults enabled.
It was removed after capture. The bundled file contains the exact profile,
12 skills, 2 experiences, 5 projects, and 4 contacts, preserving strings, IDs,
order, flags, and null links. Its SHA-256 is
`851a3026859b70de49c709dbff2a37be37cd5a753dc86baf94f5461e1a8de3ab`.
Counts are migration evidence, not runtime validation limits.

The new loader has strict JSON/UTF-8 decoding, required-field enforcement,
content/URL validation, cancellation checks, and a byte-reader test seam.
`PortfolioData` moved to the model package. Serialization runtime 1.10.0 is
pinned; its compiler plugin uses the existing Kotlin 2.3.20 version. The loader
is not wired into DI or production. Existing Kotlin constructor defaults remain
until Phase 2 so callers stay compatible.

## Baseline

Executed before source edits:

```bash
./gradlew :shared:tasks --all :androidApp:tasks --all
./gradlew :shared:testDebugUnitTest :shared:wasmJsBrowserTest \
  :androidApp:assembleDebug :shared:wasmJsBrowserDistribution \
  :androidApp:lintDebug :shared:lintDebug :androidApp:detekt :shared:detekt --continue
```

Task discovery passed. Android and Wasm each passed 11 regression tests; both
builds and both lint checks passed. Android Detekt passed. Shared Detekt failed
with 183 pre-existing style/naming findings. The combined command therefore
exited nonzero; this is not described as an overall passing check.

## Phase 1 checks

Focused Android run: 25 tests passed, including 14 new tests covering strict
decoding, missing fields, content integrity, optional links, read errors, UTF-8,
and cancellation. Newly introduced test formatting findings were corrected.

Platform verification command:

```bash
./gradlew :shared:testDebugUnitTest :shared:wasmJsBrowserTest \
  :shared:connectedDebugAndroidTest :androidApp:assembleDebug \
  :shared:wasmJsBrowserDistribution :androidApp:lintDebug :shared:lintDebug \
  :shared:detekt --continue
```

Both builds and lint checks passed. The Android packaged-read parity test passed
on TECNO LH8n (Android 14). The app APK, instrumentation APK, and Wasm production
distribution all contain byte-identical JSON at the generated resource namespace.

The first browser parity test failed with a 404 because Karma did not serve
the generated main Compose assets. A test-only mapping in
`shared/karma.config.d/compose-resources.js` serves those resources; production
hosting and entry points are unchanged. Two new test formatting findings were
also corrected. Final verification:

```bash
./gradlew :shared:testDebugUnitTest :shared:wasmJsBrowserTest :shared:detekt --continue
```

| Check | Result |
| --- | --- |
| Android unit tests | 25 passed: 11 existing regressions plus 14 loader/decoder tests |
| Wasm browser tests | 26 passed: same 25 tests plus actual bundled-read parity |
| Android instrumented packaged read | 1 passed on the connected Android 14 device |
| Android debug APK | Passed; JSON bytes match the captured snapshot |
| Wasm production distribution | Passed; JSON bytes match the captured snapshot |
| Android/shared lint | Passed |
| Shared Detekt | 183 findings, identical to baseline by path/rule/message; zero new findings |
| iOS compile/runtime/resource reads | Unverified: no macOS/Xcode on this host |

The final combined verification exits nonzero only because of the pre-existing
shared Detekt findings. Android Detekt passed at baseline; Android source did
not change in this phase. Graphify was refreshed after source changes.

Android instrumented and Wasm resource tests compare every field in the actual
bundled snapshot against the existing production repository. The Android test
uses the connected device and generated Compose resource reader, not a JVM
classpath substitute. iOS compilation/resource reads are unverified: this host
is Linux without macOS/Xcode.

## Review boundary

Android/Wasm validation gates passed; iOS remains explicitly unverified. Review
the scoped changes and decide whether iOS evidence is required before approving
the production switch. Phase 2 will migrate the
repository contract/DI, remove legacy production data/defaults, preserve current
ViewModel behavior, and retire temporary legacy parity assertions. A missing or
blocked required check is pending, not a pass.
