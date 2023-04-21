package com.amataee.pomodoro

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var settingsFab: FloatingActionButton

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning = false

    private var focusTime = 50
    // TODO: Add break time feature
//    private var breakTime = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerTextView)

        settingsFab = findViewById(R.id.settingsFab)

        timerTextView.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        settingsFab.setOnClickListener {
            showEditTextDialog()
        }

    }

    private fun showEditTextDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
        val focusTimeEditText = dialogLayout.findViewById<EditText>(R.id.focusEditText)
//        val breakTimeEditText = dialogLayout.findViewById<EditText>(R.id.breakEditText)

        with(builder) {
            setTitle("Focus & Break")
            setPositiveButton("Done") { dialog, which ->
                focusTime = focusTimeEditText.text.toString().toInt()
//                breakTime = breakTimeEditText.text.toString().toInt()
            }
            setNegativeButton("Cancel") { dialog, which ->
                // Canceled
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun startTimer() {
        val time = focusTime * 60 * 1000L
        if (!isTimerRunning) {
            timeLeftInMillis = time
            countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateTimerTextView()
                }

                override fun onFinish() {
                    timeLeftInMillis = 0
                    updateTimerTextView()
                }
            }.start()

            isTimerRunning = true
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun stopTimer() {
        if (isTimerRunning) {
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
