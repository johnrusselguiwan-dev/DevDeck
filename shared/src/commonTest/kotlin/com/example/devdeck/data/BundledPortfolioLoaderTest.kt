package com.example.devdeck.data

import com.example.devdeck.domain.model.DeveloperProfile
import com.example.devdeck.domain.model.Experience
import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.domain.model.Project
import com.example.devdeck.domain.model.Skill
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.model.SocialLink
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BundledPortfolioLoaderTest {
    private val fixture = PortfolioData(
        profile = DeveloperProfile(
            name = "Test Developer",
            title = "Developer",
            bio = "Builds apps",
            location = "Philippines",
            yearsExperience = "One year",
            playStoreAppsCount = 1,
            coreFocus = listOf("Kotlin")
        ),
        skills = listOf(Skill("skill", "Kotlin", SkillCategory.MOBILE_DEV, "Language", "Mobile development")),
        experiences = listOf(
            Experience(
                id = "experience",
                role = "Developer",
                company = "Company",
                period = "2026",
                isCurrent = true,
                summary = "Built an app",
                highlights = listOf("Shipped"),
                techStack = listOf("Kotlin")
            )
        ),
        projects = listOf(Project("project", "Portfolio", "Mobile app", "A portfolio", listOf("Kotlin"))),
        socialLinks = listOf(SocialLink("contact", "Email", "mailto:developer@example.com", "email"))
    )
    private val json = Json { encodeDefaults = true }
    private fun encoded(snapshot: PortfolioData = fixture): String = json.encodeToString(snapshot)

    @Test
    fun decodesCompleteSnapshotIncludingUnicode() {
        val snapshot = fixture.copy(profile = fixture.profile.copy(name = "Developer • 日本語"))
        assertEquals(snapshot, decodePortfolio(encoded(snapshot)))
    }

    @Test
    fun missingRootFieldsFail() {
        val root = json.parseToJsonElement(encoded()).jsonObject
        root.keys.forEach { key ->
            assertFailsWith<SerializationException>(key) { decodePortfolio(JsonObject(root - key).toString()) }
        }
    }

    @Test
    fun missingDefaultedRequiredFieldsFail() {
        val root = json.parseToJsonElement(encoded()).jsonObject
        val profile = root.getValue("profile").jsonObject
        profile.keys.forEach { key ->
            val changed = JsonObject(root + ("profile" to JsonObject(profile - key)))
            assertFailsWith<SerializationException>(key) { decodePortfolio(changed.toString()) }
        }
        listOf("skills" to "specialty", "projects" to "isFeatured").forEach { (collection, key) ->
            val record = root.getValue(collection).jsonArray.single().jsonObject
            val changed = JsonObject(root + (collection to JsonArray(listOf(JsonObject(record - key)))))
            assertFailsWith<SerializationException>(key) { decodePortfolio(changed.toString()) }
        }
    }

    @Test
    fun malformedJsonUnknownKeysAndUnknownCategoriesFail() {
        val root = json.parseToJsonElement(encoded()).jsonObject
        assertFailsWith<SerializationException> { decodePortfolio("{") }
        assertFailsWith<SerializationException> {
            decodePortfolio(JsonObject(root + ("unexpected" to JsonPrimitive(true))).toString())
        }
        val profile = root.getValue("profile").jsonObject
        assertFailsWith<SerializationException> {
            val changed = JsonObject(profile + ("unexpected" to JsonPrimitive(true)))
            decodePortfolio(JsonObject(root + ("profile" to changed)).toString())
        }
        assertFailsWith<SerializationException> { decodePortfolio(encoded().replace("MOBILE_DEV", "UNKNOWN")) }
        assertFailsWith<SerializationException> { decodePortfolio(encoded().replace("MOBILE_DEV", "mobile_dev")) }
    }

    @Test
    fun nullableProjectLinksCanBeOmitted() {
        val root = json.parseToJsonElement(encoded()).jsonObject
        val project = root.getValue("projects").jsonArray.single().jsonObject
        val linksOmitted = JsonObject(project - "playStoreUrl" - "githubUrl")
        val changed = JsonObject(root + ("projects" to JsonArray(listOf(linksOmitted))))
        assertEquals(fixture, decodePortfolio(changed.toString()))
    }

    @Test
    fun emptyCollectionsAndAdditionalRecordsAreAllowed() {
        val empty = fixture.copy(
            skills = emptyList(),
            experiences = emptyList(),
            projects = emptyList(),
            socialLinks = emptyList(),
            profile = fixture.profile.copy(coreFocus = emptyList())
        )
        assertEquals(empty, decodePortfolio(encoded(empty)))
        val expanded = fixture.copy(projects = fixture.projects + fixture.projects.single().copy(id = "another"))
        assertEquals(expanded, decodePortfolio(encoded(expanded)))
    }

    @Test
    fun duplicateAndBlankIdsFailWithinEachCollection() {
        val invalid = listOf(
            fixture.copy(skills = fixture.skills + fixture.skills),
            fixture.copy(experiences = fixture.experiences + fixture.experiences),
            fixture.copy(projects = fixture.projects + fixture.projects),
            fixture.copy(socialLinks = fixture.socialLinks + fixture.socialLinks),
            fixture.copy(skills = listOf(fixture.skills.single().copy(id = " "))),
            fixture.copy(experiences = listOf(fixture.experiences.single().copy(id = ""))),
            fixture.copy(projects = listOf(fixture.projects.single().copy(id = ""))),
            fixture.copy(socialLinks = listOf(fixture.socialLinks.single().copy(id = "")))
        )
        invalid.forEach { snapshot -> assertFailsWith<IllegalArgumentException> { decodePortfolio(encoded(snapshot)) } }
    }

    @Test
    fun invalidDisplayFieldsListsAndCountsFailWithContext() {
        val invalid = listOf(
            fixture.copy(profile = fixture.profile.copy(name = " ")),
            fixture.copy(profile = fixture.profile.copy(playStoreAppsCount = -1)),
            fixture.copy(profile = fixture.profile.copy(coreFocus = listOf(""))),
            fixture.copy(skills = listOf(fixture.skills.single().copy(specialty = ""))),
            fixture.copy(experiences = listOf(fixture.experiences.single().copy(highlights = listOf("")))),
            fixture.copy(projects = listOf(fixture.projects.single().copy(techBadges = listOf("")))),
            fixture.copy(socialLinks = listOf(fixture.socialLinks.single().copy(iconIdentifier = "")))
        )
        invalid.forEach { snapshot -> assertFailsWith<IllegalArgumentException> { decodePortfolio(encoded(snapshot)) } }
        val failure = assertFailsWith<IllegalArgumentException> { validatePortfolio(invalid.first()) }
        assertTrue(failure.message.orEmpty().contains("profile.name"))
    }

    @Test
    fun unsafeMalformedAndEmptyProjectLinksFail() {
        listOf(
            "", "https://", "https:///path", "https://?query", "https://:443/path", "javascript:alert(1)",
            "mailto:developer@example.com", "https://example.com/a b", "https://example.com/\npath",
            "https://example.com\\path"
        ).forEach { url ->
            val snapshot = fixture.copy(projects = listOf(fixture.projects.single().copy(githubUrl = url)))
            assertFailsWith<IllegalArgumentException>(url) { decodePortfolio(encoded(snapshot)) }
        }
    }

    @Test
    fun supportedContactAndWebLinksArePreserved() {
        listOf(
            "https://example.com/path?a=1&b=2",
            "http://localhost:8080/path",
            "mailto:developer@example.com",
            "tel:+639669869030"
        ).forEach { url ->
            val snapshot = fixture.copy(socialLinks = listOf(fixture.socialLinks.single().copy(url = url)))
            assertEquals(snapshot, decodePortfolio(encoded(snapshot)))
        }
        listOf("mailto:", "mailto:missing-address", "tel:", "tel:abc", "file:///tmp/file", "https://").forEach { url ->
            val snapshot = fixture.copy(socialLinks = listOf(fixture.socialLinks.single().copy(url = url)))
            assertFailsWith<IllegalArgumentException>(url) { decodePortfolio(encoded(snapshot)) }
        }
    }

    @Test
    fun loaderReadsOnlyOnLoadAndDoesNotCache() = runTest {
        val paths = mutableListOf<String>()
        val loader = BundledPortfolioLoader { path ->
            paths += path
            encoded().encodeToByteArray()
        }
        assertTrue(paths.isEmpty())
        assertEquals(fixture, loader.load())
        assertEquals(fixture, loader.load())
        assertEquals(listOf("files/portfolio.json", "files/portfolio.json"), paths)
    }

    @Test
    fun readErrorsAndInvalidUtf8Propagate() = runTest {
        val failure = IllegalStateException("Missing bundled file")
        assertSame(failure, assertFailsWith<IllegalStateException> { BundledPortfolioLoader { throw failure }.load() })
        assertFailsWith<CharacterCodingException> { BundledPortfolioLoader { byteArrayOf(0xc3.toByte(), 0x28) }.load() }
    }

    @Test
    fun cancellationWhileReadingIsNotConvertedToContentFailure() = runTest {
        val started = CompletableDeferred<Unit>()
        val loader = BundledPortfolioLoader {
            started.complete(Unit)
            awaitCancellation()
        }
        val result = async { loader.load() }
        started.await()
        result.cancel()
        assertFailsWith<CancellationException> { result.await() }
    }

    @Test
    fun cancellationBeforeReadAndAfterReadPreventsDecoding() = runTest {
        var reads = 0
        val beforeRead = async {
            cancel()
            BundledPortfolioLoader {
                reads++
                byteArrayOf()
            }.load()
        }
        assertFailsWith<CancellationException> { beforeRead.await() }
        assertEquals(0, reads)
        val afterRead = async {
            BundledPortfolioLoader {
                cancel()
                "invalid JSON".encodeToByteArray()
            }.load()
        }
        assertFailsWith<CancellationException> { afterRead.await() }
    }
}
