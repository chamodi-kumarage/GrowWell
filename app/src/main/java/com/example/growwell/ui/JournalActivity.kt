package com.example.growwell.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.EditText
import android.widget.CalendarView
import java.text.SimpleDateFormat
import java.util.*

class JournalActivity : AppCompatActivity() {
    
    private lateinit var username: String
    private lateinit var journalAdapter: JournalAdapter
    private var allEntries = mutableListOf<PrefsManager.JournalEntry>()
    private var filteredEntries = mutableListOf<PrefsManager.JournalEntry>()
    private var selectedMood = "normal"
    private var selectedDate = System.currentTimeMillis()
    private var editingEntry: PrefsManager.JournalEntry? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_journal)
        
        username = PrefsManager.getLastActiveUsername(this) ?: ""
        if (username.isBlank()) {
            Toast.makeText(this, "No active user", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        setupUI()
        loadEntries()
        setupBottomNavigation()
    }
    
    private fun setupUI() {
        val rvJournal = findViewById<RecyclerView>(R.id.rvHistory)
        val btnSaveEntry = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSaveEntry)
        val btnDeleteEntry = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDeleteEntry)
        val etNote = findViewById<EditText>(R.id.etNote)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val txtSelectedDate = findViewById<TextView>(R.id.txtSelectedDate)
        
        journalAdapter = JournalAdapter(
            filteredEntries,
            onEditClick = { entry -> editEntry(entry) },
            onDeleteClick = { entry -> deleteEntry(entry) }
        )
        
        rvJournal.layoutManager = LinearLayoutManager(this)
        rvJournal.adapter = journalAdapter
        
        // Setup emoji selection
        setupEmojiSelection()
        
        // Setup calendar
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            try {
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                txtSelectedDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate))
                updateHistoryTitle()
                filterEntriesByDate()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error selecting date: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnSaveEntry.setOnClickListener {
            val note = etNote.text.toString()
            if (note.isNotBlank()) {
                if (editingEntry != null) {
                    // Update existing entry
                    val entryIndex = allEntries.indexOfFirst { it.id == editingEntry!!.id }
                    if (entryIndex != -1) {
                        allEntries[entryIndex] = PrefsManager.JournalEntry(
                            id = editingEntry!!.id, // Keep the same ID
                            date = selectedDate,
                            mood = selectedMood,
                            note = note
                        )
                        Toast.makeText(this, "Entry updated!", Toast.LENGTH_SHORT).show()
                    }
                    editingEntry = null // Clear editing state
                } else {
                    // Create new entry
                    val entry = PrefsManager.JournalEntry(
                        id = System.currentTimeMillis().toString(),
                        date = selectedDate,
                        mood = selectedMood,
                        note = note
                    )
                    allEntries.add(0, entry) // Add to beginning
                    Toast.makeText(this, "Entry saved!", Toast.LENGTH_SHORT).show()
                }
                
                PrefsManager.setJournalEntries(this, username, allEntries)
                filterEntriesByDate()
                etNote.text.clear()
                selectedMood = "normal"
                editingEntry = null // Clear editing state
                updateEmojiSelection()
            } else {
                Toast.makeText(this, "Please write a note", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnDeleteEntry.setOnClickListener {
            etNote.text.clear()
            selectedMood = "normal"
            editingEntry = null // Clear editing state
            updateEmojiSelection()
            Toast.makeText(this, "Entry cleared", Toast.LENGTH_SHORT).show()
        }
        
        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
        
        // Set initial date
        txtSelectedDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate))
    }
    
    private fun setupEmojiSelection() {
        val emojis = mapOf(
            R.id.emojiNormal to "normal",
            R.id.emojiCool to "cool", 
            R.id.emojiAngry to "angry",
            R.id.emojiLovely to "lovely",
            R.id.emojiFunny to "funny"
        )
        
        emojis.forEach { (id, mood) ->
            findViewById<android.widget.ImageButton>(id).setOnClickListener {
                selectedMood = mood
                updateEmojiSelection()
            }
        }
        
        updateEmojiSelection()
    }
    
    private fun updateEmojiSelection() {
        val emojis = mapOf(
            R.id.emojiNormal to "normal",
            R.id.emojiCool to "cool", 
            R.id.emojiAngry to "angry",
            R.id.emojiLovely to "lovely",
            R.id.emojiFunny to "funny"
        )
        
        emojis.forEach { (id, mood) ->
            val button = findViewById<android.widget.ImageButton>(id)
            button.isSelected = mood == selectedMood
        }
    }
    
    private fun loadEntries() {
        try {
            allEntries.clear()
            allEntries.addAll(PrefsManager.getJournalEntries(this, username))
            updateHistoryTitle()
            filterEntriesByDate()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading entries: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun filterEntriesByDate() {
        try {
            filteredEntries.clear()
            
            // Get start and end of selected date
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis
            
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis
            
            // Filter entries for the selected date
            for (entry in allEntries) {
                if (entry.date >= startOfDay && entry.date < endOfDay) {
                    filteredEntries.add(entry)
                }
            }
            
            // Sort by date (newest first)
            filteredEntries.sortByDescending { it.date }
            
            if (::journalAdapter.isInitialized) {
                journalAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }
    
    private fun updateHistoryTitle() {
        try {
            val txtRecent = findViewById<TextView>(R.id.txtRecent)
            if (txtRecent != null) {
                val today = Calendar.getInstance()
                val selected = Calendar.getInstance()
                selected.timeInMillis = selectedDate
                
                val isToday = today.get(Calendar.YEAR) == selected.get(Calendar.YEAR) &&
                             today.get(Calendar.DAY_OF_YEAR) == selected.get(Calendar.DAY_OF_YEAR)
                
                txtRecent.text = if (isToday) {
                    "Today's mood history"
                } else {
                    SimpleDateFormat("MMM dd mood history", Locale.getDefault()).format(Date(selectedDate))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Silently handle the error to prevent crash
        }
    }
    
    private fun editEntry(entry: PrefsManager.JournalEntry) {
        // Set the editing entry
        editingEntry = entry
        
        // Set the selected date to the entry's date
        selectedDate = entry.date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate
        findViewById<CalendarView>(R.id.calendarView).date = selectedDate
        findViewById<TextView>(R.id.txtSelectedDate).text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate))
        
        // Set the mood selection
        selectedMood = entry.mood
        updateEmojiSelection()
        
        // Set the note text
        findViewById<EditText>(R.id.etNote).setText(entry.note)
        
        // Update the history title and filter
        updateHistoryTitle()
        filterEntriesByDate()
        
        Toast.makeText(this, "Entry loaded for editing", Toast.LENGTH_SHORT).show()
    }
    
    private fun deleteEntry(entry: PrefsManager.JournalEntry) {
        // Remove from all entries
        allEntries.removeAll { it.id == entry.id }
        
        // Save updated entries
        PrefsManager.setJournalEntries(this, username, allEntries)
        
        // Update filtered entries and refresh
        filterEntriesByDate()
        
        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
    }
    
    private fun setupBottomNavigation() {
        findViewById<android.widget.LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navHabits).setOnClickListener {
            startActivity(Intent(this, ChooseHabitActivity::class.java))
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navMood).setOnClickListener {
            // Already on mood
        }
        
        findViewById<android.widget.LinearLayout>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}

class JournalAdapter(
    private val entries: MutableList<PrefsManager.JournalEntry>,
    private val onEditClick: (PrefsManager.JournalEntry) -> Unit,
    private val onDeleteClick: (PrefsManager.JournalEntry) -> Unit
) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {
    
    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgMood: android.widget.ImageView = itemView.findViewById(R.id.imageView2)
        val txtNote: TextView = itemView.findViewById(R.id.txtNote)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val btnEdit: android.widget.ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: android.widget.ImageButton = itemView.findViewById(R.id.btnDelete)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_journal, parent, false)
        return JournalViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val entry = entries[position]
        
        // Map mood string to drawable resource
        val moodDrawable = when (entry.mood) {
            "normal" -> R.drawable.normal
            "cool" -> R.drawable.cool
            "angry" -> R.drawable.angry
            "lovely" -> R.drawable.lovely
            "funny" -> R.drawable.funny
            else -> R.drawable.normal
        }
        
        holder.imgMood.setImageResource(moodDrawable)
        holder.txtNote.text = entry.note
        holder.txtDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entry.date))
        
        // Set up click listeners
        holder.btnEdit.setOnClickListener {
            onEditClick(entry)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(entry)
        }
    }
    
    override fun getItemCount() = entries.size
}