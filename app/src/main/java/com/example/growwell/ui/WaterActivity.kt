package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.example.growwell.AlarmScheduler

class WaterActivity : AppCompatActivity() {
    
    private lateinit var username: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_water)
        
        username = PrefsManager.getLastActiveUsername(this) ?: ""
        if (username.isBlank()) {
            Toast.makeText(this, "No active user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        updateProgress()
        updatePreview()
        updateScheduledTimes()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        // Add cup button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddCup).setOnClickListener {
            val currentCups = PrefsManager.getWaterCups(this, username)
            if (currentCups < 8) {
                PrefsManager.setWaterCups(this, username, currentCups + 1)
                updateProgress()
                Toast.makeText(this, "Cup added! ${currentCups + 1}/8", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Maximum cups reached!", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Schedule reminder button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSchedule).setOnClickListener {
            scheduleReminders()
        }
        
        // Cancel all button
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelAll).setOnClickListener {
            AlarmScheduler.cancelAllHydrationAlarms(this, username)
            updatePreview()
            updateScheduledTimes()
            Toast.makeText(this, "All reminders cancelled", Toast.LENGTH_SHORT).show()
        }

        // Delete schedules icon in fixed bar
        findViewById<android.widget.ImageButton>(R.id.btnDeleteSchedules)?.setOnClickListener {
            AlarmScheduler.cancelAllHydrationAlarms(this, username)
            updatePreview()
            updateScheduledTimes()
            Toast.makeText(this, "Schedules deleted", Toast.LENGTH_SHORT).show()
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        // Setup NumberPickers
        setupNumberPickers()
    }
    
    private fun setupNumberPickers() {
        val npHours = findViewById<android.widget.NumberPicker>(R.id.npHours)
        val npMinutes = findViewById<android.widget.NumberPicker>(R.id.npMinutes)
        val npCount = findViewById<android.widget.NumberPicker>(R.id.npCount)
        
        npHours.minValue = 0
        npHours.maxValue = 23
        npHours.value = 1
        
        npMinutes.minValue = 0
        npMinutes.maxValue = 59
        npMinutes.value = 0
        
        npCount.minValue = 1
        npCount.maxValue = 10
        npCount.value = 5
        
        // Update preview when values change
        npHours.setOnValueChangedListener { _, _, _ -> updatePreview() }
        npMinutes.setOnValueChangedListener { _, _, _ -> updatePreview() }
        npCount.setOnValueChangedListener { _, _, _ -> updatePreview() }
    }
    
    private fun updateProgress() {
        val cups = PrefsManager.getWaterCups(this, username)
        val goal = PrefsManager.getWaterGoal(this, username)
        val percentage = if (goal > 0) (cups * 100) / goal else 0
        
        findViewById<android.widget.ProgressBar>(R.id.cpiWater).progress = percentage
        findViewById<android.widget.TextView>(R.id.txtPercent).text = "$percentage%"
        findViewById<android.widget.TextView>(R.id.txtProgress).text = "$cups/$goal cups"
    }
    
    private fun scheduleReminders() {
        val npHours = findViewById<android.widget.NumberPicker>(R.id.npHours)
        val npMinutes = findViewById<android.widget.NumberPicker>(R.id.npMinutes)
        val npCount = findViewById<android.widget.NumberPicker>(R.id.npCount)
        
        val intervalMs = (npHours.value * 60 + npMinutes.value) * 60 * 1000L
        val count = npCount.value
        
        val requestCodes = AlarmScheduler.scheduleHydrationBatch(this, username, intervalMs, count)
        updatePreview()
        updateScheduledTimes()
        Toast.makeText(this, "Reminders scheduled!", Toast.LENGTH_SHORT).show()
    }
    
    private fun updatePreview() {
        val npHours = findViewById<android.widget.NumberPicker>(R.id.npHours)
        val npMinutes = findViewById<android.widget.NumberPicker>(R.id.npMinutes)
        val npCount = findViewById<android.widget.NumberPicker>(R.id.npCount)
        
        val hours = npHours.value
        val minutes = npMinutes.value
        val count = npCount.value
        
        val timeText = if (hours > 0) {
            if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
        } else {
            "${minutes}m"
        }
        
        findViewById<android.widget.TextView>(R.id.tvPreview).text = "Every $timeText • $count times"
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

    private fun updateScheduledTimes() {
        val bar = findViewById<android.widget.LinearLayout>(R.id.scheduledBar)
        val tv = findViewById<android.widget.TextView>(R.id.tvScheduledTimes)
        val config = PrefsManager.getHydrationConfig(this, username)
        if (config == null || config.count <= 0 || config.intervalMs <= 0L) {
            bar.visibility = View.GONE
            tv.text = ""
            return
        }

        val now = System.currentTimeMillis()
        val times = (1..config.count).map { index -> now + (config.intervalMs * index) }
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timesText = times.joinToString(separator = ", ") { ts -> formatter.format(Date(ts)) }

        tv.text = "Scheduled: $timesText"
        bar.visibility = View.VISIBLE
    }
}