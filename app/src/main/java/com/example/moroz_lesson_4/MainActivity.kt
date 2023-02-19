package com.example.moroz_lesson_4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.moroz_lesson_4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setColorForClockHands()
    }

    private fun setColorForClockHands() {
        with(binding) {
            analogClock.setHourHandColor(
                ContextCompat.getColor(
                    this@MainActivity, R.color.hour_hand_color
                )
            )
            analogClock.setMinuteHandColor(
                ContextCompat.getColor(
                    this@MainActivity, R.color.minute_hand_color
                )
            )
            analogClock.setSecondHandColor(
                ContextCompat.getColor(
                    this@MainActivity, R.color.second_hand_color
                )
            )
        }
    }

}