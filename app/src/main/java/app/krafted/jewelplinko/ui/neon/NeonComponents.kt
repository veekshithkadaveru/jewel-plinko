package app.krafted.jewelplinko.ui.neon

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jewelplinko.R
import kotlin.random.Random

@Composable
fun GemText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 50.sp,
    letterSpacing: androidx.compose.ui.unit.TextUnit = 2.sp,
    textAlign: TextAlign = TextAlign.Center,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = Color(0xFF2A0A00),
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = letterSpacing,
            textAlign = textAlign,
            style = TextStyle(
                drawStyle = Stroke(width = 8f, join = androidx.compose.ui.graphics.StrokeJoin.Round),
            ),
        )
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = letterSpacing,
            textAlign = textAlign,
            style = TextStyle(brush = NeonBrushes.GemText),
        )
    }
}

@Composable
fun GemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    palette: GemPalette = GemPalette.Pink,
    big: Boolean = false,
    enabled: Boolean = true,
    glow: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressOffset by animateFloatAsState(
        targetValue = if (pressed && enabled) 3f else 0f,
        animationSpec = tween(80),
        label = "btnPress",
    )
    val glowAlpha = if (glow && enabled) {
        val infinite = rememberInfiniteTransition(label = "btnGlow")
        infinite.animateFloat(
            initialValue = 0.55f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(tween(1600), repeatMode = RepeatMode.Reverse),
            label = "glow",
        ).value
    } else 0.55f

    val height = if (big) 64.dp else 48.dp
    val fontSize = if (big) 22.sp else 16.sp
    val shadowDepth = if (big) 6.dp else 4.dp

    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier = modifier
            .height(height + shadowDepth)
            .alpha(if (enabled) 1f else 0.45f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = shadowDepth)
                .clip(shape)
                .background(palette.shadow),
        )
        if (glow && enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = shadowDepth)
                    .alpha(glowAlpha * 0.45f)
                    .clip(shape)
                    .background(palette.mid),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = pressOffset.dp, bottom = (shadowDepth.value - pressOffset).dp.coerceAtLeast(0.dp))
                .clip(shape)
                .background(palette.verticalBrush())
                .border(2.dp, Color(0x59FFFFFF), shape)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                ) { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                style = TextStyle(shadow = androidx.compose.ui.graphics.Shadow(Color(0x66000000), Offset(0f, 2f), 0f)),
            )
        }
    }
}

@Composable
fun CoinPill(
    value: Int,
    modifier: Modifier = Modifier,
) {
    val display = remember(value) { value.formatGrouped() }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(NeonBrushes.CoinPill)
            .border(2.dp, Color(0x8CFFB450), RoundedCornerShape(50))
            .padding(start = 6.dp, end = 14.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.coin_bag),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = display,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 8.dp),
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
fun NeonBackground(
    bg: Int = R.drawable.bg_neon,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(Neon.Black)) {
        Image(
            painter = painterResource(id = bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(radius = 20.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color(0xD90A041E),
                        0.5f to Color(0x8C28084F),
                        1f to Color(0xE60A041E),
                    )
                ),
        )
        content()
    }
}

@Composable
fun SparkleField(count: Int = 18, modifier: Modifier = Modifier) {
    val seeds = remember(count) {
        List(count) {
            Sparkle(
                xRel = Random.nextFloat(),
                yRel = Random.nextFloat(),
                radius = 1f + Random.nextFloat() * 3f,
                phase = Random.nextFloat(),
                durMs = 1500 + Random.nextInt(2500),
                hue = SPARKLE_HUES[it % SPARKLE_HUES.size],
            )
        }
    }
    val infinite = rememberInfiniteTransition(label = "sparkle")
    val time by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing), repeatMode = RepeatMode.Restart,
        ),
        label = "sparkleTime",
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        seeds.forEach { s ->
            val cycle = ((time + s.phase) % 1f)
            val a = if (cycle < 0.5f) cycle * 2f else (1f - cycle) * 2f
            val cx = s.xRel * size.width
            val cy = s.yRel * size.height
            drawCircle(color = s.hue.copy(alpha = a), radius = s.radius * 4f, center = Offset(cx, cy), alpha = 0.35f * a)
            drawCircle(color = s.hue.copy(alpha = a), radius = s.radius, center = Offset(cx, cy))
        }
    }
}

private data class Sparkle(
    val xRel: Float,
    val yRel: Float,
    val radius: Float,
    val phase: Float,
    val durMs: Int,
    val hue: Color,
)

private val SPARKLE_HUES = listOf(
    Color.White,
    Color(0xFFFF9AF0),
    Color(0xFF9AFFFF),
    Color(0xFFFFE07A),
)

