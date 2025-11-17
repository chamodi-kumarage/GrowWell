package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.Storage

class Onboarding8Activity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding8)
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGetStarted).setOnClickListener {
            // Mark onboarding as completed
            Storage.setOnboarded(this, true)
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}