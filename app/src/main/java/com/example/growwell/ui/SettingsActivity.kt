package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var username: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        username = PrefsManager.getLastActiveUsername(this) ?: return
        
        setupUI()
        loadSettings()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        val swSound = findViewById<android.widget.Switch>(R.id.swSound)
        val swVibrate = findViewById<android.widget.Switch>(R.id.swVibrate)
        val tvAbout = findViewById<android.widget.TextView>(R.id.tvAbout)
        val tvPrivacy = findViewById<android.widget.TextView>(R.id.tvPrivacy)
        val btnClear = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnClear)
        
        swSound.setOnCheckedChangeListener { _, isChecked ->
            val settings = PrefsManager.getAppSettings(this, username)
            PrefsManager.setAppSettings(this, username, settings.copy(soundEnabled = isChecked))
        }
        
        swVibrate.setOnCheckedChangeListener { _, isChecked ->
            val settings = PrefsManager.getAppSettings(this, username)
            PrefsManager.setAppSettings(this, username, settings.copy(vibrateEnabled = isChecked))
        }
        
        tvAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        
        tvPrivacy.setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }
        
        btnClear.setOnClickListener {
            showClearDataDialog()
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            startActivity(Intent(this, ChooseHabitActivity::class.java))
            finish()
        }
    }
    
    private fun loadSettings() {
        val settings = PrefsManager.getAppSettings(this, username)
        findViewById<android.widget.Switch>(R.id.swSound).isChecked = settings.soundEnabled
        findViewById<android.widget.Switch>(R.id.swVibrate).isChecked = settings.vibrateEnabled
    }
    
    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clear All Data")
            .setMessage("This will delete all your data including habits, progress, and settings. This action cannot be undone.")
            .setPositiveButton("Clear") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun clearAllData() {
        // Clear all user data
        PrefsManager.clearUserData(this, username)
        
        // Cancel all alarms
        com.example.growwell.AlarmScheduler.cancelSleepAlarm(this)
        com.example.growwell.AlarmScheduler.cancelAllHydrationAlarms(this, username)
        
        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show()
        
        // Navigate back to login
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
            // Already on settings
        }
    }
}