@Composable
fun Confetti(active: Boolean, count: Int = 30, modifier: Modifier = Modifier) {
    if (!active) return
    val pieces = remember(count) {
        List(count) {
            ConfettiPiece(
                xRel = Random.nextFloat(),
                delay = Random.nextFloat() * 0.6f,
                duration = 1.6f + Random.nextFloat() * 1.4f,
                hue = CONFETTI_HUES[it % CONFETTI_HUES.size],
                size = 6 + Random.nextFloat() * 8f,
                rotation = Random.nextFloat() * 360f,
            )
        }
    }
    val infinite = rememberInfiniteTransition(label = "confetti")
    val time by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "confettiTime",
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        pieces.forEach { p ->
            val phased = ((time - p.delay / 3.2f) % 1f + 1f) % 1f
            val y = -20f + phased * (size.height + 200f)
            val rot = (p.rotation + phased * 720f) * (Math.PI / 180f).toFloat()
            val alpha = (1f - phased * 0.8f).coerceAtLeast(0f)
            val cx = p.xRel * size.width
            val w = p.size
            val h = p.size * 0.4f
            val cs = kotlin.math.cos(rot)
            val sn = kotlin.math.sin(rot)
            val hx = w / 2f
            val hy = h / 2f
            val pts = listOf(
                Offset(-hx, -hy), Offset(hx, -hy), Offset(hx, hy), Offset(-hx, hy)
            ).map { Offset(cs * it.x - sn * it.y + cx, sn * it.x + cs * it.y + y) }
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(pts[0].x, pts[0].y)
                pts.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            }
            drawPath(path, color = p.hue.copy(alpha = alpha))
        }
    }
}

private data class ConfettiPiece(
    val xRel: Float,
    val delay: Float,
    val duration: Float,
    val hue: Color,
    val size: Float,
    val rotation: Float,
)

private val CONFETTI_HUES = listOf(
    Color(0xFFFF3A8A),
    Color(0xFFFFA31A),
    Color(0xFFFFD84A),
    Color(0xFF9B6BFF),
    Color(0xFF4DC3FF),
    Color(0xFF7FFFA0),
)

@Composable
fun GlowDot(size: Dp = 14.dp, used: Boolean = false, modifier: Modifier = Modifier) {
    val base = modifier.size(size).clip(CircleShape)
    val filled = if (used) {
        base.background(Color(0x26FFFFFF))
    } else {
        base.background(
            Brush.radialGradient(
                0f to Color.White,
                0.55f to Color(0xFFAD5FFF),
                1f to Color(0xFF5A0AAA),
            )
        )
    }
    Box(
        modifier = filled.border(
            if (used) 1.dp else 0.dp,
            if (used) Color(0x33FFFFFF) else Color.Transparent,
            CircleShape,
        ),
    )
}

@Composable
fun PulsingHalo(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF50DC),
    base: Float = 0.35f,
    peak: Float = 0.65f,
) {
    val infinite = rememberInfiniteTransition(label = "halo")
    val a by infinite.animateFloat(
        initialValue = base,
        targetValue = peak,
        animationSpec = infiniteRepeatable(tween(1800), repeatMode = RepeatMode.Reverse),
        label = "haloA",
    )
    val s by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1800), repeatMode = RepeatMode.Reverse),
        label = "haloS",
    )
    Box(
        modifier = modifier
            .scale(s)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    0f to color.copy(alpha = a),
                    0.65f to color.copy(alpha = 0f),
                )
            ),
    )
}

@Composable
fun FloatingImage(
    @androidx.annotation.DrawableRes res: Int,
    modifier: Modifier = Modifier,
    amplitude: Dp = 8.dp,
    durationMs: Int = 2800,
) {
    val infinite = rememberInfiniteTransition(label = "float")
    val offset by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMs), repeatMode = RepeatMode.Reverse),
        label = "floatY",
    )
    Image(
        painter = painterResource(id = res),
        contentDescription = null,
        modifier = modifier.offset(y = amplitude * offset),
    )
}

fun Int.formatGrouped(): String {
    val absVal = kotlin.math.abs(this)
    if (absVal < 1000) return toString()
    val s = absVal.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.length - 1 downTo 0) {
        sb.append(s[i]); c++
        if (c % 3 == 0 && i != 0) sb.append(',')
    }
    val res = sb.reverse().toString()
    return if (this < 0) "-$res" else res
}

@Composable
fun BackPill(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(NeonBrushes.CoinPill)
            .border(2.dp, Color(0x66FFB4FF), CircleShape)
            .clickable { onBack() },
        contentAlignment = Alignment.Center,
    ) {
        Text("‹", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun NeonChip(
    text: String,
    valueColor: Color = Color(0xFFFFD84A),
    modifier: Modifier = Modifier,
    valueText: String? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(NeonBrushes.CoinPill)
            .border(2.dp, Color(0x66FFB4FF), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        if (valueText != null) {
            Text(
                "  $valueText",
                color = valueColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}
