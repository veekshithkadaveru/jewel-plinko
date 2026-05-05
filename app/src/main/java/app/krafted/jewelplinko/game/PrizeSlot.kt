package app.krafted.jewelplinko.game

data class PrizeSlot(
    val index: Int,
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
    val multiplier: Int,
    val symbolDrawableRes: Int
) {
    fun contains(x: Float): Boolean = x in left..right
    val centerX: Float get() = (left + right) * 0.5f
}
