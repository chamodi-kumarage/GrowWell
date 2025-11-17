package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.example.growwell.prefs.Storage

class LaunchActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_screen)
        
        // Wait 5 seconds then navigate
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 5000)
    }
    
    private fun navigateToNextScreen() {

        val intent = Intent(this, Onboarding1Activity::class.java)
        startActivity(intent)
        finish()
    }
}

