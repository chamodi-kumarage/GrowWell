package com.example.growwell.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import java.text.SimpleDateFormat
import java.util.*

class AlarmHistoryAdapter(
    private val alarms: List<PrefsManager.AlarmHistoryItem>,
    private val onReuseAlarm: (PrefsManager.AlarmHistoryItem) -> Unit
) : RecyclerView.Adapter<AlarmHistoryAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
        val btnUse: ImageButton = itemView.findViewById(R.id.btnUse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sleep_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        
        // Format time (e.g., "10:30 PM")
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
        }
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        holder.tvTime.text = timeFormat.format(calendar.time)
        
        // Set label
        holder.tvLabel.text = "Tap to reuse"
        
        // Set reuse button click listener
        holder.btnUse.setOnClickListener {
            onReuseAlarm(alarm)
        }
    }

    override fun getItemCount(): Int = alarms.size
}

