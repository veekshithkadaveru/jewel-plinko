package app.krafted.jewelplinko.game

import android.graphics.Canvas
import android.view.SurfaceHolder
import java.util.concurrent.atomic.AtomicBoolean

class GameThread(
    private val holder: SurfaceHolder,
    private val onUpdate: (dtSeconds: Float) -> Unit,
    private val onDraw: (Canvas) -> Unit
) : Thread("PlinkoGameThread") {

    companion object {
        private const val TARGET_FRAME_NANOS = 16_666_667L
        private const val MAX_DT_SECONDS = 1f / 30f
    }

    private val running = AtomicBoolean(false)

    fun startLoop() {
        if (running.compareAndSet(false, true)) start()
    }

    fun stopLoop() {
        running.set(false)
    }

    override fun run() {
        var lastNanos = System.nanoTime()
        while (running.get()) {
            val now = System.nanoTime()
            val rawDt = (now - lastNanos) / 1_000_000_000f
            lastNanos = now
            val dt = if (rawDt > MAX_DT_SECONDS) MAX_DT_SECONDS else rawDt

            onUpdate(dt)

            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    synchronized(holder) { onDraw(canvas) }
                }
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: IllegalStateException) {
                    }
                }
            }

            val frameNanos = System.nanoTime() - now
            val sleepNanos = TARGET_FRAME_NANOS - frameNanos
            if (sleepNanos > 0) {
                try {
                    sleep(sleepNanos / 1_000_000L, (sleepNanos % 1_000_000L).toInt())
                } catch (_: InterruptedException) {
                }
            }
        }
    }
}
