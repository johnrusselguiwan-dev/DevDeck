# Portfolio Quality Implementation Plan

**Goal:** Resolve the review findings that weaken DevDeck as an Android developer portfolio, with evidence for each completed change.

**Architecture:** Keep the existing KMP, Compose, StateFlow and Koin structure. Fix demonstrated problems in place; remove unused code without adding architectural layers.

**Tech stack:** Kotlin Multiplatform, Compose Multiplatform, Android, Kotlin/Wasm, Koin, Gradle, R8, Kotlin tests, Node.js and Python tooling tests.

**Source:** Senior-developer project review from this conversation, 2026-11-08. Findings below distinguish observed failures from source-based risks.

**For agentic workers:** Execute task-by-task with one agent by default. This document is a plan, not authorization to commit, publish, deploy or start implementation.

## Global constraints

- Preserve existing user edits, including the portfolio content and resume changes.
- Keep existing dependencies and conventions unless a demonstrated requirement needs a change.
- Do not weaken lint, disable failing tests or regenerate the Detekt baseline to hide findings.
- Do not change application identity, version numbers or deployment destinations.
- Run focused checks at task checkpoints; reuse successful results while their inputs remain unchanged.
- Android runtime verification requires a connected device or emulator. iOS verification requires macOS/Xcode.
- No commits, pushes, releases or deployments without explicit authorization.

## Implementation status — 2026-11-08

| Task | Result |
| --- | --- |
| 1. Browser navigation | Fixed in the earlier accepted change; the complete browser suite passes. |
| 2. Large-text actions | Actions stack and grow with their labels. A 320.dp / 200% font-scale browser test checks rendered line bounds, truncation and both URL callbacks. Native visual checks and the rest of the proposed layout matrix remain pending. |
| 3. Android journey | Added a Compose activity-recreation/theme/Contact test. Its APK compiles, and the full gate now compiles it. Execution remains pending: no device is connected and no AVD is configured. |
| 4. Release optimization | Code and resource shrinking enabled; both optimized release variants build and pass lint. Optimized-release runtime checks and size comparison remain pending. |
| 5. Unused UI | Both unused component files deleted; corresponding baseline signatures removed. |
| 6. Content schema | Unused fields removed from models, validation, bundled JSON and fixtures. Host tests and browser packaged-content checks pass. |
| 7. Comments and inspection debt | Unsupported visual-effect claims, empty banner and unused import removed. Detekt passes. |

Verification: 38 shared host tests, 41 browser tests, 16 Node checks and 13 Python tests passed across the full check run and the final affected-test rerun. All four Android variants, both device-test APKs and the production web distribution built; lint completed. The full run initially failed on the new text-layout assertion; after correcting its paragraph-allocation assumption, the complete browser suite and both Detekt tasks passed in a focused rerun. The one-pixel line-bound tolerance handles integer measurement rounding; truncation and ellipsis are still rejected.

No Android runtime, iOS, hosted CI, publication or deployment verification is claimed. The checklists below retain the original acceptance criteria, including work that requires a device.

## Priority overview

| Priority | Task | Completion criterion |
| --- | --- | --- |
| P0 — first | 1. Diagnose and fix browser navigation smoke test | Root cause established; focused test and browser suite pass |
| P1 — before sharing | 2. Make project actions accessible at large text sizes | Both actions remain readable and usable at narrow widths and 200% font scale |
| P1 — before sharing | 3. Verify Android UI and activity recreation | Focused Android UI test plus recorded runtime checks |
| P2 — release quality | 4. Enable release optimization | Optimized production APK builds and its main flows work |
| P2 — maintenance | 5. Delete unused UI implementations | No callers lost; obsolete baseline entries removed |
| P2 — maintenance | 6. Remove unused required content fields | Schema matches actual consumers; content and tests stay consistent |
| P3 — polish | 7. Correct comments and stale inspection debt | Comments describe current behavior; focused diff is clean |

## Review focus

- Contact navigation must reach its destination after asynchronous scrolling; test synchronization must not conceal a broken journey. Owned by task 1.
- Narrow layouts and enlarged text must preserve both project actions. Owned by task 2.
- Activity recreation must preserve expected visible state, and Android intents must work outside the browser host. Owned by task 3.
- Release shrinking must preserve serialized content loading and dependency injection. Owned by task 4.
- Schema cleanup must retain fields used by the generated HTML even when Compose does not use them. Owned by task 6.

## P0 — Diagnose and fix the browser navigation smoke test

### Task 1: Restore a passing navigation check

**Files:**
- `shared/src/wasmJsTest/kotlin/com/example/devdeck/presentation/AppSmokeTest.kt`
- Inspect `shared/src/commonMain/kotlin/com/example/devdeck/presentation/ui/home/HomeScreen.kt` and `shared/src/commonMain/kotlin/com/example/devdeck/presentation/ui/components/ContactSection.kt`; modify production code only if diagnostics establish a defect there.

