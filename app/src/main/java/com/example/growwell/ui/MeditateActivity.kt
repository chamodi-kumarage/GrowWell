package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R

class MeditateActivity : AppCompatActivity() {
    
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var timeLeft = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditate)
        
        setupUI()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        val sliderMinutes = findViewById<com.google.android.material.slider.Slider>(R.id.sliderMinutes)
        val btnPlayPause = findViewById<android.widget.ImageButton>(R.id.btnPlayPause)
        val btnReset = findViewById<android.widget.TextView>(R.id.btnReset)
        val btnStop = findViewById<android.widget.TextView>(R.id.btnStop)
        
        // Update timer name when slider changes
        sliderMinutes.addOnChangeListener { _, value, _ ->
            findViewById<android.widget.TextView>(R.id.txtTimerName).text = "Current: ${value.toInt()} min session"
            findViewById<android.widget.TextView>(R.id.txtTime).text = String.format("%02d:00", value.toInt())
        }
        
        btnPlayPause.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer(sliderMinutes.value.toInt())
            }
        }
        
        btnReset.setOnClickListener {
            resetTimer()
        }
        
        btnStop.setOnClickListener {
            stopTimer()
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun startTimer(minutes: Int) {
        if (minutes <= 0) {
            Toast.makeText(this, "Please select a valid time", Toast.LENGTH_SHORT).show()
            return
        }
        
        timeLeft = minutes * 60 * 1000L
        isRunning = true
        
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateUI()
            }
            
            override fun onFinish() {
                isRunning = false
                Toast.makeText(this@MeditateActivity, "Meditation completed!", Toast.LENGTH_SHORT).show()
                updateUI()
            }
        }.start()
        
        updateUI()
    }
    
    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        updateUI()
    }
    
    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        timeLeft = 0
        updateUI()
    }
    
    private fun stopTimer() {
        timer?.cancel()
        isRunning = false
        timeLeft = 0
        updateUI()
    }
    
    private fun updateUI() {
        val minutes = (timeLeft / 60000).toInt()
        val seconds = ((timeLeft % 60000) / 1000).toInt()
        
        findViewById<android.widget.TextView>(R.id.txtTime).text = 
            String.format("%02d:%02d", minutes, seconds)
        
        val sliderValue = findViewById<com.google.android.material.slider.Slider>(R.id.sliderMinutes).value.toInt()
        val progress = if (sliderValue > 0 && timeLeft > 0) {
            val elapsedMinutes = sliderValue - minutes
            (elapsedMinutes * 100) / sliderValue
        } else 0
        
        findViewById<android.widget.ProgressBar>(R.id.cpiMeditate).progress = progress
        
        val btnPlayPause = findViewById<android.widget.ImageButton>(R.id.btnPlayPause)
        btnPlayPause.setImageResource(if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
    }
    
    private fun setupBottomNavigation() {
        findViewById<android.widget.LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navHabits).setOnClickListener {
            startActivity(Intent(this, ChooseHabitActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navMood).setOnClickListener {
            startActivity(Intent(this, JournalActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}