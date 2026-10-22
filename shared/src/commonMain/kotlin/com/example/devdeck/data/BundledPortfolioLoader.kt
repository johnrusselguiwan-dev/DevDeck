package com.example.devdeck.data

import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.shared.generated.resources.Res
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json

private val portfolioJson = Json {
    ignoreUnknownKeys = false
    isLenient = false
    coerceInputValues = false
    explicitNulls = true
}

class BundledPortfolioLoader(
    private val readBytes: suspend (String) -> ByteArray = { Res.readBytes(it) }
) {
    suspend fun load(): PortfolioData {
        currentCoroutineContext().ensureActive()
        val bytes = readBytes("files/portfolio.json")
        currentCoroutineContext().ensureActive()
        val snapshot = decodePortfolio(bytes.decodeToString(throwOnInvalidSequence = true))
        currentCoroutineContext().ensureActive()
        return snapshot
    }
}

internal fun decodePortfolio(text: String): PortfolioData =
    portfolioJson.decodeFromString<PortfolioData>(text).also(::validatePortfolio)

internal fun validatePortfolio(snapshot: PortfolioData) {
    with(snapshot.profile) {
        validateText(name, "profile.name")
        validateText(title, "profile.title")
        validateText(bio, "profile.bio")
        validateText(location, "profile.location")
        validateText(yearsExperience, "profile.yearsExperience")
        require(playStoreAppsCount >= 0) { "profile.playStoreAppsCount must be nonnegative" }
        validateEntries(coreFocus, "profile.coreFocus")
        resumeUrl?.let { validateUrl(it, "profile.resumeUrl", allowContact = false) }
        androidDownloadUrl?.let { validateUrl(it, "profile.androidDownloadUrl", allowContact = false) }
        websiteUrl?.let { validateUrl(it, "profile.websiteUrl", allowContact = false) }
    }
    validateIds(snapshot.skills.map { it.id }, "skills")
    snapshot.skills.forEach { skill ->
        validateText(skill.name, "skills[${skill.id}].name")
        validateText(skill.specialty, "skills[${skill.id}].specialty")
        validateText(skill.description, "skills[${skill.id}].description")
    }
    validateIds(snapshot.experiences.map { it.id }, "experiences")
    snapshot.experiences.forEach { experience ->
        val path = "experiences[${experience.id}]"
        validateText(experience.role, "$path.role")
        validateText(experience.company, "$path.company")
        validateText(experience.period, "$path.period")
        validateText(experience.summary, "$path.summary")
        validateEntries(experience.highlights, "$path.highlights")
        validateEntries(experience.techStack, "$path.techStack")
    }
    validateIds(snapshot.projects.map { it.id }, "projects")
    snapshot.projects.forEach { project ->
        val path = "projects[${project.id}]"
        validateText(project.title, "$path.title")
        validateText(project.subtitle, "$path.subtitle")
        validateText(project.description, "$path.description")
        validateEntries(project.techBadges, "$path.techBadges")
        validateEntries(project.contributions, "$path.contributions")
        project.playStoreUrl?.let { validateUrl(it, "$path.playStoreUrl", allowContact = false) }
        project.githubUrl?.let { validateUrl(it, "$path.githubUrl", allowContact = false) }
    }
    validateIds(snapshot.socialLinks.map { it.id }, "socialLinks")
    snapshot.socialLinks.forEach { link ->
        val path = "socialLinks[${link.id}]"
        validateText(link.platform, "$path.platform")
        validateText(link.iconIdentifier, "$path.iconIdentifier")
        validateUrl(link.url, "$path.url", allowContact = true)
    }
}

private fun validateText(value: String, path: String) {
    require(value.isNotBlank()) { "$path must not be blank" }
    require(value.none { it.code < 32 || it.code in 127..159 }) { "$path contains a control character" }
}

private fun validateEntries(values: List<String>, path: String) {
    values.forEachIndexed { index, value -> validateText(value, "$path[$index]") }
}

private fun validateIds(ids: List<String>, path: String) {
    ids.forEachIndexed { index, id -> validateText(id, "$path[$index].id") }
    require(ids.toSet().size == ids.size) { "$path contains duplicate IDs" }
}

private fun validateUrl(value: String, path: String, allowContact: Boolean) {
    validateText(value, path)
    require(value.none { it.isWhitespace() || it in "\\<>\"" }) { "$path contains invalid URL characters" }
    val scheme = value.substringBefore(':', missingDelimiterValue = "").lowercase()
    val destination = value.substringAfter(':', missingDelimiterValue = "")
    val valid = when (scheme) {
        "http", "https" -> {
            val authority = destination.removePrefix("//").substringBefore('/').substringBefore('?')
                .substringBefore('#')
            destination.startsWith("//") && authority.isNotBlank() &&
                !authority.startsWith(':') && '@' !in authority
        }
        "mailto" -> {
            val address = destination.substringBefore('?').split('@')
            allowContact && address.size == 2 && address.all { it.isNotBlank() }
        }
        "tel" -> allowContact && destination.any { it.isDigit() } &&
            destination.all { it.isDigit() || it in "+-()." }
        else -> false
    }
    require(valid) { "$path must use a supported scheme with a nonempty destination" }
}
