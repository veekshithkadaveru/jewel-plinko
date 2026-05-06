package app.krafted.jewelplinko.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.data.db.WinRecord
import app.krafted.jewelplinko.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DeepBackground = Color(0xFF0B0220)
private val CardBackground = Color(0xFF160833)
private val Gold = Color(0xFFF6C66B)
private val GoldDark = Color(0xFFB8945A)
private val GoldShimmer = Color(0xFFFFF1D0)
private val DimWhite = Color(0xFFCCBBDD)

@Composable
fun LeaderboardScreen(vm: GameViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadLeaderboard() }
    val topWins by vm.topWins.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBack() }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.back_button),
                        color = Gold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )
                }
                Text(
                    text = stringResource(R.string.leaderboard_title),
                    color = GoldShimmer,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

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

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.leaderboard_subtitle),
                color = DimWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                if (topWins.isEmpty()) {
                    Text(
                        text = stringResource(R.string.leaderboard_empty),
                        color = DimWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        itemsIndexed(topWins) { index, record ->
                            LeaderboardRow(rank = index + 1, record = record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(rank: Int, record: WinRecord) {
    val isTop = rank == 1
    val rowBackground = if (isTop) Gold.copy(alpha = 0.08f) else Color.Transparent
    val dateText = remember(record.timestampMillis) {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(record.timestampMillis))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            color = if (isTop) GoldShimmer else GoldDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.weight(0.6f)
        )

        Image(
            painter = painterResource(record.symbolDrawableRes),
            contentDescription = stringResource(R.string.result_symbol_desc),
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.size(10.dp))

        Column(modifier = Modifier.weight(1.6f)) {
            Text(
                text = record.playerName,
                color = GoldShimmer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "${record.multiplier}x · $dateText",
                color = DimWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                maxLines = 1
            )
        }

        Text(
            text = "+${record.winnings}",
            color = Gold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
