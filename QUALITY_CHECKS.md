# Quality checks

Run from the repository root (or pass the absolute script path):

```sh
bash scripts/check.sh
bash scripts/check.sh --all-variants --browser
bash scripts/check.sh --browser
bash scripts/check.sh --ios
bash scripts/check.sh --web-only --quiet
```

The default gate runs Python tooling tests, shared/Android Detekt, shared
Android-KMP lint and host tests, and `devDebug` app lint/unit tests/APK assembly.
`--all-variants` adds `prodDebug`, `devRelease` and `prodRelease` lint/assembly;
app-host unit tests run for both debug flavors. The shared library has a single
Android variant, so `:shared:testAndroidHostTest` and `:shared:lintAndroidMain`
run once. Host and device test source sets are explicitly enabled; the full gate compiles
the shared device-test APK and the app journey-test APK without requiring a connected device. App-host unit
test tasks currently have no test sources and also cover future flavor tests.

`--browser` adds the production Wasm distribution, headless Chrome tests and
Node.js checks for analytics, generated HTML, platform preferences/sharing and
startup recovery. The Compose smoke test mounts `App` with an unrelated global
Koin container, uses the real browser preferences bridge to change theme, and
navigates to Contact. Karma loads the production `platform.js` for this test.
Chrome/`CHROME_BIN` and Node.js 20+ are required. The script collects independent
failures and exits nonzero if any check fails. Gradle arguments follow `--`.

The gate uses one worker and separate debug/web and release Gradle invocations.
The shared module uses `com.android.kotlin.multiplatform.library` plus
`com.android.lint`: the standalone lint plugin is required to create its lint
verification task. Shared lint must not be replaced with app lint alone.
App lint keeps dependency analysis enabled. Both modules retain error severities
for `HardcodedText` and `ResourceAsColor` and generate HTML/XML/SARIF reports.
Run focused lint with:

```sh
./gradlew :shared:lintAndroidMain :androidApp:lintProdRelease
```

`--quiet` changes output only: complete logs are retained in a temporary file,
success prints the path, and failures print that path plus the last 60 lines.
`--web-only` runs the Python/Node checks, shared Detekt, Wasm browser tests and
production web build without Android tasks. It cannot be combined with
`--all-variants` or `--ios`. Normal CI remains the full Android/web gate.
The Python checks also test shell command selection, quiet failure reporting,
explicit Hosting destination selection and prevention of uploads after failures;
those routing tests use temporary command stubs and do not deploy.

## Detekt debt

Detekt 1.23.8 and its formatting plugin remain enabled with the existing rules.
Source coverage includes all Kotlin and Kotlin script files under each module’s
`src/` tree, including flavor-specific sources, common, Android, iOS, Wasm and
tests. Generated/build directories are excluded. Reports use repository-relative
paths for portable CI diagnostics. Analysis uses the existing syntax-based gate
without platform compilation/type resolution.
Baseline paths are `config/detekt/baseline-<module>.xml`. The shared baseline
records 32 existing issue signatures after removing resolved entries for deleted template code, unused UI components, the rewritten app root and unused imports. Android currently
has no baseline findings and needs no baseline file.

The baseline does not fix those issues. Newly introduced issue signatures fail
the gate. Remove resolved entries as debt is fixed. Do not regenerate baselines
as part of normal checks or CI, or add new findings merely to make a build pass.
Deliberate baseline updates require reviewing the diff and explaining the debt.

## GitHub Actions

`.github/workflows/quality.yml` runs the full `--all-variants --browser` gate on
PRs targeting `development` or `main`, pushes to those branches, and manual
dispatches. One Linux job uses Java 21, Node.js 22, the runner’s headless Chrome,
pinned official actions, read-only repository permissions, Gradle caching, and
report uploads even after failures. Obsolete runs are canceled and the job has a
30-minute timeout. PR caches are read-only.

All four Android variants build without Firebase or signing secrets. Debug APKs
use the standard debug key; release APKs are unsigned on CI. The quality workflow uploads
reports and does not publish APKs or deploy Firebase Hosting. The separate manual
`release-apk.yml` workflow packages a signed production APK after verifying its
signature, identity, version and non-debuggable manifest; it uploads a workflow
artifact without publishing a GitHub Release. Signing-secret setup is documented
in `docs/DEVELOPMENT.md`. Android device checks are separate. An additional `macos-15` job compiles the
shared iOS simulator framework and unsigned Swift host and runs shared simulator
tests. Local Linux validation cannot establish that job passes. Branch protection requiring this check is a separate
repository setting. Local validation cannot confirm a GitHub-hosted run;
check the pull request's quality status before merging.

