# DevDeck local content migration: implementation plan

Date: 2026-10-12. Status: Phases 0, 1, and 2 implemented; Android/Wasm checks
passed. iOS is unverified. See `LOCAL_CONTENT_PHASE1_RESULTS.md` and
`LOCAL_CONTENT_PHASE2_RESULTS.md` for evidence and remaining release checks.

Reviewed proposal:
[CODEX_DEVDECK_LOCAL_CONTENT_REFACTOR.md](/home/joro/Downloads/CODEX_DEVDECK_LOCAL_CONTENT_REFACTOR.md).
Applied skill:
[anti-slop](/home/joro/AndroidStudioProjects/DevDeck/.agents/skills/anti-slop/SKILL.md).

The original request authorized analysis and planning. The user subsequently
authorized proceeding with the local content refactor. Phase 0/1 are being
executed in the existing checkout, preserving pre-existing work; no branch
switch, commit, push, or merge is included. Phase 1 stopped for review before Phase 2. The user then authorized proceeding
to the next task; Phase 2 was performed in the same checkout without Git actions.

## Assessment and project impact

The proposal fits the current architecture. A bundled JSON snapshot can replace
the repository's static Kotlin objects while keeping the use-case/ViewModel/UI
boundary stable. Use the proposed two changes: first prepare and prove the new
source, then switch production to it.

Expected benefits:

- Editing profile, skill, experience, project, and contact records becomes a
  JSON edit, with validation catching authoring mistakes.
- One repository operation produces an internally consistent portfolio snapshot.
- Five-way `combine` and five separate production content definitions disappear.
- UI layout, filtering, navigation, themes, and platform URL behavior stay intact.

Costs and limits:

- Serialization becomes a dependency of the annotated domain models. Accept
  this small coupling; duplicate DTOs and mapper layers are unnecessary here.
- Loading now includes resource access and JSON decoding. A small parse/read cost
  replaces immediate `flowOf` emissions; existing Loading/Error/Retry states handle it.
- Content still ships with each app release. Android/iOS require a new build;
  browser deployment must publish the JSON resource alongside JS/Wasm assets.
- Browser resource loading uses an HTTP request to the bundled asset. This is
  not a new backend or remote-content service. Android/iOS read packaged resources.
- JSON covers the repository's records. Section headings, availability text,
  category display labels, and `KeyHighlightsBanner` copy remain in Kotlin;
  moving every visible word into JSON would exceed this proposal's UI boundary.
- Retrying malformed packaged JSON cannot repair it. Validation tests must catch
  such defects before deployment; no automatic retry loop or fallback dataset.

## Verified checkout and affected boundaries

Source inspection confirms:

| Area | Current implementation | Planned effect |
| --- | --- | --- |
| Modules | `:shared`, `:androidApp`, plus an Xcode iOS host | No new modules |
| Versions | Kotlin 2.3.20; Compose 1.9.3; Material 3 1.9.0; Koin 4.1.1; AGP 9.0.1; Gradle 9.1.0 | Keep existing versions |
| Resources | Compose resources already configured | Add one raw JSON file |
| Generated resource package | `com.example.devdeck.shared.generated.resources` | Import its generated `Res`; do not edit generated code |
| Serialization | No plugin or direct JSON dependency configured | Add narrowly scoped plugin/library declarations |
| Repository | Five `Flow` methods, implemented with static `flowOf` values | One cold `Flow<PortfolioData>` method in Phase 2 |
| Use case | `PortfolioData` declaration plus five-way `combine` | Move the model; preserve use case as a delegate |
| Production callers | The use case calls the five repository methods | No UI caller migration required |
| Test callers | Two repository tests and the ViewModel fake use the old contract | Update in Phase 2 |
| Profile construction | Production repository and ViewModel test fixture | Replace default-dependent test construction before removing defaults |
| DI | Repository singleton, use-case factory, ViewModel registration | Add loader registration only in Phase 2 |
| UI state | Existing cancellation, retry, and preference retention | Preserve behavior |

