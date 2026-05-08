package app.krafted.jewelplinko.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import app.krafted.jewelplinko.R
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max

class PlinkoBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private companion object {
        const val SETTLED_REMOVAL_DELAY_NANOS = 600_000_000L
        const val INITIAL_BALL_VY = 80f
    }

    private var thread: GameThread? = null

    private var boardWidth: Float = 0f
    private var boardHeight: Float = 0f

    private var pegs: List<Peg> = emptyList()
    private var slots: List<PrizeSlot> = emptyList()
    private var physics: PhysicsEngine? = null

    private val balls = CopyOnWriteArrayList<Ball>()

    @Volatile
    private var aimX: Float = 0f
    @Volatile
    private var aimRange: ClosedFloatingPointRange<Float> = 0f..0f

    private var pegBitmap: Bitmap? = null
    private var ballBitmap: Bitmap? = null
    private val symbolBitmaps = HashMap<Int, Bitmap>()

    private val srcRect = Rect()
    private val dstRect = RectF()

    private val pegBasePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E8F1FF") }
    private val pegRimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#7AA9FF")
    }
    private val slotFramePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#330B0220")
    }
    private val slotDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#66FFD27A")
        style = Paint.Style.STROKE
    }
    private val slotMultiplierPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFF6C66B")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val aimLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CCFFD27A")
        style = Paint.Style.STROKE
    }
    private val aimDotPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FFFFD27A") }
    private val backgroundFallbackPaint = Paint().apply { color = Color.parseColor("#FF0B0220") }

    private var onBallLanded: ((Ball) -> Unit)? = null

    init {
        holder.addCallback(this)
        setZOrderOnTop(true)
        holder.setFormat(android.graphics.PixelFormat.TRANSPARENT)
        isFocusable = true
    }

    fun setOnBallLandedListener(listener: ((Ball) -> Unit)?) {
        onBallLanded = listener
    }

    fun setAimX(x: Float) {
        val r = aimRange
        aimX = x.coerceIn(r.start, r.endInclusive)
    }

    fun nudgeAim(delta: Float) {
        setAimX(aimX + delta)
    }

    fun aimRange(): ClosedFloatingPointRange<Float> = aimRange

    fun activeBallCount(): Int = balls.count { !it.landed }

    fun slotMultiplierAt(index: Int): Int = slots.getOrNull(index)?.multiplier ?: 0

    fun dropBall(symbolRes: Int = R.drawable.plin_sym_1): Boolean {
        val w = boardWidth
        val h = boardHeight
        if (w <= 0f || h <= 0f) return false
        val ball = Ball(
            x = aimX,
            y = BoardLayout.dropY(h),
            vx = 0f,
            vy = INITIAL_BALL_VY,
            radius = BoardLayout.ballRadius(w),
            symbolDrawableRes = symbolRes
        )
        balls.add(ball)
        return true
    }

    fun reset() {
        balls.clear()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread = GameThread(holder, ::update, ::drawFrame).also { it.startLoop() }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        boardWidth = width.toFloat()
        boardHeight = height.toFloat()
        rebuildBoard()
        rebuildBitmaps(width, height)
        configurePaints()
        val r = aimRange
        if (aimX < r.start || aimX > r.endInclusive) aimX = (r.start + r.endInclusive) * 0.5f
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread?.stopLoop()
        try {
            thread?.join()
        } catch (_: InterruptedException) {
        }
        thread = null
        recycleBitmaps()
    }

    private fun rebuildBoard() {
        pegs = BoardLayout.buildPegs(boardWidth, boardHeight)
        slots = BoardLayout.buildSlots(boardWidth, boardHeight)
        physics = PhysicsEngine(pegs, slots, boardWidth, boardHeight)
        aimRange = BoardLayout.aimRange(boardWidth)
    }

    private fun rebuildBitmaps(width: Int, height: Int) {
        recycleBitmaps()
        val pegSize = (BoardLayout.pegRadius(boardWidth) * 2.6f).toInt().coerceAtLeast(1)
        pegBitmap = decodeAndScale(R.drawable.peg_bitmap, pegSize, pegSize)
        val ballSize = (BoardLayout.ballRadius(boardWidth) * 2f).toInt().coerceAtLeast(1)
        ballBitmap = decodeAndScale(R.drawable.ball_custom, ballSize, ballSize)
        val symbolSize = ballSize
        val symbolRes = intArrayOf(
            R.drawable.plin_sym_1,
            R.drawable.plin_sym_2,
            R.drawable.plin_sym_3,
            R.drawable.plin_sym_4,
            R.drawable.plin_sym_5,
            R.drawable.plin_sym_6,
            R.drawable.plin_sym_7
        )
        for (res in symbolRes) {
            decodeAndScale(res, symbolSize, symbolSize)?.let { symbolBitmaps[res] = it }
        }
    }

    private fun decodeAndScale(resId: Int, w: Int, h: Int): Bitmap? {
        val outWidth = max(w, 1)
        val outHeight = max(h, 1)
        val raw = BitmapFactory.decodeResource(resources, resId)
        if (raw != null) {
            return Bitmap.createScaledBitmap(raw, outWidth, outHeight, true).also {
                if (it !== raw) raw.recycle()
            }
        }

        val drawable = ResourcesCompat.getDrawable(resources, resId, null) ?: return null
        val bitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, outWidth, outHeight)
        drawable.draw(canvas)
        return bitmap
    }

    private fun recycleBitmaps() {
        pegBitmap?.recycle()
        pegBitmap = null
        ballBitmap?.recycle()
        ballBitmap = null
        for (b in symbolBitmaps.values) b.recycle()
        symbolBitmaps.clear()
    }

    private fun configurePaints() {
        val pegRadius = BoardLayout.pegRadius(boardWidth)
        pegRimPaint.strokeWidth = max(1f, pegRadius * 0.25f)
        slotDividerPaint.strokeWidth = max(1f, boardWidth * 0.0035f)
        slotMultiplierPaint.textSize = boardHeight * 0.022f
        aimLinePaint.strokeWidth = max(2f, boardWidth * 0.0045f)
        aimLinePaint.pathEffect = DashPathEffect(
            floatArrayOf(boardHeight * 0.012f, boardHeight * 0.012f), 0f
        )
    }

    private fun update(dt: Float) {
        val engine = physics ?: return
        val target = aimX
        val now = System.nanoTime()
        val toRemove = ArrayList<Ball>()
        for (ball in balls) {
            val wasLanded = ball.landed
            engine.step(ball, dt, target)
            if (!wasLanded && ball.landed) {
                ball.landedAtNanos = now
                onBallLanded?.invoke(ball)
            }
            if (ball.landed && now - ball.landedAtNanos > SETTLED_REMOVAL_DELAY_NANOS) {
                toRemove.add(ball)
            }
        }
        if (toRemove.isNotEmpty()) balls.removeAll(toRemove)
    }

    private fun drawFrame(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
        drawSlotBand(canvas)
        drawPegs(canvas)
        drawAimIndicator(canvas)
        drawBalls(canvas)
    }

    private fun drawSlotBand(canvas: Canvas) {
        if (slots.isEmpty()) return
        val top = slots.first().top
        val bottom = slots.first().bottom
        canvas.drawRect(0f, top, boardWidth, bottom, slotFramePaint)

        val bandHeight = bottom - top
        val symbolDraw = bandHeight * 0.55f
        val symbolHalf = symbolDraw * 0.5f
        val labelBaselineY = bottom - bandHeight * 0.12f

        for (slot in slots) {
            if (slot.index > 0) {
                canvas.drawLine(slot.left, top, slot.left, bottom, slotDividerPaint)
            }
            val cx = slot.centerX
            val symbolCy = top + bandHeight * 0.38f
            val bmp = symbolBitmaps[slot.symbolDrawableRes]
            if (bmp != null) {
                dstRect.set(
                    cx - symbolHalf,
                    symbolCy - symbolHalf,
                    cx + symbolHalf,
                    symbolCy + symbolHalf
                )
                srcRect.set(0, 0, bmp.width, bmp.height)
                canvas.drawBitmap(bmp, srcRect, dstRect, null)
            }
            canvas.drawText("${slot.multiplier}x", cx, labelBaselineY, slotMultiplierPaint)
        }
    }

    private fun drawPegs(canvas: Canvas) {
        for (peg in pegs) {
            val bitmap = pegBitmap
            if (bitmap != null) {
                val r = peg.radius * 1.3f
                srcRect.set(0, 0, bitmap.width, bitmap.height)
                dstRect.set(peg.x - r, peg.y - r, peg.x + r, peg.y + r)
                canvas.drawBitmap(bitmap, srcRect, dstRect, null)
            } else {
                drawFallbackPeg(canvas, peg)
            }
        }
    }

    private fun drawFallbackPeg(canvas: Canvas, peg: Peg) {
        val shader = RadialGradient(
            peg.x - peg.radius * 0.35f,
            peg.y - peg.radius * 0.35f,
            peg.radius * 1.6f,
            Color.WHITE,
            Color.parseColor("#7AA9FF"),
            Shader.TileMode.CLAMP
        )
        pegBasePaint.shader = shader
        canvas.drawCircle(peg.x, peg.y, peg.radius, pegBasePaint)
        pegBasePaint.shader = null
        canvas.drawCircle(peg.x, peg.y, peg.radius, pegRimPaint)
    }

    private fun drawAimIndicator(canvas: Canvas) {
        val topY = BoardLayout.dropY(boardHeight)
        val bottomY = BoardLayout.slotsTopY(boardHeight)
        canvas.drawLine(aimX, topY, aimX, bottomY, aimLinePaint)
        canvas.drawCircle(aimX, topY, max(4f, boardWidth * 0.012f), aimDotPaint)
    }

    private fun drawBalls(canvas: Canvas) {
        val bmp = ballBitmap ?: return
        for (ball in balls) {
            val r = ball.radius
            val left = ball.x - r
            val top = ball.y - r
            dstRect.set(left, top, left + 2f * r, top + 2f * r)
            srcRect.set(0, 0, bmp.width, bmp.height)
            val alpha = if (ball.landed) {
                val age = System.nanoTime() - ball.landedAtNanos
                val t = (age.toFloat() / SETTLED_REMOVAL_DELAY_NANOS).coerceIn(0f, 1f)
                ((1f - t) * 255f).toInt().coerceIn(0, 255)
            } else 255
            val paint: Paint? = if (alpha < 255) {
                Paint(Paint.FILTER_BITMAP_FLAG).apply { this.alpha = alpha }
            } else null
            canvas.drawBitmap(bmp, srcRect, dstRect, paint)
        }
    }

}
