package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R

class ChooseHabitActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_habit)
        
        // click listeners for habit
        findViewById<android.widget.LinearLayout>(R.id.optDrink).setOnClickListener {
            try {
                startActivity(Intent(this, WaterActivity::class.java))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Water activity error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.LinearLayout>(R.id.optMeditate).setOnClickListener {
            try {
                startActivity(Intent(this, MeditateActivity::class.java))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Meditate activity error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.LinearLayout>(R.id.optSleep).setOnClickListener {
            try {
                startActivity(Intent(this, SleepActivity::class.java))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Sleep activity error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.LinearLayout>(R.id.optWorkout).setOnClickListener {
            try {
                startActivity(Intent(this, WorkoutActivity::class.java))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Workout activity error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.LinearLayout>(R.id.optJournal).setOnClickListener {
            try {
                startActivity(Intent(this, JournalActivity::class.java))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Journal activity error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        findViewById<android.widget.LinearLayout>(R.id.optReading).setOnClickListener {
            try {
                startActivity(Intent(this, ReadingActivity::class.java))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Reading activity error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
        
        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        findViewById<android.widget.LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navHabits).setOnClickListener {
            // Already on habits
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navMood).setOnClickListener {
            startActivity(Intent(this, JournalActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}