**Evidence:** The full gate completed 40 browser tests with one failure. Running only `AppSmokeTest` also failed: no node with text `Share portfolio` after clicking Contact. The root cause was not established by the review; do not label it a timing issue without evidence.

- [ ] Reproduce with `./gradlew :shared:wasmJsBrowserTest --tests '*AppSmokeTest*' --max-workers=1 --no-daemon --console=plain`.
- [ ] Inspect the selected Contact node, list scroll position, animation completion and resulting semantics tree. Check which responsive layout the test uses.
- [ ] Determine whether the action targets the wrong node, scrolling fails, the destination is not composed, or the assertion runs too early.
- [ ] Make the smallest correction at the responsible boundary. Keep the assertion proving that Contact resources are reached. If synchronization is responsible, wait for a bounded observable condition rather than adding an arbitrary sleep.
- [ ] Run the focused test successfully, then run `:shared:wasmJsBrowserTest` to establish that the complete browser suite passes.

**Done when:** Diagnostics explain the original failure and the navigation assertion passes without being removed or weakened.

## P1 — Before sharing the portfolio

### Task 2: Let project actions adapt to available space

**Files:**
- `shared/src/commonMain/kotlin/com/example/devdeck/presentation/ui/components/ProjectsShowcase.kt` — `ProjectActions`.
- Add `shared/src/wasmJsTest/kotlin/com/example/devdeck/presentation/ProjectActionsTest.kt` for focused layout coverage using the existing test setup.

**Evidence:** Actions use equal-width cells, fixed `48.dp` heights and single-line labels. These source constraints cannot accommodate sufficiently enlarged labels in a narrow card. Device clipping was not runtime-verified during the review.

- [ ] Add a focused Compose test rendering a project with both URLs at a 320.dp viewport and font scale 2.0. Assert both actions are displayed and each invokes its correct URL callback.
- [ ] Change action arrangement to wrap or stack when necessary. Allow height to grow above a minimum 48.dp touch target and allow full labels to remain readable.
- [ ] Check the enclosing card measurement at wide widths so adaptive actions do not clip inside equal-height project rows.
- [ ] Verify 320.dp and 360.dp widths, font scales 1.0 and 2.0, one-link/two-link cards, and both themes. Visually inspect labels; existence assertions alone do not prove text is unclipped.
- [ ] Run the affected Compose tests and shared Android compilation/lint checks.

**Done when:** Labels are fully readable, both actions work, and card layouts remain usable across the checked configurations.

### Task 3: Add meaningful Android host verification

**Files:**
- `androidApp/build.gradle.kts` — configure Android UI test support only as needed.
- Create `androidApp/src/androidTest/kotlin/com/example/devdeck/PortfolioJourneyTest.kt`.
- Inspect `androidApp/src/main/kotlin/com/example/devdeck/MainActivity.kt`, `shared/src/commonMain/kotlin/com/example/devdeck/App.kt` and `shared/src/androidMain/kotlin/com/example/devdeck/Platform.android.kt`.
- `QUALITY_CHECKS.md` — record only checks actually performed.

**Evidence:** The existing Android device test verifies packaged data. Shared host tests exercise logic, while the browser smoke test exercises the web host. Neither covers Android activity recreation or native intent behavior.

- [ ] Use test dependencies already declared in the version catalog where applicable; add only the Compose/Android test artifacts required by the chosen harness.
- [ ] Add an Android journey test that launches `MainActivity`, reaches Contact, changes theme and recreates the activity.
- [ ] Assert the chosen theme and expected scroll destination survive activity recreation. Treat process recreation separately; do not claim it was tested by `ActivityScenario.recreate()`.
- [ ] Verify native sharing and at least one external link on an emulator or device. Record missing-handler behavior if a suitable test environment is available.
- [ ] Run `./gradlew :androidApp:connectedDevDebugAndroidTest` with a connected emulator/device, plus affected lint checks.
- [ ] If no device is available, compile the test APK and explicitly leave runtime verification pending.

**Done when:** The Android journey passes on a named device/API configuration and native share/link results are recorded. Compilation alone does not complete this task.

## P2 — Release quality and maintenance

### Task 4: Enable and verify release shrinking

**Files:**
- `androidApp/build.gradle.kts` — release build type.
- Review `androidApp/proguard-rules.pro`; change rules only for demonstrated failures.

