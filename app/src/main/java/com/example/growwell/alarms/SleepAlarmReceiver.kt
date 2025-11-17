package com.example.growwell.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SleepAlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val username = intent.getStringExtra("username") ?: return
        
        // Forward to main alarm receiver
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("username", username)
            putExtra("type", "sleep")
        }
        context.sendBroadcast(alarmIntent)
    }
}