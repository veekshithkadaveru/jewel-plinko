package app.krafted.jewelplinko.ui

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.viewmodel.GameViewModel

private val DeepBackground = Color(0xFF0B0220)
private val CardBackground = Color(0xFF160833)
private val Gold = Color(0xFFF6C66B)
private val GoldDark = Color(0xFFB8945A)
private val GoldShimmer = Color(0xFFFFF1D0)
private val DimWhite = Color(0xFFCCBBDD)

@Composable
fun HomeScreen(
    vm: GameViewModel,
    onPlayClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit
) {
    val state by vm.uiState.collectAsState()

    var showNamePrompt by remember { mutableStateOf(false) }

    LaunchedEffect(state.isDataLoaded) {
        if (state.isDataLoaded && state.playerName == "Player" && state.bestSingleWin == 0 && state.coinBalance == 1000) {
            showNamePrompt = true
        }
    }

    if (showNamePrompt) {
        var tempName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNamePrompt = false },
            containerColor = CardBackground,
            titleContentColor = Gold,
            textContentColor = DimWhite,
            title = { Text(stringResource(R.string.home_enter_name_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.home_enter_name_desc))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { if (it.length <= 16) tempName = it },
                        label = {
                            Text(
                                stringResource(R.string.home_name_input_label),
                                color = DimWhite
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = GoldDark,
                            focusedTextColor = GoldShimmer,
                            unfocusedTextColor = DimWhite,
                            cursorColor = Gold
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            vm.submitPlayerName(tempName)
                        }
                        showNamePrompt = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = DeepBackground
                    )
                ) {
                    Text(stringResource(R.string.home_save_name), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPill(
                    emoji = "💎",
                    label = stringResource(R.string.home_coins_label),
                    value = formatNumber(state.coinBalance)
                )
                StatPill(
                    emoji = "🏆",
                    label = stringResource(R.string.home_best_win_label),
                    value = if (state.bestSingleWin <= 0) "—" else formatNumber(state.bestSingleWin)
                )
            }

            Spacer(Modifier.weight(1f))

            GoldDivider()
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                color = Gold,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.home_subtitle),
                color = GoldDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(20.dp))
            GoldDivider()

            Spacer(Modifier.weight(1f))

            if (state.dailyBonusAvailable) {
                val bonusInteraction = remember { MutableInteractionSource() }
                val bonusPressed by bonusInteraction.collectIsPressedAsState()
                val bonusScale by animateFloatAsState(
                    if (bonusPressed) 0.95f else 1f, tween(100), label = "bs"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .scale(bonusScale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF4CD97B),
                                    Color(0xFF28A745)
                                )
                            )
                        )
                        .clickable(interactionSource = bonusInteraction, indication = null) {
                            vm.claimDailyBonus()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_claim_bonus),
                        color = DeepBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
            } else if (state.coinBalance == 0) {
                val resetInteraction = remember { MutableInteractionSource() }
                val resetPressed by resetInteraction.collectIsPressedAsState()
                val resetScale by animateFloatAsState(
                    if (resetPressed) 0.95f else 1f, tween(100), label = "rs"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .scale(resetScale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFF6B6B),
                                    Color(0xFFD94C4C)
                                )
                            )
                        )
                        .clickable(interactionSource = resetInteraction, indication = null) {
                            vm.resetBankruptAccount()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_reset_account),
                        color = DeepBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            val playInteraction = remember { MutableInteractionSource() }
            val playPressed by playInteraction.collectIsPressedAsState()
            val playScale by animateFloatAsState(
                if (playPressed) 0.95f else 1f, tween(100), label = "ps"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(playScale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Gold, Color(0xFFE8A928)))
                    )
                    .clickable(interactionSource = playInteraction, indication = null) {
                        onPlayClicked()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_play_button),
                    color = DeepBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onLeaderboardClicked() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_leaderboard_button),
                    color = GoldDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RowScope.StatPill(emoji: String, label: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = DimWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = GoldShimmer,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GoldDivider() {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(2.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Gold, Color.Transparent)
                )
            )
    )
}

private fun formatNumber(value: Int): String {
    if (value < 1000) return value.toString()
    val s = value.toString()
    val sb = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        sb.append(s[i])
        count++
        if (count % 3 == 0 && i != 0) sb.append(',')
    }
    return sb.reverse().toString()
}
