package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.widget.ProgressBar
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        
        val username = PrefsManager.getLastActiveUsername(this)
        
        if (username.isNullOrBlank()) {
            // No active user, redirect to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        try {
            setupUI(username)
            setupBottomNavigation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupUI(username: String) {
        try {
            // Set greeting
            val txtGreeting = findViewById<TextView>(R.id.txtGreeting)
            txtGreeting.text = "Hi, $username 👋"
            
            // Set today's date
            val txtToday = findViewById<TextView>(R.id.txtToday)
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            txtToday.text = dateFormat.format(Date())
            
            // Setup water progress
            setupWaterProgress(username)
            
            // Setup workout progress
            setupWorkoutProgress(username)
            
            // Setup book progress
            setupBookProgress(username)
            
            // Setup mood chart
            setupMoodChart(username)
        } catch (e: Exception) {
            e.printStackTrace()
            // UI setup failed, but don't crash the app
        }
    }
    
    private fun setupWaterProgress(username: String) {
        try {
            val cpiWater = findViewById<ProgressBar>(R.id.cpWater)
            val txtProgress = findViewById<TextView>(R.id.txtProgress)
            
            val cups = PrefsManager.getWaterCups(this, username)
            val goal = PrefsManager.getWaterGoal(this, username)
            val percentage = if (goal > 0) (cups * 100) / goal else 0
            
            cpiWater.progress = percentage
            txtProgress.text = "$percentage%"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupWorkoutProgress(username: String) {
        try {
            val cpiWorkout = findViewById<ProgressBar>(R.id.cpWorkout)
            val txtWorkoutProgress = findViewById<TextView>(R.id.txtWorkoutProgress)
            
            val exercises = PrefsManager.getWorkoutExercises(this, username)
            val doneCount = exercises.count { it.isDone }
            val totalCount = exercises.size
            val percentage = if (totalCount > 0) (doneCount * 100) / totalCount else 0
            
            cpiWorkout.progress = percentage
            txtWorkoutProgress.text = "$percentage%"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupBookProgress(username: String) {
        try {
            val cpiBook = findViewById<ProgressBar>(R.id.cpBook)
            val txtBookProgress = findViewById<TextView>(R.id.txtBookProgress)
            
            val readingProgress = PrefsManager.getReadingProgress(this, username)
            val percentage = if (readingProgress != null && readingProgress.totalPages > 0) {
                (readingProgress.readPages * 100) / readingProgress.totalPages
            } else 0
            
            cpiBook.progress = percentage
            txtBookProgress.text = "$percentage%"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupMoodChart(username: String) {
        try {
            val chartMood = findViewById<LineChart>(R.id.chartMood)

            // Get journal entries and convert to mood data for chart
            val journalEntries = PrefsManager.getJournalEntries(this, username)
            val moodData = journalEntries.map { entry ->
                PrefsManager.MoodData(entry.date, entry.mood)
            }

            if (moodData.isNotEmpty()) {
                // Sort by date and take last 7 entries
                val sortedMoodData = moodData.sortedBy { it.date }.takeLast(7)

                val entries = sortedMoodData.mapIndexed { index, moodData ->
                    val moodValue = when (moodData.mood) {
                        "normal" -> 3f
                        "cool" -> 4f
                        "angry" -> 1f
                        "lovely" -> 5f
                        "funny" -> 4f
                        else -> 3f
                    }
                    Entry(index.toFloat(), moodValue)
                }

                val dataSet = LineDataSet(entries, "Mood Trend").apply {
                    color = getColor(R.color.growell_pink)
                    setCircleColor(getColor(R.color.growell_pink))
                    lineWidth = 3f
                    circleRadius = 6f
                    setDrawValues(true)
                    valueTextSize = 12f
                    valueTextColor = getColor(R.color.white)
                    setDrawFilled(true)
                    fillColor = getColor(R.color.growell_pink)
                    fillAlpha = 50
                }

                val lineData = LineData(dataSet)
                chartMood.data = lineData

                // Configure chart appearance
                chartMood.description.text = "Last 7 Days Mood History"
                chartMood.description.textSize = 14f
                chartMood.description.textColor = getColor(R.color.white)
                chartMood.description.setPosition(800f, 50f)

                chartMood.legend.isEnabled = true
                chartMood.legend.textColor = getColor(R.color.white)
                chartMood.legend.textSize = 12f

                chartMood.setTouchEnabled(true)
                chartMood.setPinchZoom(true)
                chartMood.setScaleEnabled(true)

                // Set Y-axis range and labels (1-5 for mood scale)
                val leftAxis = chartMood.axisLeft
                leftAxis.axisMinimum = 0.5f
                leftAxis.axisMaximum = 5.5f
                leftAxis.setDrawGridLines(true)
                leftAxis.gridColor = getColor(R.color.white)
                leftAxis.gridLineWidth = 1f
                leftAxis.setDrawAxisLine(true)
                leftAxis.axisLineColor = getColor(R.color.white)
                leftAxis.axisLineWidth = 2f
                leftAxis.setDrawLabels(true)
                leftAxis.textColor = getColor(R.color.white)
                leftAxis.textSize = 12f
                leftAxis.setLabelCount(6, true) // 0.5, 1.5, 2.5, 3.5, 4.5, 5.5

                // Add custom Y-axis labels for mood levels
                leftAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            1 -> "😠 Angry"
                            2 -> "😔 Sad"
                            3 -> "😐 Normal"
                            4 -> "😊 Good"
                            5 -> "😍 Lovely"
                            else -> ""
                        }
                    }
                }

                val rightAxis = chartMood.axisRight
                rightAxis.isEnabled = false

                val xAxis = chartMood.xAxis
                xAxis.setDrawGridLines(true)
                xAxis.gridColor = getColor(R.color.white)
                xAxis.gridLineWidth = 1f
                xAxis.setDrawAxisLine(true)
                xAxis.axisLineColor = getColor(R.color.white)
                xAxis.axisLineWidth = 2f
                xAxis.setDrawLabels(true)
                xAxis.textColor = getColor(R.color.white)
                xAxis.textSize = 10f
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

                // Add custom X-axis labels for dates
                xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        if (index >= 0 && index < sortedMoodData.size) {
                            val date = Date(sortedMoodData[index].date)
                            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                            return dateFormat.format(date)
                        }
                        return ""
                    }
                }

                // Enable animations
                chartMood.animateY(1000)

                chartMood.invalidate()
            } else {
                // No data case - show empty chart with message
                chartMood.clear()
                chartMood.description.text = "No mood data available"
                chartMood.description.textSize = 14f
                chartMood.description.textColor = getColor(R.color.white)
                chartMood.description.setPosition(800f, 50f)
                chartMood.invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Chart setup failed, but don't crash the app
        }
    }


    private fun setupBottomNavigation() {
        findViewById<android.widget.LinearLayout>(R.id.navHome).setOnClickListener {
            // Already on home
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

