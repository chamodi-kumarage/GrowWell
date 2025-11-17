package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.Storage

class SignupActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRegister).setOnClickListener {
            try {
                val username = findViewById<android.widget.EditText>(R.id.etUsername).text.toString()
                val email = findViewById<android.widget.EditText>(R.id.etEmail).text.toString()
                val password = findViewById<android.widget.EditText>(R.id.etPassword).text.toString()
                
                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                // Save user data
                Storage.saveUser(this, username, password)
                
                Toast.makeText(this, "Account created successfully! Please login.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Signup error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.TextView>(R.id.txtLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}