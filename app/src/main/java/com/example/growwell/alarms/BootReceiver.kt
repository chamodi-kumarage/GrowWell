package com.example.growwell.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.growwell.AlarmScheduler
import com.example.growwell.prefs.PrefsManager

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // Reschedule all alarms for the last active user
                val username = PrefsManager.getLastActiveUsername(context)
                username?.let { user ->
                    AlarmScheduler.rescheduleAllAlarms(context, user)
                }
            }
        }
    }
}