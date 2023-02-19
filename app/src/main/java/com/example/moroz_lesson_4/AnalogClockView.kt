package com.example.moroz_lesson_4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class AnalogClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defResAttrs: Int = DEFAULT_ATTRS_VALUE,
) : View(context, attrs, defResAttrs) {

    companion object {

        private const val DEFAULT_ATTRS_VALUE = 0

        // Size const
        private const val DEFAULT_SIZE_VALUE = 0
        private const val PADDING = 50
        private const val DIVIDER_BY_HALF = 2
        private const val CONST_FOR_RADIUS_HAND_SIZE = 60F
        private const val PADDING_FOR_HOUR_HAND = 160F
        private const val PADDING_FOR_MINUTE_HAND = 80F
        private const val PADDING_FOR_SECOND_HAND = 50F

        // Stroke width const
        private const val DEFAULT_STROKE_WIDTH = 0.0F
        private const val CIRCLE_STROKE_WIDTH = 10F

        // Timer const
        private const val DEFAULT_VALUE_TIMER = 0.0F
        private const val DELAY_MILLIS = 1000L
        private const val CALCULATE_HOUR = 60

        // Timer substring
        private const val PATTERN_DATE_TIME = "HH:mm:ss"
        private const val HOUR_SUBSTRING_FROM = 0
        private const val HOUR_SUBSTRING_TO = 2
        private const val HOUR_SUBSTRING_CONST = 12
        private const val MINUTE_SUBSTRING_FROM = 3
        private const val MINUTE_SUBSTRING_TO = 5
        private const val SECOND_SUBSTRING_FROM = 6
        private const val SECOND_SUBSTRING_TO = 8

        // Coordinates const
        private const val COORDINATE_BY_X = 0
        private const val COORDINATE_BY_Y = 0
        private const val INITIAL_CAPACITY = 2
        private const val START_ANGLE = 270F
        private const val CONST_CIRCLE_HALF = 180
        private const val SKIP_ANGEL_HOUR_HAND = 30
        private const val SKIP_ANGEL_MINUTE_HAND = 6
        private const val SKIP_ANGEL_SECOND_HAND = 6
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

    // Paint clock hands
    private var hourHandPaint: Paint? = null
    private var minuteHandPaint: Paint? = null
    private var secondHandPaint: Paint? = null

    // Timer
    private val handler = Handler(Looper.getMainLooper())
    private var hourHand = DEFAULT_VALUE_TIMER
    private var minuteHand = DEFAULT_VALUE_TIMER
    private var secondHand = DEFAULT_VALUE_TIMER

    init {
        initCirclePaint()
        setCustomAttrs(attrs = attrs)
        initTimer()
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

            // Init Paints
            initHourHandPaint(hourHandColor, hourHandStrokeWidth)
            initMinuteHandPaint(minuteHandColor, minuteHandStrokeWidth)
            initSecondHandPaint(secondHandColor, secondHandStrokeWidth)
        }

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initParams()

        // Draw
        drawCircle(canvas)
        drawClockHands(canvas)
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

    private fun drawCircle(canvas: Canvas?) {
        canvas?.drawCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat(), circlePaint!!)
    }

    private fun initCirclePaint() {
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPaintParams(circlePaint!!, Color.BLACK, CIRCLE_STROKE_WIDTH)
    }

    private fun initHourHandPaint(color: Int, strokeWidth: Float) {
        hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPaintParams(hourHandPaint!!, color, strokeWidth)
    }

    private fun initMinuteHandPaint(color: Int, strokeWidth: Float) {
        minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPaintParams(minuteHandPaint!!, color, strokeWidth)
    }

    private fun initSecondHandPaint(color: Int, strokeWidth: Float) {
        secondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        setPaintParams(secondHandPaint!!, color, strokeWidth)
    }

    private fun drawClockHands(canvas: Canvas?) {
        val clockHandsRadius = radius - CONST_FOR_RADIUS_HAND_SIZE
        drawHands(
            canvas, calculateCoordinates(
                hourHand, clockHandsRadius - PADDING_FOR_HOUR_HAND, SKIP_ANGEL_HOUR_HAND
            ), hourHandPaint
        )
        drawHands(
            canvas, calculateCoordinates(
                minuteHand, clockHandsRadius - PADDING_FOR_MINUTE_HAND, SKIP_ANGEL_MINUTE_HAND
            ), minuteHandPaint
        )
        drawHands(
            canvas, calculateCoordinates(
                secondHand, clockHandsRadius - PADDING_FOR_SECOND_HAND, SKIP_ANGEL_SECOND_HAND
            ), secondHandPaint
        )
    }

    private fun drawHands(
        canvas: Canvas?,
        coordinates: List<Float>,
        paint: Paint?,
    ) {
        canvas?.drawLine(
            centreX.toFloat(),
            centreY.toFloat(),
            coordinates[COORDINATE_BY_X],
            coordinates[COORDINATE_BY_Y],
            paint!!
        )
    }

    private fun calculateCoordinates(position: Float, radius: Float, skipAngle: Int): List<Float> {
        val result = ArrayList<Float>(INITIAL_CAPACITY)
        val startAngle = START_ANGLE
        val angle = startAngle + (position * skipAngle)

        result.add(
            COORDINATE_BY_X,
            (radius * cos(angle * PI / CONST_CIRCLE_HALF) + width / DIVIDER_BY_HALF).toFloat()
        )
        result.add(
            COORDINATE_BY_Y,
            (height / DIVIDER_BY_HALF + radius * sin(angle * PI / CONST_CIRCLE_HALF)).toFloat()
        )
        return result
    }

    private fun setPaintParams(paint: Paint, color: Int, strokeWidth: Float) {
        paint.reset()
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
    }

    private fun initTimer() {
        handler.post(object : Runnable {
            override fun run() {
                val currentTime: String = SimpleDateFormat(
                    PATTERN_DATE_TIME, Locale.getDefault()
                ).format(Date())
                setClockTime(
                    currentTime.substring(HOUR_SUBSTRING_FROM, HOUR_SUBSTRING_TO)
                        .toFloat() % HOUR_SUBSTRING_CONST,
                    currentTime.substring(MINUTE_SUBSTRING_FROM, MINUTE_SUBSTRING_TO).toFloat(),
                    currentTime.substring(SECOND_SUBSTRING_FROM, SECOND_SUBSTRING_TO).toFloat()
                )
                handler.postDelayed(this, DELAY_MILLIS)
            }
        })
    }

    private fun setClockTime(hour: Float, minute: Float, second: Float) {
        hourHand = hour + (minute / CALCULATE_HOUR)
        minuteHand = minute
        secondHand = second
        invalidate()
    }

}