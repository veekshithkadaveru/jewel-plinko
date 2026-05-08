package app.krafted.jewelplinko.ui.neon

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Neon {
    val Black = Color(0xFF07060D)
    val DeepPurple = Color(0xFF1A0530)
    val MidPurple = Color(0xFF40087A)
    val PanelTop = Color(0xFF3C1466)
    val PanelBot = Color(0xFF14042A)

    val Pink = Color(0xFFFF2D8E)
    val PinkLight = Color(0xFFFF63B8)
    val PinkDark = Color(0xFFA01365)
    val PinkShadow = Color(0xFF5A0A3A)

    val Orange = Color(0xFFFF7A1A)
    val OrangeLight = Color(0xFFFFC04D)
    val OrangeDark = Color(0xFFC04500)
    val OrangeShadow = Color(0xFF5A1A00)

    val Blue = Color(0xFF1F7AFF)
    val BlueLight = Color(0xFF5FC8FF)
    val BlueDark = Color(0xFF0A3AA0)
    val BlueShadow = Color(0xFF04195A)

    val Green = Color(0xFF1EC850)
    val GreenLight = Color(0xFF7FFFA0)
    val GreenDark = Color(0xFF0A6020)
    val GreenShadow = Color(0xFF04300A)

    val Purple = Color(0xFF8A3FFF)
    val PurpleLight = Color(0xFFC89AFF)
    val PurpleDark = Color(0xFF4A0AA0)
    val PurpleShadow = Color(0xFF1A0540)

    val GemGold = Color(0xFFFFD84A)
    val GemAmber = Color(0xFFFFA31A)
    val GemRose = Color(0xFFFF5A1F)

    val Sapphire = Color(0xFF4DC3FF)
    val DimText = Color(0xCCFFD8FF)
    val SoftPink = Color(0x66FFB4FF)
}

enum class GemPalette(
    val top: Color,
    val mid: Color,
    val bot: Color,
    val shadow: Color,
) {
    Pink(Neon.PinkLight, Neon.Pink, Neon.PinkDark, Neon.PinkShadow),
    Orange(Neon.OrangeLight, Neon.Orange, Neon.OrangeDark, Neon.OrangeShadow),
    Blue(Neon.BlueLight, Neon.Blue, Neon.BlueDark, Neon.BlueShadow),
    Green(Neon.GreenLight, Neon.Green, Neon.GreenDark, Neon.GreenShadow),
    Purple(Neon.PurpleLight, Neon.Purple, Neon.PurpleDark, Neon.PurpleShadow);

    fun verticalBrush(): Brush = Brush.verticalGradient(
        0f to top, 0.55f to mid, 1f to bot
    )
}

object NeonBrushes {
    val GemText: Brush = Brush.verticalGradient(
        0f to Color(0xFFFFF7C4),
        0.35f to Color(0xFFFFD84A),
        0.7f to Color(0xFFFF8A1F),
        1f to Color(0xFFFF5A1F),
    )

    val PanelStroke: Brush = Brush.verticalGradient(
        listOf(Color(0xF23C1466), Color(0xF214042A))
    )

    val CoinPill: Brush = Brush.verticalGradient(
        listOf(Color(0xF23C1466), Color(0xF214042A))
    )
}
