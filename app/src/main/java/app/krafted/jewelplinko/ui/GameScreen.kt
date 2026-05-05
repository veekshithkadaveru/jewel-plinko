package app.krafted.jewelplinko.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.game.PlinkoBoardView
import app.krafted.jewelplinko.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(onSessionComplete: () -> Unit, vm: GameViewModel) {
    val uiState by vm.uiState.collectAsState()
    var boardView by remember { mutableStateOf<PlinkoBoardView?>(null) }

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
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Coins: ${uiState.coinBalance}", color = Color(0xFFF6C66B))
            Text(text = "Bet: ${uiState.betAmount}", color = Color.White)
            Text(
                text = "Balls: ${uiState.ballsDropped}/${uiState.ballPackage}",
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val v = boardView ?: return@Button
                val stepPx = v.width * 0.06f
                vm.nudgeAim(-stepPx)
            }) { Text("AIM <") }

            Button(
                enabled = !uiState.isDropping && uiState.ballsRemaining > 0,
                onClick = {
                    val v = boardView ?: return@Button
                    if (!vm.canDrop()) return@Button
                    if (v.dropBall(symbolRes = R.drawable.plin_sym_1)) {
                        vm.confirmDropStarted()
                    }
                }
            ) { Text("DROP") }

            Button(onClick = {
                val v = boardView ?: return@Button
                val stepPx = v.width * 0.06f
                vm.nudgeAim(stepPx)
            }) { Text("AIM >") }
        }
    }
}
