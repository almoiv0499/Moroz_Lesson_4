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
        private const val ERROR_FOR_MEASURE_WIDTH = "Error in measure width"
        private const val ERROR_FOR_MEASURE_HEIGHT = "Error in measure height"

        // Size const
        private const val DEFAULT_SIZE_VALUE = 0
        private const val PADDING = 50
        private const val DIVIDER_BY_HALF = 2
        private const val CONST_FOR_RADIUS_HAND_SIZE = 60F
        private const val PADDING_FOR_HOUR_HAND = 150F
        private const val PADDING_FOR_MINUTE_HAND = 100F
        private const val PADDING_FOR_SECOND_HAND = 50F

        // Stroke width const
        private const val DEFAULT_HOUR_HAND_STROKE_WIDTH = 6F
        private const val DEFAULT_MINUTE_HAND_STROKE_WIDTH = 10F
        private const val DEFAULT_SECOND_HAND_STROKE_WIDTH = 14F
        private const val CIRCLE_STROKE_WIDTH = 10F
        private const val POINTER_STROKE_WIDTH = 20F

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
        private const val COORDINATE_BY_Y = 1
        private const val INITIAL_CAPACITY = 2
        private const val START_ANGLE = 270F
        private const val CONST_CIRCLE_HALF = 180
        private const val SKIP_ANGEL_HOUR_HAND = 30
        private const val SKIP_ANGEL_MINUTE_HAND = 6
        private const val SKIP_ANGEL_SECOND_HAND = 6

        // Pointer
        private const val POINTER_NUMBER_FROM = 0
        private const val POINTER_NUMBER_TO = 11
        private const val COORDINATES_CONST = 0.9
    }

    // Paint circle
    private val circlePaint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private var mWidth = DEFAULT_SIZE_VALUE
    private var mHeight = DEFAULT_SIZE_VALUE
    private var centreX = DEFAULT_SIZE_VALUE
    private var centreY = DEFAULT_SIZE_VALUE
    private var padding = DEFAULT_SIZE_VALUE
    private var radius = DEFAULT_SIZE_VALUE
    private var minimum = DEFAULT_SIZE_VALUE

    // Paint clock hands
    private val hourHandPaint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private val minuteHandPaint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private val secondHandPaint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    // Timer
    private val handler = Handler(Looper.getMainLooper())
    private var hourHand = DEFAULT_VALUE_TIMER
    private var minuteHand = DEFAULT_VALUE_TIMER
    private var secondHand = DEFAULT_VALUE_TIMER

    // Pointer
    private val pointerPaint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    init {
        setPaintParams(circlePaint, Color.BLACK, CIRCLE_STROKE_WIDTH)
        setCustomAttrs(attrs)
        setPaintParams(pointerPaint, Color.BLACK, POINTER_STROKE_WIDTH)
        initTimer()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST -> desiredWidth
            MeasureSpec.EXACTLY -> desiredWidth
            MeasureSpec.UNSPECIFIED -> context.resources.displayMetrics.widthPixels
            else -> error(ERROR_FOR_MEASURE_WIDTH)
        }

        val desiredHeight = MeasureSpec.getSize(heightMeasureSpec)
        val measureHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> desiredHeight
            MeasureSpec.EXACTLY -> desiredHeight
            MeasureSpec.UNSPECIFIED -> context.resources.displayMetrics.heightPixels
            else -> error(ERROR_FOR_MEASURE_HEIGHT)
        }

        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initParams()

        // Draw
        drawCircle(canvas)
        drawClockHands(canvas)
        drawPointer(canvas)
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
            val hourHandStrokeWidth = getFloat(
                R.styleable.AnalogClockView_strokeWidthHourHand, DEFAULT_HOUR_HAND_STROKE_WIDTH
            )
            val minuteHandStrokeWidth = getFloat(
                R.styleable.AnalogClockView_strokeWidthMinuteHand, DEFAULT_MINUTE_HAND_STROKE_WIDTH
            )
            val secondHandStrokeWidth = getFloat(
                R.styleable.AnalogClockView_strokeWidthSecondHand, DEFAULT_SECOND_HAND_STROKE_WIDTH
            )

            // Init Paints
            setPaintParams(hourHandPaint, hourHandColor, hourHandStrokeWidth)
            setPaintParams(minuteHandPaint, minuteHandColor, minuteHandStrokeWidth)
            setPaintParams(secondHandPaint, secondHandColor, secondHandStrokeWidth)

            // Setting stroke width via code
            setHourHandStrokeWidth(hourHandStrokeWidth)
            setMinuteHandStrokeWidth(minuteHandStrokeWidth)
            setSecondHandStrokeWidth(secondHandStrokeWidth)

            // Setting color via code
            setHourHandColor(hourHandColor)
            setMinuteHandColor(minuteHandColor)
            setSecondHandColor(secondHandColor)
        }

        typedArray.recycle()
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
        canvas?.drawCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat(), circlePaint)
    }

    private fun drawClockHands(canvas: Canvas?) {
        val clockHandsRadius = radius - CONST_FOR_RADIUS_HAND_SIZE

        // Draw hour hand
        drawHands(
            canvas, calculateCoordinates(
                hourHand, clockHandsRadius - PADDING_FOR_HOUR_HAND, SKIP_ANGEL_HOUR_HAND
            ), hourHandPaint
        )

        // Draw minute hand
        drawHands(
            canvas, calculateCoordinates(
                minuteHand, clockHandsRadius - PADDING_FOR_MINUTE_HAND, SKIP_ANGEL_MINUTE_HAND
            ), minuteHandPaint
        )

        // Draw second hand
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

    private fun drawPointer(canvas: Canvas?) {
        for (pointer in POINTER_NUMBER_FROM..POINTER_NUMBER_TO) {
            val angle = PI / 6 * (pointer - 3)
            val coordinateX =
                ((mWidth / DIVIDER_BY_HALF) + cos(angle) * radius * COORDINATES_CONST).toFloat()
            val coordinateY =
                ((mHeight / DIVIDER_BY_HALF).toDouble() + sin(angle) * radius * COORDINATES_CONST).toFloat()
            canvas?.drawLine(
                ((mWidth / DIVIDER_BY_HALF) + cos(angle) * radius).toFloat(),
                ((mHeight / DIVIDER_BY_HALF).toDouble() + sin(angle) * radius).toFloat(),
                coordinateX,
                coordinateY,
                pointerPaint
            )
        }
    }

    private fun calculateCoordinates(position: Float, radius: Float, skipAngle: Int): List<Float> {
        val coordinates = ArrayList<Float>(INITIAL_CAPACITY)
        val startAngle = START_ANGLE
        val angle = startAngle + (position * skipAngle)

        coordinates.add(
            COORDINATE_BY_X,
            (radius * cos(angle * PI / CONST_CIRCLE_HALF) + width / DIVIDER_BY_HALF).toFloat()
        )
        coordinates.add(
            COORDINATE_BY_Y,
            (height / DIVIDER_BY_HALF + radius * sin(angle * PI / CONST_CIRCLE_HALF)).toFloat()
        )
        return coordinates
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

    fun setHourHandStrokeWidth(strokeWidth: Float) {
        hourHandPaint.strokeWidth = strokeWidth
    }

    fun setMinuteHandStrokeWidth(strokeWidth: Float) {
        minuteHandPaint.strokeWidth = strokeWidth
    }

    fun setSecondHandStrokeWidth(strokeWidth: Float) {
        secondHandPaint.strokeWidth = strokeWidth
    }

    fun setHourHandColor(color: Int) {
        hourHandPaint.color = color
    }

    fun setMinuteHandColor(color: Int) {
        minuteHandPaint.color = color
    }

    fun setSecondHandColor(color: Int) {
        secondHandPaint.color = color
    }
}