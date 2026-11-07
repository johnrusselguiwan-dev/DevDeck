package com.example.devdeck.data.repository

import com.example.devdeck.ThemePreferences
import com.example.devdeck.data.BundledPortfolioLoader
import com.example.devdeck.di.appModule
import com.example.devdeck.domain.repository.PortfolioRepository
import com.example.devdeck.testPortfolio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PortfolioRepositoryTest {
    @Test
    fun collectionReadsOneWholeSnapshotAndReloadReadsAgain() = runTest {
        var reads = 0
        val first = testPortfolio()
        val second = first.copy(profile = first.profile.copy(bio = "Updated biography"))
        val loader = BundledPortfolioLoader {
            reads++
            Json.encodeToString(if (reads == 1) first else second).encodeToByteArray()
        }
        val repository = PortfolioRepositoryImpl(loader)
        val snapshots = repository.getPortfolioData()
        assertEquals(0, reads)
        assertEquals(listOf(first), snapshots.toList())
        assertEquals(1, reads)
        assertEquals(listOf(second), snapshots.toList())
        assertEquals(2, reads)
    }

    @Test
    fun resourceFailuresPropagateThroughTheRepository() = runTest {
        val failure = IllegalStateException("Resource unavailable")
        val repository = PortfolioRepositoryImpl(BundledPortfolioLoader { throw failure })
        val caught = assertFailsWith<IllegalStateException> { repository.getPortfolioData().first() }
        assertSame(failure, caught)
    }

    @Test
    fun malformedContentFailsWithoutEmittingAFallback() = runTest {
        val repository = PortfolioRepositoryImpl(BundledPortfolioLoader { "{".encodeToByteArray() })
        val emissions = mutableListOf<Any>()
        assertFailsWith<SerializationException> {
            repository.getPortfolioData().collect { emissions += it }
        }
        assertTrue(emissions.isEmpty())
    }

    @Test
    fun collectionCancellationReachesTheResourceReader() = runTest {
        val started = CompletableDeferred<Unit>()
        var readerCancelled = false
        val loader = BundledPortfolioLoader {
            started.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                readerCancelled = true
            }
        }
        val repository = PortfolioRepositoryImpl(loader)
        val result = async { repository.getPortfolioData().first() }
        started.await()
        result.cancel()
        assertFailsWith<CancellationException> { result.await() }
        assertTrue(readerCancelled)
    }

    @Test
    fun appModuleWiresTheSnapshotRepositoryWithoutEagerReads() = runTest {
        var reads = 0
        val loader = BundledPortfolioLoader {
            reads++
            Json.encodeToString(testPortfolio()).encodeToByteArray()
        }
        val application = koinApplication {
            modules(appModule(ThemePreferences()), module { single { loader } })
        }
        try {
            val repository = application.koin.get<PortfolioRepository>()
            assertEquals(0, reads)
            assertEquals(testPortfolio(), repository.getPortfolioData().first())
            assertSame(repository, application.koin.get<PortfolioRepository>())
            assertEquals(1, reads)
        } finally {
            application.close()
        }
    }
}
