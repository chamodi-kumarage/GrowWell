package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReadingActivity : AppCompatActivity() {
    
    private lateinit var username: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)
        
        username = PrefsManager.getLastActiveUsername(this) ?: ""
        if (username.isBlank()) {
            Toast.makeText(this, "No active user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        loadCurrentBook()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        val etTitle = findViewById<android.widget.EditText>(R.id.etTitle)
        val etAuthor = findViewById<android.widget.EditText>(R.id.etAuthor)
        val etPagesTotal = findViewById<android.widget.EditText>(R.id.etPagesTotal)
        val etStartDate = findViewById<android.widget.EditText>(R.id.etStartDate)
        val sliderPages = findViewById<com.google.android.material.slider.Slider>(R.id.sliderPages)
        val btnSaveReading = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSaveReading)
        
        // Set current date as default start date
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etStartDate.setText(currentDate)
        
        btnSaveReading.setOnClickListener {
            val title = etTitle.text.toString()
            val author = etAuthor.text.toString()
            val totalPages = etPagesTotal.text.toString().toIntOrNull() ?: 0
            val startDate = etStartDate.text.toString()
            val readPages = sliderPages.value.toInt()
            
            if (title.isBlank() || author.isBlank() || totalPages <= 0) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (readPages > totalPages) {
                Toast.makeText(this, "Read pages cannot exceed total pages", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val progress = PrefsManager.ReadingProgress(
                bookTitle = title,
                author = author,
                totalPages = totalPages,
                readPages = readPages,
                startDate = System.currentTimeMillis()
            )
            
            PrefsManager.setReadingProgress(this, username, progress)
            updateUI(progress)
            Toast.makeText(this, "Reading progress saved!", Toast.LENGTH_SHORT).show()
        }
        
        // Update UI when slider changes
        sliderPages.addOnChangeListener { _, value, _ ->
            val progress = PrefsManager.getReadingProgress(this, username)
            if (progress != null) {
                val updatedProgress = progress.copy(readPages = value.toInt())
                updateUI(updatedProgress)
            }
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun loadCurrentBook() {
        val progress = PrefsManager.getReadingProgress(this, username)
        if (progress != null) {
            findViewById<android.widget.EditText>(R.id.etTitle).setText(progress.bookTitle)
            findViewById<android.widget.EditText>(R.id.etAuthor).setText(progress.author)
            findViewById<android.widget.EditText>(R.id.etPagesTotal).setText(progress.totalPages.toString())
            findViewById<com.google.android.material.slider.Slider>(R.id.sliderPages).apply {
                valueFrom = 0f
                valueTo = progress.totalPages.toFloat()
                value = progress.readPages.toFloat()
            }
            updateUI(progress)
        } else {
            // Set default values
            findViewById<com.google.android.material.slider.Slider>(R.id.sliderPages).apply {
                valueFrom = 0f
                valueTo = 100f
                value = 0f
            }
        }
    }
    
    private fun updateUI(progress: PrefsManager.ReadingProgress) {
        findViewById<android.widget.TextView>(R.id.txtBookName).text = progress.bookTitle
        findViewById<android.widget.TextView>(R.id.statPages).text = "${progress.readPages}/${progress.totalPages}"
        findViewById<android.widget.TextView>(R.id.statRemaining).text = "${progress.totalPages - progress.readPages}"
        
        val percentage = if (progress.totalPages > 0) {
            (progress.readPages * 100) / progress.totalPages
        } else 0
        
        findViewById<android.widget.ProgressBar>(R.id.cpiReading).progress = percentage
        findViewById<android.widget.TextView>(R.id.txtPercent).text = "$percentage%"
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
}