package app.krafted.jewelplinko.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.game.PlinkoBoardView
import app.krafted.jewelplinko.ui.neon.BackPill
import app.krafted.jewelplinko.ui.neon.CoinPill
import app.krafted.jewelplinko.ui.neon.GlowDot
import app.krafted.jewelplinko.ui.neon.Neon
import app.krafted.jewelplinko.ui.neon.NeonBackground
import app.krafted.jewelplinko.ui.neon.NeonBrushes
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

    NeonBackground(bg = R.drawable.bg_game_custom) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BackPill(onBack = {
                    vm.refundSession()
                    onBack()
                })
                CoinPill(value = uiState.coinBalance)
                HudPill(label = "BET", value = uiState.betAmount.toString(), valueColor = Neon.GemGold)
                HudPill(
                    label = null,
                    value = "${(uiState.ballsDropped + 1).coerceAtMost(uiState.ballPackage)}/${uiState.ballPackage}",
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(uiState.ballPackage) { i ->
                    val used = i < uiState.ballsDropped
                    GlowDot(size = 14.dp, used = used, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlinkoBoardView(ctx).also { view ->
                            view.setOnBallLandedListener { ball ->
                                vm.onBallLanded(
                                    ball.landedSlotIndex,
                                    view.slotMultiplierAt(ball.landedSlotIndex),
                                    ball.symbolDrawableRes,
                                )
                            }
                            boardView = view
                        }
                    },
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
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xB3000000))
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AimButton(
                    symbol = "◀",
                    enabled = !uiState.isDropping,
                    onClick = {
                        val v = boardView ?: return@AimButton
                        vm.nudgeAim(-v.width * 0.06f)
                    },
                    modifier = Modifier.weight(1f),
                )
                DropButton(
                    enabled = !uiState.isDropping && uiState.ballsRemaining > 0,
                    onClick = {
                        val v = boardView ?: return@DropButton
                        if (!vm.canDrop()) return@DropButton
                        if (v.dropBall(symbolRes = R.drawable.plin_sym_1)) {
                            vm.confirmDropStarted()
                        }
                    },
                )
                AimButton(
                    symbol = "▶",
                    enabled = !uiState.isDropping,
                    onClick = {
                        val v = boardView ?: return@AimButton
                        vm.nudgeAim(v.width * 0.06f)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        LaunchedEffect(uiState.isSessionComplete) {
            if (uiState.isSessionComplete) onSessionComplete()
        }
    }
}

@Composable
private fun HudPill(label: String?, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .background(NeonBrushes.CoinPill)
            .border(2.dp, Color(0x66FFB4FF), RoundedCornerShape(50))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label != null) {
            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(Modifier.width(6.dp))
        }
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AimButton(
    symbol: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressOffset by animateFloatAsState(
        targetValue = if (pressed && enabled) 4f else 0f,
        animationSpec = tween(80),
        label = "aimPress",
    )
    Box(
        modifier = modifier.height(64.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Neon.BlueShadow),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = pressOffset.dp, bottom = (6f - pressOffset).coerceAtLeast(0f).dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(
                    if (enabled) Brush.verticalGradient(
                        listOf(Neon.BlueLight, Neon.Blue, Neon.BlueDark)
                    ) else Brush.verticalGradient(
                        listOf(Color(0x99502878), Color(0x9928085A))
                    )
                )
                .border(3.dp, Color(0x80FFFFFF), RoundedCornerShape(percent = 50))
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                ) { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                symbol,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color(0x40000000),
                        offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}

@Composable
private fun DropButton(enabled: Boolean, onClick: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "dropPulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1400), repeatMode = RepeatMode.Reverse),
        label = "dropScale",
    )
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressOffset by animateFloatAsState(
        targetValue = if (pressed && enabled) 6f else 0f,
        animationSpec = tween(80),
        label = "dropPress",
    )
    Box(
        modifier = Modifier
            .size(110.dp)
            .scale(if (enabled) pulse else 1f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(CircleShape)
                .background(Neon.PinkShadow),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = pressOffset.dp, bottom = (8f - pressOffset).coerceAtLeast(0f).dp)
                .clip(CircleShape)
                .background(
                    if (enabled) Brush.verticalGradient(
                        listOf(Neon.PinkLight, Neon.Pink, Neon.PinkDark)
                    ) else Brush.verticalGradient(
                        listOf(Color(0x99502878), Color(0x9928085A))
                    )
                )
                .border(4.dp, Color(0x99FFFFFF), CircleShape)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                ) { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "DROP",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color(0x66000000),
                            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }
    }
}
