package app.krafted.jewelplinko.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.viewmodel.BallResult
import app.krafted.jewelplinko.viewmodel.GameUiState

private val DeepBackground = Color(0xFF0B0220)
private val CardBackground = Color(0xFF160833)
private val Gold = Color(0xFFF6C66B)
private val GoldDark = Color(0xFFB8945A)
private val GoldShimmer = Color(0xFFFFF1D0)
private val DimWhite = Color(0xFFCCBBDD)
private val WinGreen = Color(0xFF4CD97B)
private val LossRed = Color(0xFFFF6B6B)

@Composable
fun SessionResultScreen(
    state: GameUiState,
    onBackToHome: () -> Unit,
    onPlayAgain: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val totalSpent = state.betAmount * state.ballPackage
    val totalWon = state.sessionResults.sumOf { it.winnings }
    val net = totalWon - totalSpent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(animationSpec = tween(450), initialOffsetY = { it }) +
                    fadeIn(animationSpec = tween(450))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SESSION COMPLETE",
                    color = GoldShimmer,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Gold, Color.Transparent)
                            )
                        )
                )

                Spacer(Modifier.height(20.dp))

                if (state.isNewBestWin) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Gold, Color(0xFFE8A928))
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "NEW BEST WIN!",
                            color = DeepBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBackground)
                        .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(state.sessionResults) { result ->
                            BallResultRow(result)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBackground)
                        .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TotalRow("Total spent", "$totalSpent", Gold)
                        Spacer(Modifier.height(8.dp))
                        TotalRow("Total won", "+$totalWon", Gold)
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Gold.copy(alpha = 0.2f))
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NET",
                                color = GoldShimmer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            val netColor = when {
                                net > 0 -> WinGreen
                                net < 0 -> LossRed
                                else -> DimWhite
                            }
                            val netText = when {
                                net > 0 -> "+$net"
                                net < 0 -> "$net"
                                else -> "0"
                            }
                            Text(
                                text = netText,
                                color = netColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Gold, Color(0xFFE8A928))
                                )
                            )
                            .clickable { onPlayAgain() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PLAY AGAIN",
                            color = DeepBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardBackground)
                            .border(1.5.dp, Gold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .clickable { onBackToHome() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "HOME",
                            color = Gold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BallResultRow(result: BallResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "BALL #${result.ballIndex + 1}",
            color = GoldDark,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.weight(1.2f)
        )

        Image(
            painter = painterResource(result.symbolDrawableRes),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = "${result.multiplier}x",
            color = GoldShimmer,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "+${result.winnings}",
            color = Gold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
private fun TotalRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = DimWhite, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
