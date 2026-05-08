package app.krafted.jewelplinko.game

import app.krafted.jewelplinko.R

object BoardLayout {

    const val PEG_ROWS = 8
    const val SLOT_COUNT = 9

    private const val TOP_MARGIN_FRAC = 0.12f
    private const val SLOT_BAND_FRAC = 0.14f
    private const val SLOT_GAP_FRAC = 0.01f

    private val DEFAULT_MULTIPLIERS = intArrayOf(-10, -5, 5, 10, 50, 10, 5, -5, -10)
    private val DEFAULT_SYMBOLS = intArrayOf(
        R.drawable.plin_sym_1,
        R.drawable.plin_sym_2,
        R.drawable.plin_sym_3,
        R.drawable.plin_sym_4,
        R.drawable.plin_sym_7,
        R.drawable.plin_sym_4,
        R.drawable.plin_sym_3,
        R.drawable.plin_sym_2,
        R.drawable.plin_sym_1
    )

    fun pegRadius(boardWidth: Float): Float = boardWidth / 60f

    fun ballRadius(boardWidth: Float): Float = boardWidth / 36f

    fun slotsTopY(boardHeight: Float): Float =
        boardHeight - boardHeight * SLOT_BAND_FRAC

    fun dropY(boardHeight: Float): Float = boardHeight * (TOP_MARGIN_FRAC * 0.4f)

    fun aimRange(boardWidth: Float): ClosedFloatingPointRange<Float> {
        val margin = boardWidth * 0.15f
        return margin..(boardWidth - margin)
    }

    fun buildPegs(boardWidth: Float, boardHeight: Float): List<Peg> {
        val pegRadius = pegRadius(boardWidth)
        val topY = boardHeight * TOP_MARGIN_FRAC
        val slotsTop = slotsTopY(boardHeight)
        val pegBandHeight = slotsTop - topY - boardHeight * SLOT_GAP_FRAC
        val rowSpacing = pegBandHeight / (PEG_ROWS - 1)

        val wideCount = SLOT_COUNT + 1
        val narrowCount = SLOT_COUNT
        val wideSpacing = boardWidth / wideCount
        val pegs = ArrayList<Peg>(PEG_ROWS * wideCount)

        for (row in 0 until PEG_ROWS) {
            val y = topY + row * rowSpacing
            val isWide = row % 2 == 0
            val count = if (isWide) wideCount else narrowCount
            val startX = if (isWide) wideSpacing * 0.5f else wideSpacing
            for (col in 0 until count) {
                val x = startX + col * wideSpacing
                pegs.add(Peg(x, y, pegRadius))
            }
        }
        return pegs
    }

    fun buildSlots(
        boardWidth: Float,
        boardHeight: Float,
        multipliers: IntArray = DEFAULT_MULTIPLIERS,
        symbols: IntArray = DEFAULT_SYMBOLS
    ): List<PrizeSlot> {
        require(multipliers.size == SLOT_COUNT) { "multipliers must have $SLOT_COUNT entries" }
        require(symbols.size == SLOT_COUNT) { "symbols must have $SLOT_COUNT entries" }

        val slotWidth = boardWidth / SLOT_COUNT
        val top = slotsTopY(boardHeight)
        val bottom = boardHeight
        return (0 until SLOT_COUNT).map { i ->
            val left = i * slotWidth
            PrizeSlot(
                index = i,
                left = left,
                right = if (i == SLOT_COUNT - 1) boardWidth else left + slotWidth,
                top = top,
                bottom = bottom,
                multiplier = multipliers[i],
                symbolDrawableRes = symbols[i]
            )
        }
    }
}
