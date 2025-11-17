package com.example.growwell

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.growwell.alarms.AlarmReceiver
import com.example.growwell.alarms.HydrationReceiver
import com.example.growwell.alarms.SleepAlarmReceiver
import com.example.growwell.prefs.PrefsManager
import java.util.Calendar

object AlarmScheduler {
    
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun scheduleDailySleepAlarm(context: Context, username: String, hour: Int, minute: Int): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel existing sleep alarm
        cancelSleepAlarm(context)
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If the time has passed today, set for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val intent = Intent(context, SleepAlarmReceiver::class.java).apply {
            putExtra("username", username)
            putExtra("type", "sleep")
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            424242,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            
            // Save config
            PrefsManager.setSleepConfig(context, username, PrefsManager.SleepConfig(hour, minute, true))
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun cancelSleepAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SleepAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            424242,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleHydrationBatch(
        context: Context, 
        username: String, 
        intervalMs: Long, 
        count: Int
    ): List<Int> {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel existing hydration alarms
        cancelAllHydrationAlarms(context, username)
        
        val requestCodes = mutableListOf<Int>()
        val now = System.currentTimeMillis()
        
        repeat(count) { index ->
            val triggerTime = now + (intervalMs * (index + 1))
            val requestCode = (triggerTime % Int.MAX_VALUE).toInt()
            requestCodes.add(requestCode)
            
            val intent = Intent(context, HydrationReceiver::class.java).apply {
                putExtra("username", username)
                putExtra("type", "hydration")
                putExtra("requestCode", requestCode)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Handle permission denied
            }
        }
        
        // Save config
        PrefsManager.setHydrationConfig(
            context, 
            username, 
            PrefsManager.HydrationConfig(intervalMs, count, requestCodes)
        )
        
        return requestCodes
    }

    fun cancelAllHydrationAlarms(context: Context, username: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val config = PrefsManager.getHydrationConfig(context, username)
        
        config?.requestCodes?.forEach { requestCode ->
            val intent = Intent(context, HydrationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
        
        // Clear config
        PrefsManager.setHydrationConfig(context, username, PrefsManager.HydrationConfig(0, 0, emptyList()))
    }

    fun rescheduleAllAlarms(context: Context, username: String) {
        // Reschedule sleep alarm
        val sleepConfig = PrefsManager.getSleepConfig(context, username)
        sleepConfig?.let { config ->
            if (config.enabled) {
                scheduleDailySleepAlarm(context, username, config.hour, config.minute)
            }
        }
        
        // Reschedule hydration alarms
        val hydrationConfig = PrefsManager.getHydrationConfig(context, username)
        hydrationConfig?.let { config ->
            if (config.count > 0) {
                scheduleHydrationBatch(context, username, config.intervalMs, config.count)
            }
        }
    }
}