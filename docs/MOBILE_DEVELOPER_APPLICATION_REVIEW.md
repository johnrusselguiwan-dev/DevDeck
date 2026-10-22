# Mobile developer application review

Research date: October 21, 2026. Confirmed target: junior Kotlin/Jetpack Compose Android jobs, onsite or remote in the Philippines, plus international remote roles accepting Philippine residents. Flutter, React Native and international onsite roles are outside the current target.

Implementation follow-up: the portfolio working tree now includes revised shared content, project contribution lists, separate education, a corrected PDF resume and a generated HTML text version. The observations below record the pre-change review; the older resume exports in `/home/joro/Projects/Resume` remain unchanged. Publication is separate from these local edits.

## Scope and evidence

Reviewed `docs/DEVELOPMENT.md`, `README.md`, the Graphify report and a targeted query, `portfolio.json`, the Wasm HTML shell, and selected ViewModel, test and CI source. This assesses the current working tree, which includes unpublished changes. It does not establish that the deployed website contains the same version.

After John supplied the location, reviewed `/home/joro/Projects/Resume/AGENTS.md`, `/home/joro/Projects/Resume/tech_experience.md`, relevant skill-audit records, the Android Developer, Mobile Developer and Infor resume HTML sources in `output/`, and the Saved Mobile Developer HTML. Extracted text from the output Mobile Developer and Catdev PDFs and visually inspected the complete one-page Mobile Developer PDF.

The reviewed master profile is the source of truth for personal contributions. Specific confirmed review notes override older broad claims elsewhere in that same file. Employer repositories and authorship were not independently audited. DevDeck source confirms relevant patterns and tests exist, but their presence alone does not establish independent authorship or interview proficiency. No builds or tests were run for this research-only review. Resume files were read without modification.

## Recommended positioning

Use **Junior Android Developer | Kotlin · Jetpack Compose** as the main headline. Keep the formal HeroApps job title in employment history. Kotlin Multiplatform and Compose Multiplatform are useful supporting experience, with the current learning level stated accurately.

The clearest differentiators are contributions to two published Android apps, LoL release operations, authentication, purchases and advertising, and EarnPH location/QR integrations. These deserve more space than academic web projects. An iOS target in DevDeck does not establish a shipped iOS application or Swift proficiency.

The existing content is already unusually specific about personal versus team contributions. Preserve that distinction. Remove distracting material such as researching cancelled donations from the short MLBB overview unless it can become a clear debugging accomplishment. Avoid repeating learning disclaimers in every paragraph; state experience levels once and let concrete work explain the rest.

## What screening tools actually do

