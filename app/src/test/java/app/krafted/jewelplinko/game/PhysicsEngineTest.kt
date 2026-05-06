package app.krafted.jewelplinko.game

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class PhysicsEngineTest {

    @Test
    fun testGravityAndMaxFallSpeed() {
        val engine = PhysicsEngine(emptyList(), emptyList(), 1000f, 1000f, Random(0))
        val ball = Ball(500f, 0f, 20f, 1)

        engine.step(ball, 1f, 500f) // 1 second step
        
        // After 1 second, vy should be GRAVITY = 1800f, but capped at MAX_FALL_SPEED = 1400f
        assertEquals(PhysicsEngine.MAX_FALL_SPEED, ball.vy, 0.1f)
    }

    @Test
    fun testWallCollision() {
        val engine = PhysicsEngine(emptyList(), emptyList(), 1000f, 1000f, Random(0))
        val ball = Ball(0f, 500f, 20f, 1)
        ball.vx = -100f // Moving left into wall

        engine.step(ball, 0.1f, 500f)

        // Ball should bounce off left wall
        assertTrue("Ball should be inside left wall bounds", ball.x >= ball.radius)
        assertTrue("Ball should have positive x velocity after bounce", ball.vx > 0f)
    }

    @Test
    fun testPegCollision() {
        val pegs = listOf(Peg(500f, 500f, 10f))
        val engine = PhysicsEngine(pegs, emptyList(), 1000f, 1000f, Random(0))
        
        // Drop ball directly on peg
        val ball = Ball(500f, 480f, 10f, 1)
        ball.vy = 100f

        engine.step(ball, 0.1f, 500f)

        // Ball should have collided and bounced (velocity changed or position adjusted)
        assertTrue("Ball should have been pushed away from peg", ball.y < 490f || ball.vy <= 0f)
    }

    @Test
    fun testLanding() {
        val slots = listOf(PrizeSlot(0, 0f, 1000f, 1f))
        val engine = PhysicsEngine(emptyList(), slots, 1000f, 1000f, Random(0))
        
        val ball = Ball(500f, 900f, 20f, 1)
        ball.vy = 100f
        
        // Step enough to land
        engine.step(ball, 1f, 500f)
        
        assertTrue("Ball should be landed", ball.landed)
        assertEquals("Ball should be in slot 0", 0, ball.landedSlotIndex)
        assertEquals("Velocity should be 0", 0f, ball.vx, 0f)
        assertEquals("Velocity should be 0", 0f, ball.vy, 0f)
    }
}