- [ ] Establish the current production APK size for comparison.
- [ ] Enable `isMinifyEnabled` and `isShrinkResources` for release builds.
- [ ] Build and lint `prodRelease`; keep existing signing behavior and version values.
- [ ] Install the optimized release on a device/emulator and check startup, bundled JSON, Koin/ViewModel creation, themes, navigation, project links and sharing.
- [ ] Verify the release is non-debuggable. Record measured APK sizes and any necessary keep rules; do not infer savings or introduce package-wide keep rules without evidence.

**Done when:** The optimized APK passes build/lint and the listed runtime checks. No claim that obfuscation alone provides security.

### Task 5: Delete unused UI implementations

**Files to remove after confirming references:**
- `shared/src/commonMain/kotlin/com/example/devdeck/presentation/ui/components/AmbientPointerGlowBox.kt`
- `shared/src/commonMain/kotlin/com/example/devdeck/presentation/ui/components/KeyHighlightsBanner.kt`
- Update `config/detekt/baseline-shared.xml` only for findings resolved by these deletions.

- [ ] Confirm there are still no callers in source, tests, previews or configuration.
- [ ] Delete both unused implementations, including their private helpers.
- [ ] Remove only their obsolete baseline signatures; do not regenerate the baseline.
- [ ] Review the diff and run shared Detekt plus affected compilation.

**Done when:** No remaining reference requires the deleted code. The two files account for 204 removable source lines at review time; recheck if their contents have changed.

### Task 6: Make the content schema reflect actual behavior

**Files:**
- `shared/src/commonMain/kotlin/com/example/devdeck/domain/model/PortfolioModels.kt`
- `shared/src/commonMain/kotlin/com/example/devdeck/data/BundledPortfolioLoader.kt`
- `shared/src/commonMain/composeResources/files/portfolio.json`
- `shared/src/commonTest/kotlin/com/example/devdeck/PortfolioTestFixtures.kt`
- `shared/src/commonTest/kotlin/com/example/devdeck/data/BundledPortfolioLoaderTest.kt`
- Inspect `scripts/generate-portfolio-overview.mjs` and its tests before removing fields.

- [ ] Reconfirm all consumers of `iconIdentifier`, `coreFocus` and `playStoreAppsCount` across Kotlin, JavaScript, content and documentation.
- [ ] Remove these fields if they remain unused, including corresponding validation, JSON entries and fixture requirements. Preserve unrelated content edits.
- [ ] Keep `isEducation`: the HTML generator uses it to separate work and education, even though Compose does not.
- [ ] Update decoder tests to prove the revised bundled schema loads. Keep malformed input, unsafe URL and duplicate-ID protections.
- [ ] Run `./gradlew :shared:testAndroidHostTest`, the browser packaged-resource test and `node --test scripts/portfolio-overview.test.mjs`.

**Done when:** Required content fields have consumers, production content loads, and generated HTML retains its existing information.

## P3 — Final polish

### Task 7: Correct misleading comments and resolved inspection debt

**Files:**
- `shared/src/commonMain/kotlin/com/example/devdeck/presentation/ui/home/HomeScreen.kt`
- `config/detekt/baseline-shared.xml`
- `QUALITY_CHECKS.md` only when new verification evidence is available.

- [ ] Replace the screen description with a short account of its current responsive layout and state ownership.
- [ ] Remove unsupported blur/fading-edge claims, the empty fading-edge banner and the unused `Dp` import.
- [ ] Remove the matching resolved baseline entry without changing unrelated rules or suppressions.
- [ ] Review the focused diff and run `git diff --check`. Reuse task-checkpoint results when inputs are unchanged.

**Done when:** Documentation matches the implementation and the changed lines have no outstanding inspection debt.

## Final acceptance checkpoint

- [ ] Run `bash scripts/check.sh --all-variants --browser --quiet` once after the implementation changes settle; inspect the failure details if its exit status is nonzero.
- [ ] Confirm all four Android variants build, shared/app lint and Detekt pass, shared host tests pass, and the complete browser suite passes.
- [ ] Attach the Android runtime evidence from tasks 2–4. List any remaining device or platform limitations explicitly.
- [ ] Review the final diff for unrelated edits, new suppressions, unnecessary dependencies and accidental loss of user changes.
- [ ] Report remaining limitations: iOS, hosted CI, deployed-site behavior, Git-history secrets and a dedicated dependency vulnerability scan were not established by the original review.
- [ ] Stop after implementation and verification. Commit, release and deployment decisions require separate authorization.

## Review baseline — 2026-11-08

- 38 shared Android host tests passed.
- 13 Python and 16 Node.js tests passed.
- Android lint, Detekt, four APK variants and device-test APK compilation completed.
- Full browser run: 39 passed, 1 failed. The failing app smoke test also failed independently with the same missing `Share portfolio` assertion.
- `git diff --check` passed.
- No critical security vulnerability was identified in the inspected paths. This is a scoped review result, not a security certification.
- The review changed no source files. Existing content/resume edits were preserved.