ATS means applicant tracking system. Parsing, keyword search, employer filters, and AI-assisted assessment are different capabilities. Greenhouse documents recruiter searches over resume text. Ashby documents configurable AI checks against criteria chosen for each role. These sources demonstrate available capabilities, not how often Philippine employers use them or which stage every employer reviews first. There is no universal keyword list or passing score. [Greenhouse keyword search](https://support.greenhouse.io/hc/en-us/articles/115004600186-Search-resumes-for-keywords), [Ashby application review](https://docs.ashbyhq.com/ai-assisted-application-review).

Put required evidence in the resume and application fields. Do not rely on a reviewer or screening product opening a portfolio link. Match the job description's terminology when it accurately describes your experience, and connect important skills to an actual contribution.

## Hiring evidence

This is a small directional sample, not a statistical market survey or a shortlist of junior vacancies.

| Employer source | Requirements relevant to this portfolio | Interpretation |
| --- | --- | --- |
| [VeritasPay / Narratech junior Android posting](https://ph.linkedin.com/jobs/view/junior-android-developer-at-veritaspay-philippines-inc-4392137962) | Kotlin/Java, Android lifecycle, MVVM, REST APIs, Git, permissions/data protection, collaboration; KMP exposure is a plus | Useful entry-level comparison with substantial overlap. The employer-posted listing says applications are closed; it is not an active lead. |
| [KDCI Android Developer](https://kdci1.recruitee.com/o/android-developer) | Kotlin/Java, Android SDK, Jetpack Compose, Android Studio, REST APIs, Material Design; Play publication and CI/CD are additional advantages | Strong stack overlap. The page asks for 3+ years, so it should inform vocabulary rather than be treated as a junior match. |
| [Bjak Android Software Engineer, Philippines](https://jobs.ashbyhq.com/bjakcareer/e2cac589-f5dd-49a9-8e92-dd8525a02679?embed=js) | Kotlin, Compose, Coroutines/Flow, Android fundamentals, shipped work, REST integration and production debugging | Indexed employer content stresses app links and clear personal contributions. Full page extraction was limited by JavaScript; current availability and full requirements need checking before applying. |
| [Grab Mobile Android Engineer](https://jobs.smartrecruiters.com/Grab/744000152878659-software-engineer-mobile-android-) | Android/Kotlin, architecture, caching/storage, unit/UI tests, CI/CD and collaboration | Regional comparison for engineering depth; not evidence of Philippine remote eligibility. It requests 2+ years. |

A previously indexed Legacy People APAC Android listing returned HTTP 410 when opened; it is excluded from actionable job recommendations. Senior postings should not become a junior candidate's mandatory checklist.

International remote eligibility needs its own check. A remote label does not establish hiring from the Philippines. Appetiser's official careers page describes a remote-first workplace, but the page inspected currently shows only a Junior Full-Stack vacancy, despite older search results for junior Android hiring. Treat it as an employer to revisit, not a confirmed open Android vacancy. No new international junior Android vacancy with fully verified Philippine eligibility was established in this small follow-up sample. [Appetiser careers](https://appetiser.recruitee.com/).

## Keywords and your evidence

| Priority | Wording to use when relevant | Existing evidence and limits |
| --- | --- | --- |
| Core | Android Development, Kotlin, Jetpack Compose, Android SDK, Android Studio, Java, XML Layouts | Kotlin/Compose work across HeroApps projects; Java Android work in CN-Karigusan. Java depth should remain proportionate. |
| Core | Kotlin Coroutines, Flow, StateFlow, ViewModel, MVVM, UI State Management | Reported project use; DevDeck contains ViewModel/StateFlow and coroutine tests. Be ready to explain lifecycle and cancellation. |
| Core | REST APIs, Retrofit, OkHttp, API Integration, Error Handling | Existing skills and EarnPH contributions. Name the libraries used in the relevant project; DevDeck itself has no remote API backend. |
| Core when requested | Room, SQLite, Offline Caching, Cloud Firestore | Master profile records reported use. Keep them in relevant skills lists; do not turn use into ownership of a complete offline architecture. |
| Core | Dependency Injection, Hilt, Koin, Clean Architecture, Multi-Module Gradle Projects, Git | Hilt in MLBB/LoL, Koin in KMP projects, existing layered/module structures. Working within an architecture is different from having designed it. |
| Differentiator | Firebase Authentication, Phone OTP, Firebase Cloud Messaging (FCM), Crashlytics, Remote Config | Reported authentication, check-in, notification and release work. Expand FCM once. |
| Differentiator | Google Play Console, Google Play Billing, In-App Purchases, Internal/Closed/Open Testing, Production Releases | LoL purchases and release operations. Identify the team-owned app and your own responsibilities. |
| Role-specific | Location Permissions, GPS, Polygon Geofencing, QR Code Scanning, Push Notifications | EarnPH, currently internal testing. Polygon checks do not automatically imply Android Geofencing API use. |
| Supporting | Kotlin Multiplatform (KMP), Compose Multiplatform, Material Design 3 | KMP/CMP use is confirmed with continued learning. Do not imply a shipped iOS application. |
| Optional for relevant postings | GitHub Actions, CI/CD, Unit Testing, Android Lint, Detekt | Master profile records familiarity/limited experience. A skills listing must not become an achievement claiming independent pipeline or custom-rule authorship. Static analysis is not a substitute for tests. |

Do not add WorkManager, Espresso, Swift/SwiftUI, Flutter, React Native, OAuth, certificate pinning or performance optimization as existing skills without actual experience. Room/offline caching was absent from the initial portfolio review but is recorded in the supplied master profile; the earlier suggestion that this was a new skill gap is corrected. Hilt use does not establish deep Dagger knowledge. Authentication SDK integration does not establish security expertise.

Use plain skill names in final resumes, consistent with the Resume workspace preference; retain proficiency detail in the master profile and avoid unsupported ownership claims in bullets. Omit low-priority, limited-experience tooling when space is needed for confirmed Android work.

Suggested compact skills block, shortened per posting:

- **Android:** Kotlin, Java, Android SDK, Jetpack Compose, XML Layouts, Material Design 3.
- **Architecture and state:** MVVM, Clean Architecture, Coroutines, Flow, StateFlow, Hilt, Koin, multi-module Gradle projects.
- **Data:** REST APIs, Retrofit, OkHttp, Room, SQLite, Cloud Firestore.
- **Firebase:** Authentication, Cloud Messaging (FCM), Crashlytics, Remote Config, App Distribution.
- **Delivery and integrations:** Git, pull requests, Google Play Console, Google Play Billing, in-app purchases, GPS/geofencing, QR scanning.
- **Additional mobile experience:** Kotlin Multiplatform (KMP), Compose Multiplatform.

These names record relevant use, not equal proficiency across every item. SharedFlow can be added for a relevant posting, but advanced reactive-pipeline expertise is not established. Do not add a specific AWS service when the confirmed activity was consuming backend REST endpoints.

## Resume file audit: correct before submitting

The current DevDeck content is substantially more accurate than the older exported resumes. Keywords are already plentiful; factual alignment is the urgent issue.

| Existing wording or presentation | Finding and correction |
| --- | --- |
| Three production apps | MLBB and LoL are published; EarnPH is internal testing. Use “contributed to two published Android guides and an internally tested rewards app.” |
| Co-architected modules; built LoL from scratch | Architecture ownership is pending, and LoL started from an existing champion list in the shared repository. Describe the champion browsing/search/detail features and release work you implemented. |
| Authored CI pipelines and custom Detekt rules | Reviewed notes record familiarity, not confirmed independent authorship. Remove these achievement claims. |
| Implemented motion-sensor rewards or scratch cards | These EarnPH features were inherited. Remove personal implementation credit. |
| Direct CameraX/native camera implementation | The confirmed QR flow uses KScan; do not infer low-level CameraX ownership from its implementation. |
| 60 FPS, low latency, IP-block avoidance, resolved trademark flags or proven billing diagnosis | Unmeasured or pending claims. Remove these outcomes and replace with the confirmed feature or investigation scope. |
| DevDeck “In development” | Public Web and Android releases exist; an iOS release is not confirmed. Include the portfolio URL, which is missing from the reviewed Mobile Developer contact block. |
| Catdev PDF length | Three pages, including a split skills list, references and certification text. Use the one-page Mobile Developer layout as the starting point and remove redundant content. |
| Android/Infor/Saved Mobile Developer typography | HTML sources use much body text below the Resume workspace's 10–11pt requirement. Cut duplication instead of shrinking the text. The Infor source also uses a skills table despite the single-column/no-layout-table rule. |

The output Mobile Developer PDF is one page, has selectable text and a clear reading order in local extraction, and its rendered page has no obvious clipping or overlap. This is a practical extraction/layout check, not a vendor ATS score. Its factual content still needs replacement. The master profile itself also retains older architecture, localization, and Koin ownership wording alongside pending flags; use the specific user-confirmed notes instead of copying every bullet verbatim.

## Suggested resume wording

Summary:

> Junior Android Developer at HeroApps since February 2026, building features with Kotlin and Jetpack Compose. Contributed to two published Android guides, including champion browsing and search, Firebase authentication, in-app purchases, and Google Play release workflows. Also work on location and QR check-in features for an internally tested rewards app.

Potential experience bullets, supported by the reviewed master profile:

- Implemented champion browsing, filtering, detail screens and a shared search component across champion, item and summoner-spell screens in the published LoL Game Guide.
- Implemented donation and remove-ads purchases using Google Play Billing and handled LoL Play Console testing tracks through production release.
- Implemented Firebase Authentication login/sign-up and contributed Jetpack Compose screens to the MLBB Game Guide redesign from Figma designs.
- Implemented or substantially modified phone OTP, GPS/polygon geofencing, QR check-in and REST API integrations for EarnPH, a rewards app in internal testing.

Use a direct verb such as “implemented” or “extended” only after confirming the ownership of each EarnPH feature. Replace “implemented or substantially modified” with the correct verb per feature when that detail is available.

Add outcomes when measured: affected screens, release scope, reproducible bugs fixed, documented testing coverage, or before/after performance measurements. Never invent user counts, revenue, crash reductions or speed improvements. Publishing a purchase feature does not establish increased revenue.

Recommended one-page order for your current experience: contact and links, short summary, technical skills, professional experience, selected projects, education/awards. This length and ordering are editorial recommendations, not ATS rules. Keep your degree, capstone and OJT awards concise. Use the precise graduation and employment dates consistently.

Use one column, standard section names and selectable text. Keep contact details in the document body. Avoid graphics, skill bars, text boxes and complex tables in the submitted resume. Follow the application's required file type; PDF is suitable when accepted and text extraction works. Check reading order by copying its text into a plain-text editor and verify autofilled application fields. Greenhouse specifically documents columns, graphics and header/footer contact information as parsing risks. [Greenhouse parsing guidance](https://support.greenhouse.io/hc/en-us/articles/200989175-Unsuccessful-resume-parse).

## Portfolio changes, in priority order

1. **Provide the resume.** `profile.resumeUrl` is currently `null`. Add a real, accessible resume link, and make Resume, Email, GitHub and selected app links easy to reach without installing the APK.
2. **Add short case studies.** Start with LoL for feature/release breadth and EarnPH for device/API integrations. For each, show the problem, app status, team context, your contribution, one technical decision or difficult edge case, and evidence such as approved screenshots, a short demo or public source. Respect employer permission for internal assets.
3. **Provide readable HTML content.** `shared/src/wasmJsMain/resources/index.html` currently contains metadata, a loading spinner and scripts rather than your experience/project text. Add a visible semantic HTML overview or an accessible HTML resume/case-study page, generated from the same content if practical. Keep the KMP app as a demonstration. This is a discoverability concern inferred from the source shell, not a measured failure of every AI tool. Google documents that canvas content is not indexed and recommends text in semantic HTML/DOM. [Google developer SEO guidance](https://developers.google.com/search/docs/fundamentals/get-started-developers).
4. **Make evidence easy to inspect.** Keep public Play links, explain unavailable source code, distinguish internal testing from production, and make Android/Web/iOS status explicit. Add screenshots and a brief architecture/testing explanation to DevDeck's README. Current source includes regression tests for state, retry/cancellation and bundled content; explain them only to the extent you can defend them.
5. **Prioritize mobile content.** If presentation order is flexible, LoL tells the broadest Android story. Keep MLBB prominent if its flagship position is intentional. Place academic web/desktop work lower and separate Education from professional Experience when practical.

## Learning priorities

Apply while improving these areas; they are recommendations based on current evidence gaps, not proof that you have never used them.

1. **Android state and lifecycle:** explain configuration changes, process death, ViewModel scope, Flow collection, cancellation and Compose recomposition with a small working example.
2. **Local data and offline behavior:** deepen the Room/offline caching experience already reported. Demonstrate API/cache boundaries, loading/error states, retry and consistency decisions in an example you understand and can maintain.
3. **Tests you can maintain:** write focused ViewModel/repository tests and one Compose interaction test. Know what they verify and how to diagnose failures.
4. **Production diagnosis:** investigate a real issue using Crashlytics or Android profiling tools and document reproduction, cause, fix and measured verification.

WorkManager, deep links, accessibility and security fundamentals can follow when target postings or your feature require them. KMP can remain an additional strength. There is no evidence here that learning Flutter, React Native and Swift simultaneously is the best next step for your Android applications.

For each application, map its must-have requirements to evidence, tailor the headline/summary and most relevant bullets, and distinguish skills still being learned. Location, work authorization, schedule and experience requirements can affect screening independently of technical vocabulary.

## Application targeting

Prioritize Junior Android Developer, Associate Android Developer, Android Developer I, Associate Software Engineer (Android), and Kotlin Android Developer roles whose requirements fit junior experience. Generic Mobile Developer searches should still be filtered to native Android/Kotlin; web React experience does not establish React Native experience.

For Philippine applications, state location and openness to onsite/hybrid/remote work, and settle relocation details for each offer. For international remote applications, check explicit country eligibility, employment/contract arrangement and working-hour overlap. Keep the same factual experience in both versions; tailor the summary and selected bullets to each role.

Search examples: `("junior" OR "associate") "Android Developer" Kotlin Philippines`, `"Android" "Jetpack Compose" Philippines`, `"Android Developer" Kotlin remote Philippines`, and `"Android Engineer" Kotlin remote APAC`. APAC/Asia/worldwide labels still require a country check.

## Remaining scope

Fact and keyword assessment is complete for the supplied notes and reviewed resume versions. A rewritten final resume and publication changes were not requested in this research task. No resume, application content, portfolio source or deployment was modified; only this research report was updated.
