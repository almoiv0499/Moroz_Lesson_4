package com.example.moroz_lesson_4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class AnalogClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defResAttrs: Int = 0,
) : View(context, attrs, defResAttrs) {

    companion object {
        // Size const
        private const val DEFAULT_SIZE_VALUE = 0
        private const val PADDING = 50
        private const val DIVIDER_BY_HALF = 2

        // Stroke width const
        private const val DEFAULT_STROKE_WIDTH = 0.0F
        private const val CIRCLE_STROKE_WIDTH = 10F
    }

    // Paint circle
    private var circlePaint: Paint? = null
    private var mWidth = DEFAULT_SIZE_VALUE
    private var mHeight = DEFAULT_SIZE_VALUE
    private var centreX = DEFAULT_SIZE_VALUE
    private var centreY = DEFAULT_SIZE_VALUE
    private var padding = DEFAULT_SIZE_VALUE
    private var radius = DEFAULT_SIZE_VALUE
    private var minimum = DEFAULT_SIZE_VALUE

    init {
        initAllPaints()
        setCustomAttrs(attrs = attrs)
    }

    private fun initParams() {
        mWidth = width
        mHeight = height
        padding = PADDING

        centreX = mWidth / DIVIDER_BY_HALF
        centreY = mHeight / DIVIDER_BY_HALF
        minimum = min(mHeight, mWidth)
        radius = minimum / DIVIDER_BY_HALF - padding

    }

    private fun setCustomAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnalogClockView)

        with(typedArray) {
            // Color for clock hands
            val hourHandColor = getColor(R.styleable.AnalogClockView_colorHourHand, Color.RED)
            val minuteHandColor = getColor(R.styleable.AnalogClockView_colorMinuteHand, Color.BLUE)
            val secondHandColor = getColor(R.styleable.AnalogClockView_colorSecondHand, Color.GRAY)

            // Stroke width for clock hands
            val hourHandStrokeWidth =
                getFloat(R.styleable.AnalogClockView_strokeWidthHourHand, DEFAULT_STROKE_WIDTH)
            val minuteHandStrokeWidth =
                getFloat(R.styleable.AnalogClockView_strokeWidthMinuteHand, DEFAULT_STROKE_WIDTH)
            val secondHandStrokeWidth =
                getFloat(R.styleable.AnalogClockView_strokeWidthSecondHand, DEFAULT_STROKE_WIDTH)
        }

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initParams()

        // Draw
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas?) {
        canvas?.drawCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat(), circlePaint!!)
    }

    private fun initAllPaints() {
        initCirclePaint()
    }

    private fun initCirclePaint() {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPaintParams(circlePaint!!, Color.BLACK, CIRCLE_STROKE_WIDTH)
    }

    private fun setPaintParams(paint: Paint, color: Int, strokeWidth: Float) {
        paint.reset()
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
    }

}