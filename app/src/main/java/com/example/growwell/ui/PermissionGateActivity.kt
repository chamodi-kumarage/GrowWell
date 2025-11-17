package com.example.growwell.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.growwell.R
import com.example.growwell.Permissions
import com.example.growwell.prefs.PrefsManager

class PermissionGateActivity : AppCompatActivity() {
    
    private lateinit var username: String
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkExactAlarmPermission()
        } else {
            // Intentionally no toast
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        
        username = intent.getStringExtra("username") ?: ""
        
        if (username.isBlank()) {
            finish()
            return
        }
        
        try {
            setupUI()
            requestPermissions()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupUI() {
        try {
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGrantPermissions).setOnClickListener {
                if (hasAllPermissions()) {
                    PrefsManager.setPermissionFlowCompleted(this, true)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    // Intentionally no toast
                }
            }
            
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSkip).setOnClickListener {
                PrefsManager.setPermissionFlowCompleted(this, true)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!Permissions.hasPostNotifications(this)) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    checkExactAlarmPermission()
                }
            } else {
                checkExactAlarmPermission()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun checkExactAlarmPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!Permissions.hasExactAlarm(this)) {
                    Permissions.requestExactAlarm(this)
                } else {
                    // already granted
                }
            } else {
                // not needed
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun hasAllPermissions(): Boolean {
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Permissions.hasPostNotifications(this)
        } else true
        
        val hasExactAlarmPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Permissions.hasExactAlarm(this)
        } else true
        
        return hasNotificationPermission && hasExactAlarmPermission
    }
    
    override fun onResume() {
        super.onResume()
        // Re-check permissions when returning from settings
        if (hasAllPermissions()) {
            findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGrantPermissions).isEnabled = true
        }
    }
}