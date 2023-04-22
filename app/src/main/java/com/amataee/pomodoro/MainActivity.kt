package com.amataee.pomodoro

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.annotation.RequiresApi

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

    private fun setButton(button: Button, isEnabled: Boolean, isClickable: Boolean) {
        button.isEnabled = isEnabled
        button.isClickable = isClickable
    }

    private fun vibrate() {
        val v = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
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
        setButton(focusButton, isEnabled = false, isClickable = false)
        setButton(breakButton, isEnabled = false, isClickable = false)
        settingsFab.isClickable = false

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
            setButton(focusButton, isEnabled = true, isClickable = true)
            setButton(breakButton, isEnabled = true, isClickable = true)
            settingsFab.isClickable = true
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
