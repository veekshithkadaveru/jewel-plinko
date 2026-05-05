package app.krafted.jewelplinko.game

class Ball(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    val symbolDrawableRes: Int,
    var landed: Boolean = false,
    var landedSlotIndex: Int = -1,
    var landedAtNanos: Long = 0L
)
