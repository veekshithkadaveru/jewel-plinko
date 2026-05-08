package app.krafted.jewelplinko.game

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class PhysicsEngineTest {

    @Test
    fun testGravityAndMaxFallSpeed() {
        val engine = PhysicsEngine(emptyList(), emptyList(), 1000f, 1000f, Random(0))
        val ball = Ball(x = 500f, y = 0f, vx = 0f, vy = 0f, radius = 20f, symbolDrawableRes = 1)

        engine.step(ball, 1f, 500f)
        
        assertEquals(PhysicsEngine.MAX_FALL_SPEED, ball.vy, 0.1f)
    }

    @Test
    fun testWallCollision() {
        val engine = PhysicsEngine(emptyList(), emptyList(), 1000f, 1000f, Random(0))
        val ball = Ball(x = 0f, y = 500f, vx = -100f, vy = 0f, radius = 20f, symbolDrawableRes = 1)

        engine.step(ball, 0.1f, 500f)

        assertTrue("Ball should be inside left wall bounds", ball.x >= ball.radius)
        assertTrue("Ball should have positive x velocity after bounce", ball.vx > 0f)
    }

    @Test
    fun testPegCollision() {
        val pegs = listOf(Peg(500f, 500f, 10f))
        val engine = PhysicsEngine(pegs, emptyList(), 1000f, 1000f, Random(0))
        
        val ball = Ball(x = 500f, y = 480f, vx = 0f, vy = 100f, radius = 10f, symbolDrawableRes = 1)

        engine.step(ball, 0.016f, 500f)

        assertTrue("Ball should have been pushed away from peg", ball.y < 490f || ball.vy <= 0f)
    }

    @Test
    fun testLanding() {
        val slots = listOf(PrizeSlot(index = 0, left = 0f, right = 1000f, top = 950f, bottom = 1000f, multiplier = 1, symbolDrawableRes = 1))
        val engine = PhysicsEngine(emptyList(), slots, 1000f, 1000f, Random(0))
        
        val ball = Ball(x = 500f, y = 900f, vx = 0f, vy = 100f, radius = 20f, symbolDrawableRes = 1)
        
        engine.step(ball, 1f, 500f)
        
        assertTrue("Ball should be landed", ball.landed)
        assertEquals("Ball should be in slot 0", 0, ball.landedSlotIndex)
        assertEquals("Velocity should be 0", 0f, ball.vx, 0f)
        assertEquals("Velocity should be 0", 0f, ball.vy, 0f)
    }
}
