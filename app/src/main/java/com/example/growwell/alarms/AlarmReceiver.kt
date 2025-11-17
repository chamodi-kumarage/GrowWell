package com.example.growwell.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.growwell.R
import com.example.growwell.prefs.PrefsManager
import com.example.growwell.ui.AlarmRingActivity

class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val CHANNEL_ID = "alarms"
        const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val username = intent.getStringExtra("username") ?: return
        val type = intent.getStringExtra("type") ?: return
        
        // Create notification channel
        createNotificationChannel(context)
        
        // Get user settings
        val settings = PrefsManager.getAppSettings(context, username)
        
        // Show notification
        showAlarmNotification(context, type, settings)
        
        // Start alarm activity
        val alarmIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("username", username)
            putExtra("type", type)
            putExtra("soundEnabled", settings.soundEnabled)
            putExtra("vibrateEnabled", settings.vibrateEnabled)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(alarmIntent)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for sleep alarms and hydration reminders"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showAlarmNotification(context: Context, type: String, settings: PrefsManager.AppSettings) {
        val title = when (type) {
            "sleep" -> "Sleep Alarm"
            "hydration" -> "Hydration Reminder"
            else -> "Alarm"
        }
        
        val message = when (type) {
            "sleep" -> "Time to sleep! Good night!"
            "hydration" -> "Time to drink water! Stay hydrated!"
            else -> "Alarm notification"
        }
        
        val stopIntent = Intent(context, AlarmStopReceiver::class.java).apply {
            putExtra("type", type)
        }
        
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(
                R.drawable.stop,
                "Stop Alarm",
                stopPendingIntent
            )
            .setFullScreenIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, AlarmRingActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ),
                true
            )
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}