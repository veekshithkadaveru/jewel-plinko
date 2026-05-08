package app.krafted.jewelplinko.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameViewModelTest {

    private lateinit var viewModel: GameViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = GameViewModel(app)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runBlocking {
        delay(100)
        
        val state = viewModel.uiState.value
        assertTrue("Coin balance should be loaded", state.coinBalance >= 0)
        assertEquals("Initial bet amount is 50", 50, state.betAmount)
        assertEquals("Initial ball package is 1", 1, state.ballPackage)
    }

    @Test
    fun testStartSessionUpdatesBet() = runBlocking {
        delay(100)
        viewModel.startSession(100, 3)
        val state = viewModel.uiState.value
        assertEquals(100, state.betAmount)
        assertEquals(3, state.ballPackage)
    }

    @Test
    fun testRefundSession() = runBlocking {
        delay(100)
        
        viewModel.resetBankruptAccount()
        delay(100)
        
        val canStart = viewModel.startSession(100, 3)
        assertTrue(canStart)
        
        var state = viewModel.uiState.value
        assertEquals(700, state.coinBalance)
        assertEquals(3, state.ballsRemaining)
        
        viewModel.refundSession()
        state = viewModel.uiState.value
        
        assertEquals(1000, state.coinBalance)
        assertEquals(0, state.ballsRemaining)
        assertFalse(state.isSessionComplete)
    }
}