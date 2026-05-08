package app.krafted.jewelplinko.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.ui.neon.Confetti
import app.krafted.jewelplinko.ui.neon.FloatingImage
import app.krafted.jewelplinko.ui.neon.GemButton
import app.krafted.jewelplinko.ui.neon.GemPalette
import app.krafted.jewelplinko.ui.neon.GemText
import app.krafted.jewelplinko.ui.neon.GlowDot
import app.krafted.jewelplinko.ui.neon.Neon
import app.krafted.jewelplinko.ui.neon.NeonBackground
import app.krafted.jewelplinko.ui.neon.PulsingHalo
import app.krafted.jewelplinko.ui.neon.SparkleField
import app.krafted.jewelplinko.ui.neon.formatGrouped
import app.krafted.jewelplinko.viewmodel.BallResult
import app.krafted.jewelplinko.viewmodel.GameUiState

@Composable
fun SessionResultScreen(
    state: GameUiState,
    onSubmitName: (String) -> Unit,
    onBackToHome: () -> Unit,
    onPlayAgain: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    var nameInput by remember(state.playerName) { mutableStateOf("") }
    var nameSaved by remember { mutableStateOf(false) }
    val commitName = {
        val trimmed = nameInput.trim()
        if (trimmed.isNotEmpty() && trimmed != state.playerName) {
            onSubmitName(trimmed)
            nameSaved = true
        }
    }

    val totalSpent = state.betAmount * state.ballPackage
    val totalWon = state.sessionResults.sumOf { it.winnings }
    val net = totalWon - totalSpent
    val isWin = net > 0
    val isJackpot = state.sessionResults.any { it.multiplier >= 50 }
    val bestIndex = state.sessionResults
        .withIndex()
        .maxByOrNull { it.value.winnings }?.index ?: -1

    NeonBackground(bg = R.drawable.bg_neon) {
        SparkleField(count = 22, modifier = Modifier.fillMaxSize())
        Confetti(active = isWin, count = if (isJackpot) 60 else 26, modifier = Modifier.fillMaxSize())

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(animationSpec = tween(450), initialOffsetY = { it }) +
                    fadeIn(animationSpec = tween(450)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "SESSION COMPLETE",
                            color = Color(0xA6FFFFFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 5.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        GemText(
                            text = when {
                                isJackpot -> "JACKPOT!"
                                isWin -> "NICE DROP!"
                                else -> "TRY AGAIN"
                            },
                            fontSize = 32.sp,
                        )
                    }

                    Box(contentAlignment = Alignment.Center) {
                        PulsingHalo(
                            modifier = Modifier.size(130.dp),
                            color = if (isWin) Color(0xFFFFB428) else Color(0xFFAD5FFF),
                        )
                        FloatingImage(
                            res = if (isWin) R.drawable.coin_bag else R.drawable.gift_purple,
                            modifier = Modifier.size(85.dp),
                            amplitude = 4.dp,
                            durationMs = 2400,
                        )
                    }

                    BallResultsList(state.sessionResults, bestIndex)

                    if (state.isNewBestWin) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(listOf(Neon.GemGold, Neon.GemAmber))
                                )
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                        ) {
                            Text(
                                "NEW BEST WIN!",
                                color = Color(0xFF2A0A00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                            )
                        }
                    }

                    TotalsCard(totalSpent = totalSpent, totalWon = totalWon, net = net, isWin = isWin)

                    NameInputCard(
                        value = nameInput,
                        onValueChange = { nameInput = it; nameSaved = false },
                        saved = nameSaved,
                        onSave = commitName,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GemButton(
                        text = "HOME",
                        onClick = { commitName(); onBackToHome() },
                        palette = GemPalette.Purple,
                        modifier = Modifier.weight(1f),
                    )
                    GemButton(
                        text = "PLAY AGAIN ▶",
                        onClick = { commitName(); onPlayAgain() },
                        palette = GemPalette.Pink,
                        glow = true,
                        modifier = Modifier.weight(2f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BallResultsList(results: List<BallResult>, bestIndex: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .heightIn(max = 130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0x14FFFFFF), Color(0x05FFFFFF)))
            )
            .border(1.5.dp, Color(0x40FFB4FF), RoundedCornerShape(16.dp)),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            items(results) { r ->
                BallResultRow(r, isBest = results.indexOf(r) == bestIndex && results.size > 1)
                if (r != results.last()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x14FFFFFF)),
                    )
                }
            }
        }
    }
}

@Composable
private fun BallResultRow(result: BallResult, isBest: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlowDot(size = 26.dp)
        Spacer(Modifier.size(10.dp))
        Text(
            "Ball ${result.ballIndex + 1}",
            color = Color(0xE6FFFFFF),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${result.multiplier}x",
            color = when {
                result.multiplier >= 20 -> Neon.GemGold
                result.multiplier >= 5 -> Color(0xFF9B6BFF)
                else -> Color.White
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = if (result.winnings >= 0) "+${result.winnings.formatGrouped()}" else result.winnings.formatGrouped(),
            color = if (result.winnings >= 0) Neon.GemGold else Color(0xFFFF6080),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.heightIn(min = 0.dp),
            textAlign = TextAlign.End,
        )
        if (isBest) {
            Spacer(Modifier.size(6.dp))
            Text("👑", fontSize = 16.sp)
        }
    }
}

@Composable
private fun TotalsCard(totalSpent: Int, totalWon: Int, net: Int, isWin: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
    ) {
        TotalsRow("Total Bet", "−${totalSpent.formatGrouped()}", Color(0xCCFFFFFF))
        TotalsRow("Total Won", "+${totalWon.formatGrouped()}", Neon.GemGold)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0x26FFFFFF)),
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "NET",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            val arrow = if (isWin) "▲ +" else if (net < 0) "▼ " else ""
            Text(
                "$arrow${kotlin.math.abs(net).formatGrouped()}",
                color = if (isWin) Neon.GreenLight else if (net < 0) Color(0xFFFF6080) else Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun TotalsRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Color(0xCCFFFFFF), fontSize = 13.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun NameInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    saved: Boolean,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x14FFFFFF))
            .border(1.5.dp, Color(0x33FFB4FF), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            "ENTER NAME FOR LEADERBOARD",
            color = Neon.GemGold,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.take(16)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            singleLine = true,
            placeholder = { Text("Enter your name...", color = Color(0x80FFFFFF)) },
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0x4D000000),
                unfocusedContainerColor = Color(0x4D000000),
                focusedBorderColor = Neon.GemGold,
                unfocusedBorderColor = Color(0x60FFB4FF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Neon.GemGold,
                focusedPlaceholderColor = Color(0x80FFFFFF),
                unfocusedPlaceholderColor = Color(0x80FFFFFF),
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSave() }),
        )
        Spacer(Modifier.height(12.dp))
        GemButton(
            text = if (saved) "SAVED ✓" else "SAVE",
            onClick = onSave,
            palette = if (saved) GemPalette.Purple else GemPalette.Orange,
            enabled = !saved && value.trim().isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
