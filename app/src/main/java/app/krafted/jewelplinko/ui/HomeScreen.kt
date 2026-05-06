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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DeepBackground = Color(0xFF0B0220)
private val CardBackground = Color(0xFF160833)
private val Gold = Color(0xFFF6C66B)
private val GoldDark = Color(0xFFB8945A)
private val GoldShimmer = Color(0xFFFFF1D0)
private val DimWhite = Color(0xFFCCBBDD)

@Composable
fun HomeScreen(
    coinBalance: Int,
    bestSingleWin: Int,
    onPlayClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPill(
                    emoji = "💎",
                    label = "COINS",
                    value = formatNumber(coinBalance)
                )
                StatPill(
                    emoji = "🏆",
                    label = "BEST WIN",
                    value = if (bestSingleWin <= 0) "—" else formatNumber(bestSingleWin)
                )
            }

            Spacer(Modifier.weight(1f))

            GoldDivider()
            Spacer(Modifier.height(20.dp))
            Text(
                text = "JEWEL PLINKO",
                color = Gold,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "PHYSICS GEMSTONE DROP",
                color = GoldDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(20.dp))
            GoldDivider()

            Spacer(Modifier.weight(1f))

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
                    text = "PLAY",
                    color = DeepBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "LEADERBOARD",
                color = GoldDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .clickable { onLeaderboardClicked() }
                    .padding(8.dp)
            )
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
