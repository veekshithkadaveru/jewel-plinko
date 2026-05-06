package app.krafted.jewelplinko.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.game.PlinkoBoardView
import app.krafted.jewelplinko.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(onSessionComplete: () -> Unit, onBack: () -> Unit, vm: GameViewModel) {
    val uiState by vm.uiState.collectAsState()
    var boardView by remember { mutableStateOf<PlinkoBoardView?>(null) }

    BackHandler(enabled = !uiState.isSessionComplete) {
        vm.refundSession()
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0220))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlinkoBoardView(ctx).also { view ->
                    view.setOnBallLandedListener { ball ->
                        vm.onBallLanded(
                            ball.landedSlotIndex,
                            view.slotMultiplierAt(ball.landedSlotIndex),
                            ball.symbolDrawableRes
                        )
                    }
                    boardView = view
                }
            }
        )

        val view = boardView
        if (view != null) {
            LaunchedEffect(view) {
                while (view.aimRange().endInclusive <= 0f) {
                    delay(16)
                }
                val range = view.aimRange()
                vm.setAimRange(range)
                vm.setAimPosition((range.start + range.endInclusive) * 0.5f)
            }
            LaunchedEffect(uiState.aimPosition) {
                view.setAimX(uiState.aimPosition)
            }
        }

        LaunchedEffect(uiState.isSessionComplete) {
            if (uiState.isSessionComplete) onSessionComplete()
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.game_coins, uiState.coinBalance),
                color = Color(0xFFF6C66B),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.game_bet, uiState.betAmount),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.game_balls,
                    uiState.ballsDropped,
                    uiState.ballPackage
                ),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameButton(
                text = stringResource(R.string.game_aim_left),
                onClick = {
                    val v = boardView ?: return@GameButton
                    val stepPx = v.width * 0.06f
                    vm.nudgeAim(-stepPx)
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            val isDropEnabled = !uiState.isDropping && uiState.ballsRemaining > 0
            GameButton(
                text = stringResource(R.string.game_drop),
                enabled = isDropEnabled,
                onClick = {
                    val v = boardView ?: return@GameButton
                    if (!vm.canDrop()) return@GameButton
                    if (v.dropBall(symbolRes = R.drawable.plin_sym_1)) {
                        vm.confirmDropStarted()
                    }
                },
                modifier = Modifier.weight(1.5f),
                isPrimary = true
            )

            Spacer(Modifier.width(12.dp))

            GameButton(
                text = stringResource(R.string.game_aim_right),
                onClick = {
                    val v = boardView ?: return@GameButton
                    val stepPx = v.width * 0.06f
                    vm.nudgeAim(stepPx)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed && enabled) 0.95f else 1f, tween(100), label = "btn_scale"
    )

    val bgColor = if (enabled) {
        if (isPrimary) Brush.horizontalGradient(listOf(Color(0xFFF6C66B), Color(0xFFE8A928)))
        else Brush.horizontalGradient(listOf(Color(0xFF6B3FA0), Color(0xFF512A7A)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF444444), Color(0xFF333333)))
    }

    val textColor = if (enabled) {
        if (isPrimary) Color(0xFF0B0220) else Color(0xFFF6C66B)
    } else {
        Color(0xFF888888)
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (enabled) (if (isPrimary) Color(0xFFFFF1D0) else Color(0xFFB8945A)) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .then(
                if (enabled) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (isPrimary) 18.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
