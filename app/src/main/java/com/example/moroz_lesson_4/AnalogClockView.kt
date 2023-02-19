package com.example.moroz_lesson_4

import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.util.AttributeSet
import android.view.View


class AnalogClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defResAttrs: Int = 0,
) : View(context, attrs, defResAttrs) {


    init {
        setCustomAttributes(attrs = attrs)
    }

    private fun setCustomAttributes(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnalogClockView)

        with(typedArray) {
            // Color for clock hands
            val colorHourHand = getColor(R.styleable.AnalogClockView_colorHourHand, Color.RED)
            val colorMinuteHand = getColor(R.styleable.AnalogClockView_colorMinuteHand, Color.BLUE)
            val colorSecondHand = getColor(R.styleable.AnalogClockView_colorSecondHand, Color.GRAY)

            // Stroke width for clock hands
            val strokeWidthHourHand =
                getFloat(R.styleable.AnalogClockView_strokeWidthHourHand, 0.0F)
            val strokeWidthMinuteHand =
                getFloat(R.styleable.AnalogClockView_strokeWidthMinuteHand, 0.0f)
            val strokeWidthSecondHand =
                getFloat(R.styleable.AnalogClockView_strokeWidthSecondHand, 0.0F)
        }

        typedArray.recycle()
    }

}