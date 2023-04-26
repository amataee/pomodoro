package com.amataee.pomodoro

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var settingsFab: FloatingActionButton
    private lateinit var focusButton: Button
    private lateinit var breakButton: Button

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning = false

    private var focusTime = 50
    private var breakTime = 10

    private val transition = Slide(Gravity.TOP)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerTextView)
        settingsFab = findViewById(R.id.settingsFab)
        focusButton = findViewById(R.id.focusButton)
        breakButton = findViewById(R.id.breakButton)

        timerTextView.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        settingsFab.setOnClickListener {
            if (!isTimerRunning){
                showEditTextDialog()
            }
        }

        focusButton.setOnClickListener {
            timeLeftInMillis = focusTime.toLong() * 60 * 1000
            startTimer()
        }

        breakButton.setOnClickListener {
            timeLeftInMillis = breakTime.toLong() * 60 * 1000
            startTimer()
        }

    }

    private fun View.slideVisibility(visibility: Boolean, durationTime: Long = 400) {
        transition.apply {
            duration = durationTime
            addTarget(this@slideVisibility)
        }
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.isVisible = visibility
    }

    private fun vibrate() {
        val v = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            v.vibrate(
                VibrationEffect.createOneShot(
                    1200,
                    VibrationEffect.EFFECT_DOUBLE_CLICK
                )
            )
        } else {
            v.vibrate(1200)
        }
    }

    private fun showEditTextDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val timeEditText = dialogLayout.findViewById<EditText>(R.id.timeEditText)

        with(builder) {
            setTitle("Change Timer")
            setPositiveButton("Done") { dialog, which ->
                timeLeftInMillis = timeEditText.text.toString().toLong() * 60 * 1000
            }
            setNegativeButton("Cancel") { dialog, which ->
                Log.d("Cancel", "Canceled Change Timer Dialog.")
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun startTimer() {
        settingsFab.slideVisibility(false)
        breakButton.slideVisibility(false)
        focusButton.slideVisibility(false)

        var time = 0L
        time = if (timeLeftInMillis == 0L) {
            focusTime * 60 * 1000L
        } else {
            timeLeftInMillis
        }
        if (!isTimerRunning) {
            timeLeftInMillis = time
            countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimerTextView()
                }

                override fun onFinish() {
                    timeLeftInMillis = 0
                    vibrate()
                    updateTimerTextView()
                }
            }.start()

            isTimerRunning = true
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            settingsFab.slideVisibility(true)
            breakButton.slideVisibility(true)
            focusButton.slideVisibility(true)

            countDownTimer.cancel()
            isTimerRunning = false
        }
    }

    private fun updateTimerTextView() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timerTextView.text = getString(R.string.timer_format).format(minutes, seconds)
    }

}
