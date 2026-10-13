package com.example.devdeck.presentation.ui.home

import androidx.lifecycle.ViewModelStore
import com.example.devdeck.data.BundledPortfolioLoader
import com.example.devdeck.data.repository.PortfolioRepositoryImpl
import com.example.devdeck.domain.model.PortfolioData
import com.example.devdeck.domain.model.SkillCategory
import com.example.devdeck.domain.repository.PortfolioRepository
import com.example.devdeck.domain.usecase.GetPortfolioDataUseCase
import com.example.devdeck.presentation.common.theme.ThemeMode
import com.example.devdeck.testPortfolio
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val store = ViewModelStore()
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        store.clear()
        dispatcher.scheduler.runCurrent()
        Dispatchers.resetMain()
    }

    private fun createViewModel(repository: PortfolioRepository): HomeViewModel {
        return HomeViewModel(GetPortfolioDataUseCase(repository)).also { store.put("home", it) }
    }

    @Test
    fun liveDataUpdatesKeepThemeAndSkillFilter() = runTest {
        val repository = TestRepository()
        val viewModel = createViewModel(repository)
        runCurrent()
        viewModel.setThemeMode(ThemeMode.DARK)
        viewModel.filterSkillsByCategory(SkillCategory.DEVOPS)

        repository.snapshot.value = repository.snapshot.value.copy(
            profile = repository.snapshot.value.profile.copy(bio = "Updated biography")
        )
        runCurrent()

        val state = assertIs<HomeUiState.Success>(viewModel.uiState.value)
        assertEquals("Updated biography", state.profile.bio)
        assertEquals(ThemeMode.DARK, state.themeMode)
        assertEquals(SkillCategory.DEVOPS, state.selectedSkillCategory)
    }

    @Test
    fun repeatedReloadsReplaceThePreviousCollector() = runTest {
        val repository = TestRepository()
        val viewModel = createViewModel(repository)
        runCurrent()
        repeat(10) {
            viewModel.loadPortfolioData()
            runCurrent()
            assertEquals(1, repository.activeCollectors)
        }
        assertEquals(1, repository.maximumCollectors)
    }

    @Test
    fun retryAfterFailureKeepsPreferences() = runTest {
        val repository = TestRepository()
        val viewModel = createViewModel(repository)
        runCurrent()
        viewModel.setThemeMode(ThemeMode.LIGHT)
        viewModel.filterSkillsByCategory(SkillCategory.CROSS_PLATFORM)

        repository.failNextCollection = true
        viewModel.loadPortfolioData()
        runCurrent()
        val error = assertIs<HomeUiState.Error>(viewModel.uiState.value)
        assertEquals("Repository unavailable", error.message)
        assertEquals(ThemeMode.LIGHT, error.themeMode)

        viewModel.loadPortfolioData()
        runCurrent()
        val recovered = assertIs<HomeUiState.Success>(viewModel.uiState.value)
        assertEquals(ThemeMode.LIGHT, recovered.themeMode)
        assertEquals(SkillCategory.CROSS_PLATFORM, recovered.selectedSkillCategory)
    }

    @Test
    fun clearingTheViewModelStopsCollection() = runTest {
        val repository = TestRepository()
        createViewModel(repository)
        runCurrent()
        assertEquals(1, repository.activeCollectors)

        store.clear()
        runCurrent()
        assertEquals(0, repository.activeCollectors)
    }

    @Test
    fun malformedBundledContentCanRetryWithoutLosingPreferences() = runTest {
        var content = "{"
        val repository = PortfolioRepositoryImpl(BundledPortfolioLoader { content.encodeToByteArray() })
        val viewModel = createViewModel(repository)
        runCurrent()
        assertIs<HomeUiState.Error>(viewModel.uiState.value)
        viewModel.setThemeMode(ThemeMode.DARK)
        viewModel.filterSkillsByCategory(SkillCategory.DEVOPS)

        content = Json.encodeToString(testPortfolio())
        viewModel.loadPortfolioData()
        runCurrent()

        val recovered = assertIs<HomeUiState.Success>(viewModel.uiState.value)
        assertEquals(testPortfolio().profile, recovered.profile)
        assertEquals(ThemeMode.DARK, recovered.themeMode)
        assertEquals(SkillCategory.DEVOPS, recovered.selectedSkillCategory)
    }

    @Test
    fun reloadCancelsSuspendedResourceReadAndKeepsPreferences() = runTest {
        val firstRead = CompletableDeferred<ByteArray>()
        var reads = 0
        var cancelledReads = 0
        val bytes = Json.encodeToString(testPortfolio()).encodeToByteArray()
        val loader = BundledPortfolioLoader {
            reads++
            if (reads == 1) {
                try {
                    firstRead.await()
                } finally {
                    cancelledReads++
                }
            } else {
                bytes
            }
        }
        val viewModel = createViewModel(PortfolioRepositoryImpl(loader))
        runCurrent()
        assertIs<HomeUiState.Loading>(viewModel.uiState.value)
        viewModel.setThemeMode(ThemeMode.LIGHT)
        viewModel.filterSkillsByCategory(SkillCategory.CROSS_PLATFORM)
        viewModel.loadPortfolioData()
        runCurrent()

        val state = assertIs<HomeUiState.Success>(viewModel.uiState.value)
        assertEquals(2, reads)
        assertEquals(1, cancelledReads)
        assertEquals(ThemeMode.LIGHT, state.themeMode)
        assertEquals(SkillCategory.CROSS_PLATFORM, state.selectedSkillCategory)
    }

    private class TestRepository : PortfolioRepository {
        val snapshot = MutableStateFlow(testPortfolio())
        var activeCollectors = 0
        var maximumCollectors = 0
        var failNextCollection = false

        override fun getPortfolioData(): Flow<PortfolioData> = flow {
            if (failNextCollection) {
                failNextCollection = false
                error("Repository unavailable")
            }
            activeCollectors++
            maximumCollectors = maxOf(maximumCollectors, activeCollectors)
            try {
                emitAll(snapshot)
            } finally {
                activeCollectors--
            }
        }
    }
}