Content baseline:

- One explicitly constructed profile; its values override stale model defaults.
- Skills: 12, ordered `s1` through `s12`. Category counts are 5 mobile,
  1 cross-platform, 3 architecture, 3 DevOps.
- Experiences: `exp1`, `exp2`, with `isCurrent` true and false respectively.
- Projects: `proj1` through `proj5`; all currently have `isFeatured = true`.
- Contacts: `soc1` through `soc4`, ordered GitHub, LinkedIn, Email, Phone.

| Project | Play Store destination | GitHub repository destination |
| --- | --- | --- |
| `proj1` | null | null |
| `proj2` | present | null |
| `proj3` | present | null |
| `proj4` | null | present |
| `proj5` | null | present |

Preserve every existing string, URL, flag, ID, enum value, and list order exactly.
Do not resolve or rewrite external links during migration.

The checkout already has many uncommitted source/build changes. Preserve them.
Current source contains 11 common tests; stored Android/Wasm reports cover only
10 earlier tests. Those reports do not establish a current baseline. Graphify
still exposes some removed navigation symbols; source was used to verify callers
and the actual contract.

## Target design

```text
portfolio.json
    -> Res.readBytes("files/portfolio.json")
    -> strict UTF-8 decoding
    -> decodePortfolio(text) + validatePortfolio(snapshot)
    -> BundledPortfolioLoader.load()
    -> PortfolioRepository.getPortfolioData(): Flow<PortfolioData>
    -> GetPortfolioDataUseCase.invoke()
    -> existing HomeViewModel / HomeUiState / HomeScreen
```

Keep `PortfolioData` in `domain/model/PortfolioData.kt`. Its five fields retain
their current names and types. The JSON root contains those same five fields.

Keep the data layer small:

- `BundledPortfolioLoader` reads the resource and returns a complete validated
  snapshot. It performs no work in its constructor.
- A pure `decodePortfolio(String)` function configures decoding and invokes
  validation. A pure `validatePortfolio(PortfolioData)` checks content semantics.
  These helpers can stay in the loader file until its size warrants separation.
- Use one constructor function parameter for reading bytes, defaulting to
  `Res.readBytes`, so common tests can inject bytes, errors, and suspended reads.
  This is an I/O test seam, not a new data-source interface or service framework.
- Do not add caching, custom dispatchers, DTO mapping, network clients, result
  wrappers, or new ViewModels. Read once per collection/reload.
- Production repository returns `flow { emit(loader.load()) }`. Reading inside
  the Flow keeps resource/decoding failures in the existing ViewModel error path.
  Do not eagerly load while constructing the repository or creating the Flow.

