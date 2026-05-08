package app.krafted.jewelplinko.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import app.krafted.jewelplinko.ui.neon.GemText
import app.krafted.jewelplinko.ui.neon.NeonBackground
import app.krafted.jewelplinko.ui.neon.PulsingHalo
import app.krafted.jewelplinko.ui.neon.SparkleField
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(isDataLoaded: Boolean, onSplashComplete: () -> Unit) {
    var iconShown by remember { mutableStateOf(false) }
    var titleShown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300); iconShown = true
        delay(700); titleShown = true
    }

    LaunchedEffect(isDataLoaded, titleShown) {
        if (isDataLoaded && titleShown) {
            delay(1100)
            onSplashComplete()
        }
    }

    val iconScale by animateFloatAsState(
        targetValue = if (iconShown) 1f else 0.4f,
        animationSpec = tween(durationMillis = 700),
        label = "iconScale",
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (iconShown) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "iconAlpha",
    )
    val titleScale by animateFloatAsState(
        targetValue = if (titleShown) 1f else 0.5f,
        animationSpec = tween(durationMillis = 600),
        label = "titleScale",
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleShown) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "titleAlpha",
    )

    NeonBackground(bg = R.drawable.bg_neon) {
        SparkleField(count = 28, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(contentAlignment = Alignment.Center) {
                PulsingHalo(modifier = Modifier.size(260.dp))
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .scale(iconScale)
                        .alpha(iconAlpha)
                        .clip(RoundedCornerShape(40.dp))
                        .border(4.dp, Color(0x40FFFFFF), RoundedCornerShape(40.dp)),
                )
            }
            Spacer(Modifier.height(28.dp))
            Box(
                modifier = Modifier
                    .scale(titleScale)
                    .alpha(titleAlpha),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GemText(text = "JEWEL", fontSize = 56.sp)
                    Spacer(Modifier.height(2.dp))
                    GemText(text = "PLINKO", fontSize = 56.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "LOADING THE GEMS…",
                color = Color(0xD9FFC8FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(titleAlpha),
            )
            Spacer(Modifier.height(12.dp))
            ShimmerBar(modifier = Modifier.alpha(titleAlpha))
        }
    }
}

@Composable
private fun ShimmerBar(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "shimmer")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "shimmerPhase",
    )
    Box(
        modifier = modifier
            .size(width = 200.dp, height = 6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0x1FFFFFFF)),
    ) {
        val brush = Brush.linearGradient(
            0f to Color(0xFFFF3A8A),
            0.5f to Color(0xFFFFA31A),
            1f to Color(0xFFFFD84A),
            start = Offset(-200f + phase * 800f, 0f),
            end = Offset(200f + phase * 800f, 0f),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush),
        )
    }
}
