package app.krafted.jewelplinko.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.data.db.WinRecord
import app.krafted.jewelplinko.ui.neon.BackPill
import app.krafted.jewelplinko.ui.neon.Neon
import app.krafted.jewelplinko.ui.neon.NeonBackground
import app.krafted.jewelplinko.ui.neon.PulsingHalo
import app.krafted.jewelplinko.ui.neon.SparkleField
import app.krafted.jewelplinko.ui.neon.formatGrouped
import app.krafted.jewelplinko.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(vm: GameViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadLeaderboard() }
    val topWins by vm.topWins.collectAsState()

    NeonBackground(bg = R.drawable.bg_neon2) {
        SparkleField(count = 16, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackPill(onBack = onBack)
                Text(
                    "BIGGEST WINS",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Box(modifier = Modifier.size(40.dp))
            }

            Box(contentAlignment = Alignment.Center) {
                PulsingHalo(modifier = Modifier.size(140.dp), color = Color(0xFFFFB428))
                Text("🏆", fontSize = 80.sp)
            }

            Spacer(Modifier.height(16.dp))

            if (topWins.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("💎", fontSize = 50.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No wins yet — drop a ball to start!",
                        color = Color(0x99FFFFFF),
                        fontSize = 14.sp,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp),
                ) {
                    itemsIndexed(topWins) { index, record ->
                        LeaderboardRow(rank = index + 1, record = record)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(rank: Int, record: WinRecord) {
    val isTop = rank == 1
    val medal = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }
    val rowBg = if (isTop) {
        Brush.horizontalGradient(
            listOf(Color(0x40FFB428), Color(0x26FF50B4))
        )
    } else {
        Brush.verticalGradient(listOf(Color(0x0FFFFFFF), Color(0x05FFFFFF)))
    }
    val rowBorder = if (isTop) Color(0x8CFFC828) else Color(0x33FFB4FF)
    val dateText = remember(record.timestampMillis) {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(record.timestampMillis))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(rowBg)
            .border(if (isTop) 1.5.dp else 1.dp, rowBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            medal,
            color = if (isTop) Neon.GemGold else Color(0xCCFFFFFF),
            fontSize = if (rank <= 3) 22.sp else 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.size(36.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    record.winnings.formatGrouped(),
                    color = Neon.GemGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    " coins",
                    color = Color(0xB3FFFFFF),
                    fontSize = 12.sp,
                )
            }
            Text(
                "${record.playerName} · ${record.multiplier}x · $dateText",
                color = Color(0xA6FFFFFF),
                fontSize = 11.sp,
            )
        }
        Image(
            painter = painterResource(id = R.drawable.coin_bag),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
    }
}
