package com.example.devdeck.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.devdeck.di.appModule
import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.domain.usecase.GetPortfolioDataUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.koinApplication
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BundledPortfolioResourceTest {
    @Test
    fun productionRepositoryLoadsThePackagedSnapshot() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resource = "composeResources/com.example.devdeck.shared.generated.resources/files/portfolio.json"
        val bytes = context.assets.open(resource).use { it.readBytes() }
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
