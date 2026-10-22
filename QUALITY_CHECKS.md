# Quality checks

Run from the repository root (or pass the absolute script path):

```sh
bash scripts/check.sh
bash scripts/check.sh --all-variants --browser
bash scripts/check.sh --browser
bash scripts/check.sh --ios
```

The default gate runs `devDebug` lint, unit tests and APK assembly, shared debug
lint/unit tests, and Detekt for both modules. `--all-variants` adds `devRelease`,
`prodDebug` and `prodRelease` lint/assembly, plus shared release lint and app-host
unit tests for both debug flavors. The shared library has no environment flavors;
shared Android tests run once. AGP 9 enables unit tests for the tested build type
(`debug`) by default; the gate uses those tasks and validates release builds
through assembly and lint. There are currently no app-host unit test sources;
those tasks also cover future flavor-specific debug tests.

`--browser` adds the production Wasm distribution, headless Chrome tests
(including a packaged JSON read), and Node.js tests for analytics privacy/dispatch
and the generated HTML portfolio's content, resource links and escaping.
It requires Chrome or `CHROME_BIN` and Node.js 20 or later. The script collects
analytics and Gradle failures and exits nonzero if either fails. Each Gradle
invocation uses `--continue` to collect independent task failures. Additional
Gradle arguments can follow `--`, for example `-- --no-daemon --console=plain`.
iOS checks require macOS/Xcode and are explicitly selected with `--ios`.

The gate uses one Gradle worker and fresh Gradle processes for debug/web and
release checks. AGP 9.0.1 lint can crash inside
`KotlinUastLanguagePlugin.isJvmOrCommonElement` when shared debug and release
analysis run in the same process, even with one worker. Separate invocations
keep both build types checked without disabling lint rules or variants.

Lint errors fail builds, including `HardcodedText` and `ResourceAsColor`; test
sources remain excluded from lint. Both Android modules produce HTML, XML and
SARIF lint reports under `build/reports/`. Release checks use the same gate as
debug checks. To run only production release lint:

```sh
./gradlew :androidApp:lintProdRelease :shared:lintRelease
```

## Detekt debt

Detekt 1.23.8 and its formatting plugin remain enabled with the existing rules.
Source coverage includes all Kotlin and Kotlin script files under each module’s
`src/` tree, including flavor-specific sources, common, Android, iOS, Wasm and
tests. Generated/build directories are excluded. Reports use repository-relative
paths for portable CI diagnostics. Analysis uses the existing syntax-based gate
without platform compilation/type resolution.
Baseline paths are `config/detekt/baseline-<module>.xml`. The shared baseline
records 40 unique existing issue signatures. Android currently
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
use the standard debug key; release APKs are unsigned on CI. The workflow uploads
quality reports and does not publish APKs or deploy Firebase Hosting. Device and
iOS checks are separate. Branch protection requiring this check is a separate
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
