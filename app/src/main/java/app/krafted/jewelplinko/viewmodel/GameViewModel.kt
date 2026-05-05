package app.krafted.jewelplinko.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aimRange: ClosedFloatingPointRange<Float> = 0f..0f

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
}
