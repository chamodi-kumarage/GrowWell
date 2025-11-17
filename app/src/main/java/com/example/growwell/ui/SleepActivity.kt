package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.growwell.R
import com.example.growwell.AlarmScheduler
import com.example.growwell.prefs.PrefsManager
import java.util.Calendar

class SleepActivity : AppCompatActivity() {
    
    private lateinit var username: String
    private lateinit var rvAlarmHistory: RecyclerView
    private lateinit var alarmHistoryAdapter: AlarmHistoryAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)
        
        username = PrefsManager.getLastActiveUsername(this) ?: ""
        if (username.isBlank()) {
            Toast.makeText(this, "No active user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        loadCurrentSettings()
        setupAlarmHistory()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        val timePicker = findViewById<android.widget.TimePicker>(R.id.timePicker)
        val btnSaveUpdate = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSaveUpdate)
        
        btnSaveUpdate.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            
            val success = AlarmScheduler.scheduleDailySleepAlarm(this, username, hour, minute)
            if (success) {
                // Add to alarm history
                PrefsManager.addAlarmToHistory(this, username, hour, minute)
                // Refresh the alarm history list
                refreshAlarmHistory()
                Toast.makeText(this, "Sleep alarm set for ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to set alarm. Check permissions.", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun loadCurrentSettings() {
        val config = PrefsManager.getSleepConfig(this, username)
        if (config != null) {
            findViewById<android.widget.TimePicker>(R.id.timePicker).apply {
                hour = config.hour
                minute = config.minute
            }
        }
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
    
    private fun setupAlarmHistory() {
        rvAlarmHistory = findViewById(R.id.rvAlarmHistory)
        rvAlarmHistory.layoutManager = LinearLayoutManager(this)
        
        // Initialize adapter with empty list
        alarmHistoryAdapter = AlarmHistoryAdapter(emptyList()) { alarm ->
            // Handle alarm reuse - set the time picker to this alarm's time
            findViewById<android.widget.TimePicker>(R.id.timePicker).apply {
                hour = alarm.hour
                minute = alarm.minute
            }
            Toast.makeText(this, "Alarm time set to ${String.format("%02d:%02d", alarm.hour, alarm.minute)}", Toast.LENGTH_SHORT).show()
        }
        
        rvAlarmHistory.adapter = alarmHistoryAdapter
        refreshAlarmHistory()
    }
    
    private fun refreshAlarmHistory() {
        val alarmHistory = PrefsManager.getAlarmHistory(this, username)
        // Sort by date created (newest first)
        val sortedHistory = alarmHistory.sortedByDescending { it.dateCreated }
        alarmHistoryAdapter = AlarmHistoryAdapter(sortedHistory) { alarm ->
            // Handle alarm reuse - set the time picker to this alarm's time
            findViewById<android.widget.TimePicker>(R.id.timePicker).apply {
                hour = alarm.hour
                minute = alarm.minute
            }
            Toast.makeText(this, "Alarm time set to ${String.format("%02d:%02d", alarm.hour, alarm.minute)}", Toast.LENGTH_SHORT).show()
        }
        rvAlarmHistory.adapter = alarmHistoryAdapter
    }
}