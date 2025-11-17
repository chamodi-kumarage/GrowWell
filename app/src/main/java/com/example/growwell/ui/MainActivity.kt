package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.prefs.PrefsManager

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        val username = PrefsManager.getLastActiveUsername(this)
        if (username != null) {
            // User is logged in, go to home
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // User not logged in, go to login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        
        finish()
    }
}

