package app.krafted.jewelplinko.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.ui.neon.CoinPill
import app.krafted.jewelplinko.ui.neon.FloatingImage
import app.krafted.jewelplinko.ui.neon.GemButton
import app.krafted.jewelplinko.ui.neon.GemPalette
import app.krafted.jewelplinko.ui.neon.GemText
import app.krafted.jewelplinko.ui.neon.Neon
import app.krafted.jewelplinko.ui.neon.NeonBackground
import app.krafted.jewelplinko.ui.neon.NeonBrushes
import app.krafted.jewelplinko.ui.neon.PulsingHalo
import app.krafted.jewelplinko.ui.neon.SparkleField
import app.krafted.jewelplinko.ui.neon.formatGrouped
import app.krafted.jewelplinko.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    vm: GameViewModel,
    onPlayClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
) {
    val state by vm.uiState.collectAsState()

    NeonBackground(bg = R.drawable.bg_neon) {
        SparkleField(count = 22, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CoinPill(value = state.coinBalance)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    PulsingHalo(modifier = Modifier.size(240.dp))
                    FloatingImage(
                        res = R.drawable.ball_purple,
                        modifier = Modifier.size(180.dp),
                        amplitude = 8.dp,
                        durationMs = 2800,
                    )
                }
                Spacer(Modifier.height(12.dp))
                GemText("JEWEL", fontSize = 50.sp)
                Spacer(Modifier.height(2.dp))
                GemText("PLINKO", fontSize = 50.sp)
                Spacer(Modifier.height(20.dp))
                BestWinCard(state.bestSingleWin)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                GemButton(
                    text = "▶  PLAY",
                    onClick = onPlayClicked,
                    palette = GemPalette.Pink,
                    big = true,
                    glow = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GemButton(
                        text = "🏆 LEADERBOARD",
                        onClick = onLeaderboardClicked,
                        palette = GemPalette.Purple,
                        modifier = Modifier.weight(1f),
                    )
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        GemButton(
                            text = if (state.dailyBonusAvailable) "🎁 BONUS!" else "🎁 CLAIMED",
                            onClick = { vm.claimDailyBonus() },
                            palette = GemPalette.Orange,
                            enabled = state.dailyBonusAvailable,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (!state.dailyBonusAvailable && state.dailyBonusTimeRemainingMs > 0) {
                            val timeText = app.krafted.jewelplinko.viewmodel.GameViewModel.formatTimeRemaining(
                                state.dailyBonusTimeRemainingMs
                            )
                            Text(
                                text = "Next bonus in: $timeText",
                                color = Color(0x80FFFFFF),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                if (state.coinBalance == 0 && !state.dailyBonusAvailable) {
                    Spacer(Modifier.height(12.dp))
                    GemButton(
                        text = "RESET ACCOUNT",
                        onClick = { vm.resetBankruptAccount() },
                        palette = GemPalette.Blue,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun BestWinCard(bestWin: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x1AFFFFFF))
            .border(1.5.dp, Color(0x4DFFB4FF), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🏆", fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "BEST WIN",
                color = Color(0xB3FFFFFF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Text(
                bestWin.formatGrouped(),
                color = Neon.GemGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun NamePromptDialog(onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var temp by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Neon.PanelBot,
        titleContentColor = Neon.GemGold,
        textContentColor = Color(0xFFE8DDFF),
        title = { Text(stringResource(R.string.home_enter_name_title)) },
        text = {
            Column {
                Text(stringResource(R.string.home_enter_name_desc))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = temp,
                    onValueChange = { if (it.length <= 16) temp = it },
                    label = { Text(stringResource(R.string.home_name_input_label), color = Color(0xFFE8DDFF)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Neon.GemGold,
                        unfocusedBorderColor = Neon.PinkLight,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFE8DDFF),
                        cursorColor = Neon.GemGold,
                    ),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (temp.isNotBlank()) onSubmit(temp) else onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Neon.Pink,
                    contentColor = Color.White,
                ),
            ) { Text(stringResource(R.string.home_save_name), fontWeight = FontWeight.Bold) }
        },
    )
}
