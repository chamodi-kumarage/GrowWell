package com.example.growwell.ui

import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.alarms.AlarmStopReceiver
import com.example.growwell.prefs.PrefsManager

class AlarmRingActivity : AppCompatActivity() {
    
    private var ringtone: android.media.Ringtone? = null
    private var vibrator: Vibrator? = null
    private lateinit var username: String
    private var alarmType: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarmring)
        
        username = intent.getStringExtra("username") ?: return
        alarmType = intent.getStringExtra("type") ?: "alarm"
        
        setupUI()
        startAlarm()
    }
    
    private fun setupUI() {
        val btnStopAlarm = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnStopAlarm)
        val txtTitle = findViewById<android.widget.TextView>(R.id.txtTitle)
        val txtMessage = findViewById<android.widget.TextView>(R.id.txtMessage)
        
        // Set alarm content based on type
        when (alarmType) {
            "sleep" -> {
                txtTitle.text = "Sleep Alarm"
                txtMessage.text = "Time to sleep! Good night!"
            }
            "hydration" -> {
                txtTitle.text = "Hydration Reminder"
                txtMessage.text = "Time to drink water! Stay hydrated!"
            }
            else -> {
                txtTitle.text = "Alarm"
                txtMessage.text = "Alarm notification"
            }
        }
        
        btnStopAlarm.setOnClickListener {
            stopAlarm()
        }
    }
    
    private fun startAlarm() {
        val settings = PrefsManager.getAppSettings(this, username)
        
        // Start sound if enabled
        if (settings.soundEnabled) {
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
            ringtone?.play()
        }
        
        // Start vibration if enabled
        if (settings.vibrateEnabled) {
            vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(VibratorManager::class.java)
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Vibrator::class.java)
            }
            
            val pattern = longArrayOf(0, 1000, 1000)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        }
    }
    
    private fun stopAlarm() {
        // Stop sound
        ringtone?.stop()
        
        // Stop vibration
        vibrator?.cancel()
        
        // Send stop broadcast
        val stopIntent = Intent(this, AlarmStopReceiver::class.java).apply {
            putExtra("type", alarmType)
        }
        sendBroadcast(stopIntent)
        
        Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }
}