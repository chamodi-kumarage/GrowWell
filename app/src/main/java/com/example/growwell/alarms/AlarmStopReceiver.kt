package com.example.growwell.alarms

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmStopReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Cancel notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID)
        
        // Send broadcast to stop alarm activity
        val stopIntent = Intent("com.example.growwell.STOP_ALARM")
        context.sendBroadcast(stopIntent)
    }
}