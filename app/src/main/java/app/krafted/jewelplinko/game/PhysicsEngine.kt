package app.krafted.jewelplinko.game

import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.random.Random

class PhysicsEngine(
    private val pegs: List<Peg>,
    private val slots: List<PrizeSlot>,
    private val boardWidth: Float,
    private val boardHeight: Float,
    private val random: Random = Random.Default
) {

    companion object {
        const val GRAVITY = 1800f
        const val RESTITUTION = 0.55f
        const val WALL_RESTITUTION = 0.6f
        const val MAX_FALL_SPEED = 1400f
        const val AIM_BIAS_STRENGTH = 250f
        const val AIM_BIAS_DECAY_AT = 0.6f
        const val RANDOM_NUDGE = 40f
    }

    fun step(ball: Ball, dtSeconds: Float, aimTargetX: Float) {
        if (ball.landed) return

        val biasFade = aimBiasFade(ball.y)
        val dx = aimTargetX - ball.x
        val biasAccel = if (biasFade > 0f && dx != 0f) {
            AIM_BIAS_STRENGTH * sign(dx) * biasFade
        } else 0f

        ball.vx += biasAccel * dtSeconds
        ball.vy += GRAVITY * dtSeconds
        if (ball.vy > MAX_FALL_SPEED) ball.vy = MAX_FALL_SPEED

        ball.x += ball.vx * dtSeconds
        ball.y += ball.vy * dtSeconds

        resolveWalls(ball)
        resolvePegs(ball)
        resolveLanding(ball)
    }

    private fun aimBiasFade(ballY: Float): Float {
        val cutoff = AIM_BIAS_DECAY_AT * boardHeight
        if (ballY >= cutoff) return 0f
        val t = ballY / cutoff
        return (1f - t).coerceIn(0f, 1f)
    }

    private fun resolveWalls(ball: Ball) {
        if (ball.x - ball.radius < 0f) {
            ball.x = ball.radius
            if (ball.vx < 0f) ball.vx = -ball.vx * WALL_RESTITUTION
        } else if (ball.x + ball.radius > boardWidth) {
            ball.x = boardWidth - ball.radius
            if (ball.vx > 0f) ball.vx = -ball.vx * WALL_RESTITUTION
        }
    }

    private fun resolvePegs(ball: Ball) {
        for (peg in pegs) {
            val dx = ball.x - peg.x
            val dy = ball.y - peg.y
            val minDist = ball.radius + peg.radius
            val distSq = dx * dx + dy * dy
            if (distSq >= minDist * minDist || distSq == 0f) continue

            val dist = sqrt(distSq)
            val nx = dx / dist
            val ny = dy / dist

            val overlap = minDist - dist
            ball.x += nx * overlap
            ball.y += ny * overlap

            val vDotN = ball.vx * nx + ball.vy * ny
            if (vDotN < 0f) {
                ball.vx = (ball.vx - 2f * vDotN * nx) * RESTITUTION
                ball.vy = (ball.vy - 2f * vDotN * ny) * RESTITUTION

                val nudge = (random.nextFloat() * 2f - 1f) * RANDOM_NUDGE
                ball.vx += -ny * nudge
                ball.vy += nx * nudge
            }
        }
    }

    private fun resolveLanding(ball: Ball) {
        val slotsTop = BoardLayout.slotsTopY(boardHeight)
        if (ball.y < slotsTop) return
        val slot = slots.firstOrNull { it.contains(ball.x) } ?: slots.last()
        ball.landed = true
        ball.landedSlotIndex = slot.index
        ball.vx = 0f
        ball.vy = 0f
        ball.y = slotsTop + (ball.bottomFromTop())
    }

    private fun Ball.bottomFromTop(): Float = radius
}
