package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R

class Onboarding2Activity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding2)
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNext).setOnClickListener {
            startActivity(Intent(this, Onboarding3Activity::class.java))
        }
    }
}