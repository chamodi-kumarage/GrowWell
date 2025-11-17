package com.example.growwell.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HydrationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val username = intent.getStringExtra("username") ?: return
        val requestCode = intent.getIntExtra("requestCode", 0)
        
        // Forward to main alarm receiver
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("username", username)
            putExtra("type", "hydration")
            putExtra("requestCode", requestCode)
        }
        context.sendBroadcast(alarmIntent)
    }
}