package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.example.growwell.prefs.Storage

class LoginActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnlogin).setOnClickListener {
            try {
                val username = findViewById<android.widget.EditText>(R.id.etUsername).text.toString()
                val password = findViewById<android.widget.EditText>(R.id.etPassword).text.toString()
                
                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                // Validate user
                val userData = Storage.getUser(this)
                
                if (userData != null && userData.first == username && userData.second == password) {
                    // Set as active user
                    PrefsManager.setLastActiveUsername(this, username)
                    
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to permission gate
                    try {
                        val intent = Intent(this, PermissionGateActivity::class.java).apply {
                            putExtra("username", username)
                        }
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Invalid credentials. Please check your username and password.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Login error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.TextView>(R.id.txtSignup).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}