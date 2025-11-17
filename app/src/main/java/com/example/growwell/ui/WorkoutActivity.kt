package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.ImageButton

class WorkoutActivity : AppCompatActivity() {
    
    private lateinit var username: String
    private lateinit var exerciseAdapter: ExerciseAdapter
    private var exercises = mutableListOf<PrefsManager.WorkoutExercise>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_workout)
        
        username = PrefsManager.getLastActiveUsername(this) ?: ""
        if (username.isBlank()) {
            Toast.makeText(this, "No active user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        loadExercises()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        val rvExercises = findViewById<RecyclerView>(R.id.rvExercises)
        val btnAddExercise = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddExercise)
        
        exerciseAdapter = ExerciseAdapter(exercises) { exercise, action ->
            when (action) {
                "edit" -> showEditDialog(exercise)
                "delete" -> deleteExercise(exercise)
                "toggle" -> toggleExercise(exercise)
            }
        }
        
        rvExercises.layoutManager = LinearLayoutManager(this)
        rvExercises.adapter = exerciseAdapter
        
        btnAddExercise.setOnClickListener {
            showAddDialog()
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun loadExercises() {
        exercises.clear()
        exercises.addAll(PrefsManager.getWorkoutExercises(this, username))
        exerciseAdapter.notifyDataSetChanged()
        updateProgress()
    }
    
    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exercise, null)
        val etName = dialogView.findViewById<android.widget.EditText>(R.id.etName)
        val etDetails = dialogView.findViewById<android.widget.EditText>(R.id.etDetails)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Exercise")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val details = etDetails.text.toString()
                
                if (name.isNotBlank()) {
                    val exercise = PrefsManager.WorkoutExercise(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        details = details,
                        isDone = false
                    )
                    exercises.add(exercise)
                    PrefsManager.setWorkoutExercises(this, username, exercises)
                    exerciseAdapter.notifyDataSetChanged()
                    updateProgress()
                    Toast.makeText(this, "Exercise added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter exercise name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    private fun showEditDialog(exercise: PrefsManager.WorkoutExercise) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exercise, null)
        val etName = dialogView.findViewById<android.widget.EditText>(R.id.etName)
        val etDetails = dialogView.findViewById<android.widget.EditText>(R.id.etDetails)
        
        etName.setText(exercise.name)
        etDetails.setText(exercise.details)
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Edit Exercise")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val details = etDetails.text.toString()
                
                if (name.isNotBlank()) {
                    val index = exercises.indexOf(exercise)
                    if (index != -1) {
                        exercises[index] = exercise.copy(name = name, details = details)
                        PrefsManager.setWorkoutExercises(this, username, exercises)
                        exerciseAdapter.notifyItemChanged(index)
                        Toast.makeText(this, "Exercise updated!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter exercise name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    private fun deleteExercise(exercise: PrefsManager.WorkoutExercise) {
        exercises.remove(exercise)
        PrefsManager.setWorkoutExercises(this, username, exercises)
        exerciseAdapter.notifyDataSetChanged()
        updateProgress()
        Toast.makeText(this, "Exercise deleted!", Toast.LENGTH_SHORT).show()
    }
    
    private fun toggleExercise(exercise: PrefsManager.WorkoutExercise) {
        val index = exercises.indexOf(exercise)
        if (index != -1) {
            exercises[index] = exercise.copy(isDone = !exercise.isDone)
            PrefsManager.setWorkoutExercises(this, username, exercises)
            exerciseAdapter.notifyItemChanged(index)
            updateProgress()
        }
    }
    
    private fun updateProgress() {
        val doneCount = exercises.count { it.isDone }
        val totalCount = exercises.size
        val percentage = if (totalCount > 0) (doneCount * 100) / totalCount else 0
        
        findViewById<android.widget.ProgressBar>(R.id.cpiWorkout).progress = percentage
        findViewById<android.widget.TextView>(R.id.txtProgress).text = "$percentage%"
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

class ExerciseAdapter(
    private val exercises: List<PrefsManager.WorkoutExercise>,
    private val onAction: (PrefsManager.WorkoutExercise, String) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    
    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtDetails: TextView = itemView.findViewById(R.id.txtDetails)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.txtName.text = exercise.name
        holder.txtDetails.text = exercise.details
        holder.cbDone.isChecked = exercise.isDone
        
        holder.cbDone.setOnCheckedChangeListener { _, _ ->
            onAction(exercise, "toggle")
        }
        
        holder.btnEdit.setOnClickListener {
            onAction(exercise, "edit")
        }
        
        holder.btnDelete.setOnClickListener {
            onAction(exercise, "delete")
        }
    }
    
    override fun getItemCount() = exercises.size
}