package com.example.workouttimerapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mainHandler: Handler

    private var seconds = 0

    // Is the stopwatch running?
    private var running = false
    private var wasRunning = false

    private var type: String? = ""

    lateinit var sharedPreferences: SharedPreferences

    private val updateTextTask = object : Runnable {
        override fun run() {
            mainHandler.postDelayed(this, 1000)
        }
    }


    // If the activity is paused,
    // stop the stopwatch.
    override fun onPause() {
        super.onPause()
        wasRunning = running
        running = false
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    override fun onResume() {
        super.onResume()
        if (wasRunning) {
            running = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = this.getSharedPreferences(
            "sharedPrefFile",
            Context.MODE_PRIVATE
        )
        mainHandler = Handler(Looper.getMainLooper())
        startButton.setOnClickListener { startTimer() }
        pauseButton.setOnClickListener { pauseTimer() }
        stopButton.setOnClickListener { stopTimer() }
        type = workoutTypeEditText.text.toString()


        val sharedPreferencesSeconds = sharedPreferences.getInt("seconds", -1)
        val sharedNameValue = sharedPreferences.getString("type", null)


        if (sharedPreferencesSeconds != -1) {
            // and seconds.

            val hours = sharedPreferencesSeconds / 3600
            val minutes = sharedPreferencesSeconds % 3600 / 60
            val secs = sharedPreferencesSeconds % 60
            val time = String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d", hours,
                minutes, secs
            )
            topText.text = "You spent $time on $sharedNameValue"
        }

        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState
                .getInt("seconds")
            running = savedInstanceState
                .getBoolean("running")
            wasRunning = savedInstanceState
                .getBoolean("wasRunning")
            type = savedInstanceState.getString("type")
            workoutTypeEditText.setText(type)
        }
        runTimer()
    }

    // Save the state of the stopwatch
    // if it's about to be destroyed.
    public override fun onSaveInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState
            .putString("type", type)
        savedInstanceState
            .putInt("seconds", seconds)
        savedInstanceState
            .putBoolean("running", running)
        savedInstanceState
            .putBoolean("wasRunning", wasRunning)
    }

    fun startTimer() {
        running = true
    }

    fun stopTimer() {
        //save seconds and text

        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("seconds", seconds)
        editor.putString("type", workoutTypeEditText.text.toString())
        editor.apply()
        editor.commit()
        seconds = 0
        running = false
        wasRunning = false
    }

    fun pauseTimer() {
        running = false
    }

    private fun runTimer() {

        // Get the text view.
        val timeView = timeText

        // Creates a new Handler
        val handler = Handler()

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d", hours,
                    minutes, secs
                )

                // Set the text view text.
                timeView.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }

}