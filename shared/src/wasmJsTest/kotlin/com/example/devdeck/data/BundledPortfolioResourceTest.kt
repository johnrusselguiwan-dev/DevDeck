package com.example.devdeck.data

import com.example.devdeck.di.appModule
import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.domain.usecase.GetPortfolioDataUseCase
import com.example.devdeck.shared.generated.resources.Res
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BundledPortfolioResourceTest {
    @Test
    fun productionRepositoryLoadsThePackagedSnapshot() = runTest {
        val bytes = Res.readBytes("files/portfolio.json")
        val expected = Json.decodeFromString<PortfolioData>(bytes.decodeToString(throwOnInvalidSequence = true))
        val application = koinApplication { modules(appModule) }
        try {
            val snapshot = application.koin.get<GetPortfolioDataUseCase>()().first()
            assertEquals(expected, snapshot)
            snapshot.projects.mapNotNull { it.githubUrl }.forEach { url ->
                val path = url.removePrefix("https://github.com/").trimEnd('/').split('/')
                assertTrue(path.size >= 2, "Repository URL needs an owner and repository: $url")
            }
        } finally {
            application.close()
        }
    }
}
