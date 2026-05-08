package app.krafted.jewelplinko.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.ui.neon.BackPill
import app.krafted.jewelplinko.ui.neon.CoinPill
import app.krafted.jewelplinko.ui.neon.FloatingImage
import app.krafted.jewelplinko.ui.neon.GemButton
import app.krafted.jewelplinko.ui.neon.GemPalette
import app.krafted.jewelplinko.ui.neon.GemText
import app.krafted.jewelplinko.ui.neon.GlowDot
import app.krafted.jewelplinko.ui.neon.Neon
import app.krafted.jewelplinko.ui.neon.NeonBackground
import app.krafted.jewelplinko.ui.neon.SparkleField

private val BetOptions = listOf(10, 25, 50, 100, 250, 500)
private val BallPackages = listOf(1, 3, 5)

@Composable
fun BetScreen(
    coinBalance: Int,
    onStartSession: (bet: Int, ballPackage: Int) -> Unit,
    onBack: () -> Unit,
) {
    var selectedBetIndex by remember { mutableIntStateOf(2) }
    var selectedBalls by remember { mutableIntStateOf(3) }

    val bet = BetOptions[selectedBetIndex]
    val totalCost = bet * selectedBalls
    val canAfford = coinBalance >= totalCost

    NeonBackground(bg = R.drawable.bg_neon2) {
        SparkleField(count = 16, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackPill(onBack = onBack)
                Text(
                    text = "PLACE YOUR BET",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                CoinPill(value = coinBalance)
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                FloatingImage(
                    res = R.drawable.gift_blue,
                    modifier = Modifier.size(130.dp),
                    amplitude = 6.dp,
                    durationMs = 2400,
                )
            }

            Spacer(Modifier.height(8.dp))
            SectionLabel("BET AMOUNT")
            Spacer(Modifier.height(10.dp))
            BetStepper(
                bet = bet,
                onDec = { selectedBetIndex = (selectedBetIndex - 1).coerceAtLeast(0) },
                onInc = { selectedBetIndex = (selectedBetIndex + 1).coerceAtMost(BetOptions.lastIndex) },
            )

            Spacer(Modifier.height(18.dp))
            SectionLabel("BALL PACKAGE")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BallPackages.forEach { n ->
                    BallPackageCard(
                        count = n,
                        selected = selectedBalls == n,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedBalls = n },
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("POSSIBLE MULTIPLIERS")
            Spacer(Modifier.height(10.dp))
            MultiplierStrip()

            Spacer(Modifier.weight(1f))

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x4D000000))
                        .border(1.dp, Color(0x33FFB4FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "TOTAL COST",
                        color = Color(0xBFFFFFFF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = totalCost.toString(),
                        color = if (canAfford) Neon.GemGold else Color(0xFFFF6080),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
                Spacer(Modifier.height(12.dp))
                GemButton(
                    text = if (canAfford) "DROP THE BALLS!" else "NOT ENOUGH COINS",
                    onClick = { if (canAfford) onStartSession(bet, selectedBalls) },
                    palette = GemPalette.Pink,
                    big = true,
                    glow = canAfford,
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = Color(0xB3FFFFFF),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

@Composable
private fun BetStepper(bet: Int, onDec: () -> Unit, onInc: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x14FFFFFF), Color(0x05FFFFFF))
                )
            )
            .border(1.5.dp, Color(0x4DFFB4FF), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StepperButton(symbol = "−", onClick = onDec)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GemText(text = bet.toString(), fontSize = 38.sp)
            Text(
                "COINS",
                color = Color(0x99FFFFFF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
        }
        StepperButton(symbol = "+", onClick = onInc)
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(listOf(Neon.PinkLight, Neon.PinkDark))
            )
            .border(2.dp, Color(0x66FFFFFF), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun BallPackageCard(
    count: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = tween(150),
        label = "ballSel",
    )
    val bg = if (selected) {
        Brush.verticalGradient(listOf(Neon.PinkLight, Neon.PinkDark))
    } else {
        Brush.verticalGradient(listOf(Color(0x14FFFFFF), Color(0x05FFFFFF)))
    }
    val border = if (selected) Color.White else Color(0x4DFFB4FF)
    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(if (selected) 2.dp else 1.5.dp, border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(count) { GlowDot(size = 12.dp) }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = if (count == 1) "BALL" else "BALLS",
            color = Color(0xD9FFFFFF),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun MultiplierStrip() {
    val infinite = rememberInfiniteTransition(label = "multGlow")
    val pulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1800), repeatMode = RepeatMode.Reverse),
        label = "multPulse",
    )
    val float by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(2200), repeatMode = RepeatMode.Reverse),
        label = "multFloat",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.multiplier_x10),
            contentDescription = null,
            modifier = Modifier.height(44.dp),
        )
        Image(
            painter = painterResource(id = R.drawable.multiplier_x20),
            contentDescription = null,
            modifier = Modifier
                .height(52.dp)
                .offset(y = float.dp),
        )
        Image(
            painter = painterResource(id = R.drawable.multiplier_x50),
            contentDescription = null,
            modifier = Modifier
                .height(58.dp)
                .scale(pulse),
        )
    }
}