## Local verification

On 2026-10-21, the portfolio content/resume/text-version changes passed the full
`--all-variants --browser` gate: 35 shared Android unit tests, 36 Wasm browser
tests and 6 Node.js checks, with all four APKs and the production web distribution
built. Packaged JSON was compared with the source in every APK and the web
bundle; the bundled PDF was checked against its source asset. The PDF was
visually reviewed and its text extracted. Desktop/phone browser layouts, both
app themes, project contributions, education, skill filtering, contact and text
navigation were reviewed locally. No Android device was connected; iOS runtime
checks, publication and hosted CI were not performed for this change.

On 2026-10-20, the full gate passed with 34 shared Android unit tests, 35 Wasm
browser tests and 3 analytics tests. Fresh serial lint analysis passed for all
four app variants and both shared build types. All four APK identities and debug
flags were checked; local signatures verified. A source-only temporary copy
without Firebase/signing files also built all four variants and produced unsigned
release APKs. A temporary `devDebug` wildcard-import probe failed Detekt as
expected and was removed. Actionlint, shell syntax, script option/failure checks,
and `git diff --check` passed. Hosted Actions and iOS/device runs were not executed.

On 2026-11-04, the saved-theme, sharing and release-tooling changes passed the full
`--all-variants --browser` gate: 37 shared Android unit tests and 38 Wasm browser
tests, all four Android APK variants, both shared lint build types, Detekt, the
production web distribution, Node.js checks and four Python packaging tests.
The production web bundle includes the theme/share helper before Wasm. The exact
release-workflow verification step passed locally against the signed production
APK, checking its signature, application ID/version, non-debuggable manifest and
checksum; bundled portfolio content matched the current source. A source-only
copy without credentials passed unsigned release configuration (`--dry-run`),
and the explicit signing guard failed as expected. This is not an unsigned APK
assembly check. Skill files were validated locally and remain ignored by Git.
No Android device was connected; browser UI automation was unavailable. Visual
checks, native share-sheet runtime checks, iOS compilation/runtime and hosted
GitHub workflow execution were not performed. No deployment or publication ran.

On 2026-11-05, architecture fixes passed a clean debug/web gate followed by the
release gate: 38 shared Android host tests, 40 Wasm browser tests (including the
awaited production-app startup/theme/navigation smoke test), 16 Node.js checks
and 13 Python tooling tests. All four Android APKs, the Android device-test APK
and the production web distribution built. Shared Android lint reported no
issues; all four app lint reports had no errors, with existing target/toolchain
and dependency update warnings remaining. Both Detekt reports had no findings.
A temporary API-level violation confirmed shared lint coverage and was removed.
The first release phase exhausted disk space; after removing confirmed generated
test bundles and redundant scratch copies, the same release tasks passed.
The release-workflow packaging, signature, identity/version, non-debuggable
manifest and checksum checks passed against the newly built production APK.
Browser helper files matched the production distribution. The macOS iOS job is
configured but was not run locally; no Android device runtime check, hosted CI,
deployment or publication was performed.

On 2026-11-08, portfolio review fixes removed unused UI and schema fields,
corrected obsolete comments, made project actions stack with flexible heights,
and enabled release code/resource shrinking. The full gate compiled all four
Android variants, the shared device-test APK, the app journey-test APK and the
production web distribution; lint, 38 shared host tests, 16 Node checks and 13
Python tests passed. Its browser phase initially failed on a new layout test.
After correcting the test to check rendered line bounds rather than paragraph
allocation, the complete 41-test browser suite and both Detekt tasks passed in
a focused rerun. The earlier Contact navigation test remains covered.

The app journey test exercises theme selection, Contact navigation and activity
recreation. Run it with `./gradlew :androidApp:connectedDevDebugAndroidTest`.
It was compiled, not executed: no Android device or configured AVD was available.
Native visual/share/link checks, optimized-release runtime verification, APK size
comparison, iOS and hosted CI remain unverified. Nothing was published or deployed.
