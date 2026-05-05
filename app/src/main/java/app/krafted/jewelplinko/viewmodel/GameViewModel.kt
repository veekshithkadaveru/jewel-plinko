package app.krafted.jewelplinko.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.krafted.jewelplinko.JewelPlinkoApplication
import app.krafted.jewelplinko.data.db.PlinkoDao
import app.krafted.jewelplinko.data.db.WalletEntity
import app.krafted.jewelplinko.data.db.WinRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BallResult(
    val ballIndex: Int,
    val multiplier: Int,
    val winnings: Int,
    val symbolDrawableRes: Int
)

data class GameUiState(
    val coinBalance: Int = 1000,
    val betAmount: Int = 50,
    val ballPackage: Int = 1,
    val ballsRemaining: Int = 1,
    val ballsDropped: Int = 0,
    val aimPosition: Float = 0f,
    val aimBias: Float = 0f,
    val isDropping: Boolean = false,
    val sessionResults: List<BallResult> = emptyList(),
    val isSessionComplete: Boolean = false,
    val bestSingleWin: Int = 0,
    val isNewBestWin: Boolean = false,
    val dailyBonusAvailable: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PlinkoDao =
        (application as JewelPlinkoApplication).database.plinkoDao()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aimRange: ClosedFloatingPointRange<Float> = 0f..0f

    init {
        viewModelScope.launch {
            val wallet = dao.getWallet() ?: WalletEntity(
                id = 0,
                coins = 1000,
                lastDailyBonusClaimMillis = null
            ).also { dao.upsertWallet(it) }
            val bestWin = dao.getBestWin()
            _uiState.update {
                it.copy(
                    coinBalance = wallet.coins,
                    bestSingleWin = bestWin?.winnings ?: 0
                )
            }
        }
    }

    fun startSession(bet: Int, ballPackage: Int): Boolean {
        if (bet <= 0 || ballPackage <= 0) return false
        val cost = bet * ballPackage
        val current = _uiState.value
        if (cost > current.coinBalance) return false
        _uiState.update {
            it.copy(
                coinBalance = it.coinBalance - cost,
                betAmount = bet,
                ballPackage = ballPackage,
                ballsRemaining = ballPackage,
                ballsDropped = 0,
                sessionResults = emptyList(),
                isSessionComplete = false,
                isDropping = false,
                isNewBestWin = false
            )
        }
        viewModelScope.launch {
            dao.upsertWallet(
                WalletEntity(
                    coins = _uiState.value.coinBalance,
                    lastDailyBonusClaimMillis = null
                )
            )
        }
        return true
    }

    fun nudgeAim(delta: Float) {
        _uiState.update {
            val next = (it.aimPosition + delta).coerceIn(aimRange.start, aimRange.endInclusive)
            it.copy(aimPosition = next)
        }
    }

    fun setAimRange(range: ClosedFloatingPointRange<Float>) {
        aimRange = range
        _uiState.update {
            if (it.aimPosition in range) {
                it
            } else {
                val mid = (range.start + range.endInclusive) / 2f
                it.copy(aimPosition = mid)
            }
        }
    }

    fun setAimPosition(x: Float) {
        _uiState.update {
            val clamped = x.coerceIn(aimRange.start, aimRange.endInclusive)
            it.copy(aimPosition = clamped)
        }
    }

    fun canDrop(): Boolean {
        val current = _uiState.value
        return !current.isDropping && current.ballsRemaining > 0
    }

    fun confirmDropStarted(): Boolean {
        val current = _uiState.value
        if (current.isDropping || current.ballsRemaining <= 0) return false
        _uiState.update {
            it.copy(
                isDropping = true,
                ballsRemaining = it.ballsRemaining - 1,
                ballsDropped = it.ballsDropped + 1
            )
        }
        return true
    }

    fun onBallLanded(slotIndex: Int, multiplier: Int, symbolRes: Int) {
        _uiState.update {
            val winnings = it.betAmount * multiplier
            val result = BallResult(
                ballIndex = it.ballsDropped - 1,
                multiplier = multiplier,
                winnings = winnings,
                symbolDrawableRes = symbolRes
            )
            val newBest = winnings > it.bestSingleWin
            val remaining = it.ballsRemaining
            it.copy(
                sessionResults = it.sessionResults + result,
                coinBalance = it.coinBalance + winnings,
                bestSingleWin = if (newBest) winnings else it.bestSingleWin,
                isNewBestWin = newBest,
                isDropping = false,
                isSessionComplete = remaining == 0
            )
        }
        val state = _uiState.value
        viewModelScope.launch {
            dao.upsertWallet(
                WalletEntity(
                    coins = state.coinBalance,
                    lastDailyBonusClaimMillis = null
                )
            )
            dao.insertWin(
                WinRecord(
                    multiplier = multiplier,
                    winnings = state.betAmount * multiplier,
                    symbolDrawableRes = symbolRes,
                    timestampMillis = System.currentTimeMillis()
                )
            )
            val bestWin = dao.getBestWin()
            _uiState.update { it.copy(bestSingleWin = bestWin?.winnings ?: it.bestSingleWin) }
        }
    }

    fun resetSession() {
        _uiState.update {
            it.copy(
                sessionResults = emptyList(),
                isSessionComplete = false,
                isNewBestWin = false,
                ballsDropped = 0,
                ballsRemaining = 0
            )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                GameViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                )
            }
        }
    }
}