The generated `Res` already provides a suspend `readBytes` function. Its resolved
path starts with `composeResources/com.example.devdeck.shared.generated.resources/`;
tests and hosting checks must use that resource namespace, not assume a public
`/files/portfolio.json` URL. The official API documents raw resource access.
[Compose resource usage](https://kotlinlang.org/docs/multiplatform/compose-multiplatform-resources-usage.html#raw-files).

## Adjustments to the supplied proposal

### Required fields must be strict during Phase 1 too

`@Serializable` alone would let omitted profile fields reuse today's nonblank,
incorrect defaults. Post-decode nonblank validation would not catch that.

In Phase 1, retain Kotlin constructor defaults for compatibility, but mark
defaulted non-null content fields with `@Required`: all profile fields,
`Skill.specialty`, and `Project.isFeatured`. Keep nullable project URL defaults
optional. Write missing-field tests immediately. `@Required` enforces presence
even when a constructor default exists.
[Kotlin serialization Required API](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-core/kotlinx.serialization/-required/).

In Phase 2, remove personal-content defaults from `DeveloperProfile` and update
the explicit test profile. Remove redundant `@Required` annotations from fields
whose defaults have been removed; retain them for required fields that still
have defaults. No separate JSON DTO or handwritten field-presence tree is needed.

Use strict JSON configuration: unknown keys and unknown enum constants fail;
lenient parsing and value coercion remain disabled; explicit null handling is
retained. Require all five root fields. Decode UTF-8 without silently replacing
invalid byte sequences. Store enum constant names, not display labels.

Validation should check nonblank profile/display fields and IDs; unique IDs
within each collection; nonnegative app count; nonblank provided list entries;
and supported URL schemes with a nonempty destination. Reject obvious malformed
links and control characters without implementing a complete URI standards
parser or rewriting strings. Project links allow HTTP/HTTPS; social links
support the existing HTTPS/mailto/tel schemes. Empty collections remain valid.
Never enforce migration record counts as runtime constraints.

### Android unit tests and packaged-resource tests are different

The pinned Compose 1.9.3 Android resource reader uses an initialized Android
context and `AssetManager`. A plain `testDebugUnitTest` run does not provide
equivalent runtime proof. Its class-loader fallback still comes after asset
context access, so merely putting JSON on a JVM test classpath is insufficient.
This was checked against the installed 1.9.3 source artifact.

Keep pure decoder, validator, fake-reader, and ViewModel tests in `commonTest`.
Put actual Android packaged reads in `androidInstrumentedTest`, with only the
necessary runner/dependency setup in `shared`. Confirm the source set and task
names in Phase 0 before adding that setup. An APK asset inspection supplements
this test but does not replace runtime verification.

For Wasm, first use the existing generated resource/Karma setup. Add a small
test asset/proxy configuration only if a real packaged-read test demonstrates
that it is needed. Compose 1.9.3 has a distinct browser test reader, so also
verify normal application loading after the Phase 2 switch. Do not introduce
diagnostic UI or change platform entry points to make tests pass.

### Parity evidence must not become a second permanent content source

Phase 1 compares decoded bundled data against the current production repository,
field-for-field. Capture that verified snapshot/hash as migration evidence for
Phase 2. Keep a temporary test-only snapshot only if the switch needs it.

After final parity validation, retire the full duplicated legacy fixture and
fixed-count migration assertions. Keep small representative decoder fixtures
and structural/content-quality checks against the real resource. Future valid
JSON edits must not require updating an identical hardcoded portfolio in tests.
Review the existing ID-specific repository tests accordingly while preserving
their useful link-quality checks.

## Phase 0: baseline and preparation

Source inspection and fresh baseline execution are complete. Results are recorded
in `LOCAL_CONTENT_PHASE1_RESULTS.md`. The steps below remain the baseline procedure
for a future checkout or materially changed source.

Before implementation:

1. Recheck status and scoped diffs. Record the starting changes; do not reset,
   clean, stash, or commit unrelated work.
2. Discover actual tasks using `:shared:tasks --all` and `:androidApp:tasks --all`.
   Confirm instrumentation support and available devices/macOS runners.
3. Run the baseline Android and browser tests and builds independently. Run
   lint/Detekt separately because the check script stops on Detekt failures.
4. Record each command/result, including blocked checks and the current test
   count. Do not treat old passing reports as current results.
5. Capture the exact content snapshot using repository-produced objects, including
   resolved defaults and nulls. Record IDs, ordering, and URL availability.

Gate: classify baseline results as pass, existing failure, or environment block;
resolve migration-relevant failures before editing. iOS remains unverified without
macOS/Xcode. No existing unrelated Detekt cleanup is added to this refactor.

## Phase 1: add JSON and prove parity; keep production unchanged

1. Add a catalog alias for `org.jetbrains.kotlin.plugin.serialization` using
   `version.ref = "kotlin"`, and apply it to `shared`. Add the root `apply false`
   declaration if following the current root plugin convention.
2. Propose a pinned `kotlinx-serialization-json` version of **1.10.0**, not whatever
   version appears in a current tutorial. Its release uses Kotlin 2.3.0, aligning
   with this checkout's 2.3.20 compiler baseline. Compiler plugin stays 2.3.20;
   Android/Wasm compilation must verify the choice before the phase can pass.
   [Serialization 1.10.0 release](https://github.com/Kotlin/kotlinx.serialization/releases/tag/v1.10.0).
3. Move `PortfolioData` into the model package and update the use-case import.
   Annotate the root, record models, and category enum for serialization; apply
   the required-field policy above. Do not alter production values/defaults yet.
4. Generate complete UTF-8 JSON from the actual repository snapshot with every
   content field explicit, including defaults, `isFeatured`, and null URLs.
   Keep readable indentation and stable array order.
5. Add loader, pure decoding, and validation. Preserve cancellation; never turn
   `CancellationException` into a content error. Check cancellation around the
   read/decode boundary rather than introducing a custom coroutine scope.
6. Add pure tests, packaged-resource tests, and exact migration parity tests.
7. Compile/build both feasible platforms and rerun the current regression suite.

No production repository switch and no loader DI registration in this phase.

Gate: decoded real-resource content equals the legacy production snapshot; strict
failure tests pass; existing behavior tests pass; Android APK/Wasm builds pass;
actual packaged reads are verified on supported environments. Report missing
Android device/iOS checks explicitly. Do not claim a complete gate or silently
advance past a required check that has not run.

## Phase 2: switch to the snapshot contract

1. Replace the five repository methods with
   `getPortfolioData(): Flow<PortfolioData>`. Current callers are narrow enough
   that an adapter layer or five compatibility accessors are unnecessary.
2. Inject the loader into `PortfolioRepositoryImpl`, emit its snapshot inside a
   cold Flow, and remove the five hardcoded production definitions.
3. Delegate the existing use case to the new method. Keep this pass-through use
   case for migration stability; removing it or changing to a suspend API is
   outside this change.
4. Register `single { BundledPortfolioLoader() }` and retain the repository
   binding, use-case factory, and ViewModel registration. Explicit loader creation
   avoids asking DI to supply its default reader function. Leave host startup alone.
5. Remove conflicting profile defaults and give tests an explicit small profile
   fixture. Preserve production JSON values exactly.
6. Update the ViewModel fake to expose a `MutableStateFlow<PortfolioData>` through
   the new repository method. Keep collector accounting, failure/retry behavior,
   live-emission preference tests, and cleanup checks.
7. Exercise real repository/loader failure and cancellation through the use case
   and ViewModel using the injectable reader. Keep HomeScreen/HomeUiState behavior.
8. Prove Phase 2 data equals the Phase 1 snapshot, then retire temporary legacy
   content fixtures and counts. Retain meaningful ongoing validation tests.
9. Run UI smoke checks on Android where possible and the normal Wasm app. Check
   profile/cards, null-link hiding, skills filters, themes, navigation, resize scroll
   retention, and the browser's actual JSON resource request.
10. Update `PROJECT_CONTEXT.md` and run `graphify update .`; review the final scoped
    diff. No commits are made unless the user separately requests them.

Gate: JSON is the sole production source of repository content; data parity and
behavior checks pass; there are no new unrelated layout, host, plugin-model, or
dependency-version changes. Record all remaining environment limitations.

## File change map

Paths below are relative to the repository. Shared Kotlin paths use the existing
`shared/src/commonMain/kotlin/com/example/devdeck/` root.

| File | Phase | Change |
| --- | --- | --- |
| `gradle/libs.versions.toml` | 1 | Serialization plugin/library aliases |
| Root `build.gradle.kts` | 1, if needed | Serialization plugin `apply false` |
| `shared/build.gradle.kts` | 1 | Plugin, common JSON dependency, minimal instrumentation test setup |
| `shared/src/commonMain/composeResources/files/portfolio.json` | 1 | Complete extracted content |
| `domain/model/PortfolioModels.kt` | 1 and 2 | Serialization/required fields, then remove stale profile defaults |
| `domain/model/PortfolioData.kt` | 1 | Move current aggregate without changing fields |
| `domain/usecase/GetPortfolioDataUseCase.kt` | 1 and 2 | Import moved model, then remove `combine` |
| `data/BundledPortfolioLoader.kt` | 1 | Resource read, strict decode, validation |
| `domain/repository/PortfolioRepository.kt` | 2 | Single snapshot Flow contract |
| `data/repository/PortfolioRepositoryImpl.kt` | 2 | Loader injection and one-shot emission |
| `di/AppModule.kt` | 2 | Explicit loader registration |
| `shared/src/commonTest/...` | 1 and 2 | Decoder/validation/fake-reader tests, parity, existing test adaptations |
| `shared/src/androidInstrumentedTest/...` | 1 | Actual Android packaged-resource read |
| `shared/src/wasmJsTest/...` | 1 | Actual browser packaged-resource read |
| `shared/karma.config.d/...` | Only if demonstrated necessary | Test-only resource serving configuration |
| `PROJECT_CONTEXT.md`, `graphify-out/` | 2 | Implementation facts and graph refresh |

## Verification matrix and commands

Proposed commands, to confirm against task discovery in Phase 0:

```bash
./gradlew :shared:tasks --all :androidApp:tasks --all
./gradlew :shared:testDebugUnitTest :shared:wasmJsBrowserTest
./gradlew :androidApp:assembleDebug :shared:wasmJsBrowserDistribution
./gradlew :androidApp:lintDebug :shared:lintDebug
./gradlew :androidApp:detekt :shared:detekt --continue
./gradlew :shared:connectedDebugAndroidTest
./gradlew :shared:wasmJsBrowserDevelopmentRun
# macOS/Xcode only:
./gradlew :shared:iosSimulatorArm64Test
```

Confirm Xcode scheme and simulator destination before the host build/smoke check.
Do not assume a nonexistent `composeApp` task or claim an iOS test from Linux.

| Check | Evidence required |
| --- | --- |
| Complete decoding/parity | Actual bundled snapshot equals current source snapshot, including all fields/order/nulls |
| Required fields | Missing profile fields, specialty, featured flag, and root fields fail |
| Invalid content | Malformed JSON/UTF-8, unknown fields/categories, duplicate/blank IDs, blank display fields, invalid URLs fail with useful context |
| Optional content | Null/omitted nullable URLs remain absent; empty collections and valid added/removed records are allowed |
| Repository behavior | Cold collection reads once and emits one whole snapshot; read/decode failures propagate |
| ViewModel behavior | Existing preferences/live-update/cancel/retry/cleanup tests remain meaningful under the new contract |
| Android packaging | JSON bytes are present in the generated APK resource namespace; instrumented read/decode succeeds |
| Wasm packaging | Real `Res.readBytes` test succeeds; normal app fetch returns valid bundled JSON rather than an HTML fallback |
| iOS integration | Compile and resource-read/UI smoke results from macOS, or an explicit unverified status |
| Content deployment | JS/Wasm/JSON publish together; verify hosting base path and stale-cache behavior before release |

Pure decoder fixtures alone are not packaged-resource proof. Do not freeze the
production record counts in ongoing tests. Report baseline failures separately
from new failures and do not label a blocked check as passing.

## Delivery and recovery

Each phase is one reviewable change with file summary, exact commands/results,
parity evidence, and remaining risks. Phase 1 must pass its gate before Phase 2.
The attached proposal suggests a separate review before the production switch;
Phase 1 stopped at that boundary, and the subsequent user request authorized
Phase 2 in the existing checkout.

Before implementation, record enough of the existing scoped working-tree diff
to reverse only this migration's changes if a gate fails. Do not use `git reset`
or overwrite the pre-existing repository/Gradle edits. Phase 1 naturally retains
the legacy production path; if Phase 2 fails, restore that path from the recorded
Phase 1 state while diagnosing the loader. A rollback is a development action,
not a second hardcoded runtime fallback shipped alongside JSON.

The current delivery includes Phase 2 and the production switch. Remaining
release checks are iOS verification, deployment base-path/cache policy, and
review/commit/PR work when authorized.
Backend features, skill references, UI refactoring,
preference persistence, global Koin ownership, and Android plugin migration stay
outside this work.
