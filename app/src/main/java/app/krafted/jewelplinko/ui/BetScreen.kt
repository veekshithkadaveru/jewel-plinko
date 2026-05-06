package app.krafted.jewelplinko.ui

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R

private val DeepBackground = Color(0xFF0B0220)
private val CardBackground = Color(0xFF160833)
private val Gold = Color(0xFFF6C66B)
private val GoldDark = Color(0xFFB8945A)
private val GoldShimmer = Color(0xFFFFF1D0)
private val AccentPurple = Color(0xFF6B3FA0)
private val DimWhite = Color(0xFFCCBBDD)

private val BetOptions = listOf(10, 25, 50, 100, 250, 500)
private val BallPackages = listOf(1, 3, 5)

@Composable
fun BetScreen(
    coinBalance: Int,
    onStartSession: (bet: Int, ballPackage: Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedBetIndex by remember { mutableIntStateOf(2) }
    var selectedBallPackage by remember { mutableIntStateOf(1) }

    val bet = BetOptions[selectedBetIndex]
    val totalCost = bet * selectedBallPackage
    val canAfford = totalCost <= coinBalance

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.coins_format, coinBalance),
                color = Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.bet_title),
                color = GoldShimmer,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Gold, Color.Transparent)
                        )
                    )
            )

            Spacer(Modifier.height(40.dp))

            SectionLabel(stringResource(R.string.bet_amount_label))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val leftScale by animateFloatAsState(
                    if (pressed) 0.85f else 1f, tween(100), label = "ls"
                )
                val decDesc = stringResource(R.string.bet_decrease_desc)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(leftScale)
                        .clip(CircleShape)
                        .background(AccentPurple.copy(alpha = 0.6f))
                        .border(1.dp, Gold.copy(alpha = 0.4f), CircleShape)
                        .clickable(interactionSource = interactionSource, indication = null) {
                            selectedBetIndex =
                                (selectedBetIndex - 1 + BetOptions.size) % BetOptions.size
                        }
                        .semantics { contentDescription = decDesc },
                    contentAlignment = Alignment.Center
                ) {
                    Text("◀", color = Gold, fontSize = 18.sp)
                }

                Spacer(Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Gold.copy(alpha = 0.15f),
                                    CardBackground
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Gold.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$bet",
                        color = GoldShimmer,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(20.dp))

                val rightInteraction = remember { MutableInteractionSource() }
                val rightPressed by rightInteraction.collectIsPressedAsState()
                val rightScale by animateFloatAsState(
                    if (rightPressed) 0.85f else 1f, tween(100), label = "rs"
                )
                val incDesc = stringResource(R.string.bet_increase_desc)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(rightScale)
                        .clip(CircleShape)
                        .background(AccentPurple.copy(alpha = 0.6f))
                        .border(1.dp, Gold.copy(alpha = 0.4f), CircleShape)
                        .clickable(interactionSource = rightInteraction, indication = null) {
                            selectedBetIndex = (selectedBetIndex + 1) % BetOptions.size
                        }
                        .semantics { contentDescription = incDesc },
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", color = Gold, fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(36.dp))

            SectionLabel(stringResource(R.string.bet_package_label))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BallPackages.forEach { count ->
                    val selected = count == selectedBallPackage
                    val borderColor by animateColorAsState(
                        if (selected) Gold else Gold.copy(alpha = 0.2f),
                        tween(200), label = "bc$count"
                    )
                    val bgAlpha by animateFloatAsState(
                        if (selected) 0.2f else 0.05f,
                        tween(200), label = "ba$count"
                    )
                    val textColor by animateColorAsState(
                        if (selected) GoldShimmer else DimWhite,
                        tween(200), label = "tc$count"
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Gold.copy(alpha = bgAlpha))
                            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { selectedBallPackage = count }
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$count",
                            color = textColor,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (count == 1) stringResource(R.string.bet_ball_singular) else stringResource(
                                R.string.bet_balls_plural
                            ),
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(44.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(
                        1.dp,
                        Gold.copy(alpha = 0.15f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.bet_per_ball_label),
                            color = DimWhite,
                            fontSize = 14.sp
                        )
                        Text(
                            "$bet",
                            color = Gold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.bet_count_label),
                            color = DimWhite,
                            fontSize = 14.sp
                        )
                        Text(
                            "×$selectedBallPackage",
                            color = Gold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.bet_total_cost_label),
                            color = GoldShimmer,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$totalCost",
                            color = if (canAfford) GoldShimmer else Color(0xFFFF6B6B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            val startInteraction = remember { MutableInteractionSource() }
            val startPressed by startInteraction.collectIsPressedAsState()
            val startScale by animateFloatAsState(
                if (startPressed) 0.95f else 1f, tween(100), label = "ss"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(startScale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (canAfford) Brush.horizontalGradient(
                            listOf(Gold, Color(0xFFE8A928))
                        ) else Brush.horizontalGradient(
                            listOf(Color(0xFF444444), Color(0xFF333333))
                        )
                    )
                    .then(
                        if (canAfford) Modifier.clickable(
                            interactionSource = startInteraction,
                            indication = null
                        ) {
                            onStartSession(bet, selectedBallPackage)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                val buttonText = if (canAfford) {
                    if (selectedBallPackage == 1) stringResource(R.string.bet_drop_button_singular)
                    else stringResource(R.string.bet_drop_button_plural)
                } else {
                    stringResource(R.string.bet_not_enough_coins)
                }
                Text(
                    text = buttonText,
                    color = if (canAfford) DeepBackground else Color(0xFF888888),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.back_button),
                    color = GoldDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = GoldDark,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp
    )
}
