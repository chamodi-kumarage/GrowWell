package com.example.growwell.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrefsManager {
    private const val GLOBAL_PREFS = "app_prefs"
    private const val KEY_LAST_ACTIVE_USER = "last_active_username"
    private const val KEY_PERMISSION_FLOW_COMPLETED = "permission_flow_completed"
    
    // Per-account keys
    private const val KEY_WATER_CUPS = "water_cups"
    private const val KEY_WATER_GOAL = "water_goal"
    private const val KEY_SLEEP_CONFIG = "sleep_config"
    private const val KEY_HYDRATION_CONFIG = "hydration_config"
    private const val KEY_WORKOUT_EXERCISES = "workout_exercises"
    private const val KEY_JOURNAL_ENTRIES = "journal_entries"
    private const val KEY_READING_PROGRESS = "reading_progress"
    private const val KEY_SETTINGS = "settings"
    private const val KEY_MOOD_DATA = "mood_data"
    private const val KEY_ALARM_HISTORY = "alarm_history"

    data class SleepConfig(
        val hour: Int,
        val minute: Int,
        val enabled: Boolean
    )

    data class HydrationConfig(
        val intervalMs: Long,
        val count: Int,
        val requestCodes: List<Int>
    )

    data class WorkoutExercise(
        val id: String,
        val name: String,
        val details: String,
        val isDone: Boolean = false
    )

    data class JournalEntry(
        val id: String,
        val date: Long,
        val mood: String,
        val note: String
    )

    data class ReadingProgress(
        val bookTitle: String,
        val author: String,
        val totalPages: Int,
        val readPages: Int,
        val startDate: Long
    )

    data class AppSettings(
        val soundEnabled: Boolean = true,
        val vibrateEnabled: Boolean = true
    )

    data class MoodData(
        val date: Long,
        val mood: String
    )

    data class AlarmHistoryItem(
        val id: String,
        val hour: Int,
        val minute: Int,
        val dateCreated: Long
    )

    private fun getGlobalPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(GLOBAL_PREFS, Context.MODE_PRIVATE)
    }

    private fun getUserPrefs(context: Context, username: String): SharedPreferences {
        return context.getSharedPreferences("user_$username", Context.MODE_PRIVATE)
    }

    fun setLastActiveUsername(context: Context, username: String) {
        getGlobalPrefs(context).edit()
            .putString(KEY_LAST_ACTIVE_USER, username)
            .apply()
    }

    fun getLastActiveUsername(context: Context): String? {
        return getGlobalPrefs(context).getString(KEY_LAST_ACTIVE_USER, null)
    }

    fun setPermissionFlowCompleted(context: Context, completed: Boolean) {
        getGlobalPrefs(context).edit()
            .putBoolean(KEY_PERMISSION_FLOW_COMPLETED, completed)
            .apply()
    }

    fun isPermissionFlowCompleted(context: Context): Boolean {
        return getGlobalPrefs(context).getBoolean(KEY_PERMISSION_FLOW_COMPLETED, false)
    }

    // Water tracking
    fun setWaterCups(context: Context, username: String, cups: Int) {
        getUserPrefs(context, username).edit()
            .putInt(KEY_WATER_CUPS, cups)
            .apply()
    }

    fun getWaterCups(context: Context, username: String): Int {
        return getUserPrefs(context, username).getInt(KEY_WATER_CUPS, 0)
    }

    fun setWaterGoal(context: Context, username: String, goal: Int) {
        getUserPrefs(context, username).edit()
            .putInt(KEY_WATER_GOAL, goal)
            .apply()
    }

    fun getWaterGoal(context: Context, username: String): Int {
        return getUserPrefs(context, username).getInt(KEY_WATER_GOAL, 8)
    }

    // Sleep alarm
    fun setSleepConfig(context: Context, username: String, config: SleepConfig) {
        val gson = Gson()
        val json = gson.toJson(config)
        getUserPrefs(context, username).edit()
            .putString(KEY_SLEEP_CONFIG, json)
            .apply()
    }

    fun getSleepConfig(context: Context, username: String): SleepConfig? {
        val json = getUserPrefs(context, username).getString(KEY_SLEEP_CONFIG, null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, SleepConfig::class.java)
        } else null
    }

    // Hydration reminders
    fun setHydrationConfig(context: Context, username: String, config: HydrationConfig) {
        val gson = Gson()
        val json = gson.toJson(config)
        getUserPrefs(context, username).edit()
            .putString(KEY_HYDRATION_CONFIG, json)
            .apply()
    }

    fun getHydrationConfig(context: Context, username: String): HydrationConfig? {
        val json = getUserPrefs(context, username).getString(KEY_HYDRATION_CONFIG, null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, HydrationConfig::class.java)
        } else null
    }

    // Workout exercises
    fun setWorkoutExercises(context: Context, username: String, exercises: List<WorkoutExercise>) {
        val gson = Gson()
        val json = gson.toJson(exercises)
        getUserPrefs(context, username).edit()
            .putString(KEY_WORKOUT_EXERCISES, json)
            .apply()
    }

    fun getWorkoutExercises(context: Context, username: String): List<WorkoutExercise> {
        val json = getUserPrefs(context, username).getString(KEY_WORKOUT_EXERCISES, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<WorkoutExercise>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    // Journal entries
    fun setJournalEntries(context: Context, username: String, entries: List<JournalEntry>) {
        val gson = Gson()
        val json = gson.toJson(entries)
        getUserPrefs(context, username).edit()
            .putString(KEY_JOURNAL_ENTRIES, json)
            .apply()
    }

    fun getJournalEntries(context: Context, username: String): List<JournalEntry> {
        val json = getUserPrefs(context, username).getString(KEY_JOURNAL_ENTRIES, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<JournalEntry>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    // Reading progress
    fun setReadingProgress(context: Context, username: String, progress: ReadingProgress?) {
        val gson = Gson()
        val json = if (progress != null) gson.toJson(progress) else null
        getUserPrefs(context, username).edit()
            .putString(KEY_READING_PROGRESS, json)
            .apply()
    }

    fun getReadingProgress(context: Context, username: String): ReadingProgress? {
        val json = getUserPrefs(context, username).getString(KEY_READING_PROGRESS, null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, ReadingProgress::class.java)
        } else null
    }

    // App settings
    fun setAppSettings(context: Context, username: String, settings: AppSettings) {
        val gson = Gson()
        val json = gson.toJson(settings)
        getUserPrefs(context, username).edit()
            .putString(KEY_SETTINGS, json)
            .apply()
    }

    fun getAppSettings(context: Context, username: String): AppSettings {
        val json = getUserPrefs(context, username).getString(KEY_SETTINGS, null)
        return if (json != null) {
            val gson = Gson()
            gson.fromJson(json, AppSettings::class.java)
        } else AppSettings()
    }

    // Mood data for charts
    fun setMoodData(context: Context, username: String, moodData: List<MoodData>) {
        val gson = Gson()
        val json = gson.toJson(moodData)
        getUserPrefs(context, username).edit()
            .putString(KEY_MOOD_DATA, json)
            .apply()
    }

    fun getMoodData(context: Context, username: String): List<MoodData> {
        val json = getUserPrefs(context, username).getString(KEY_MOOD_DATA, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<MoodData>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    // Alarm history
    fun setAlarmHistory(context: Context, username: String, alarms: List<AlarmHistoryItem>) {
        val gson = Gson()
        val json = gson.toJson(alarms)
        getUserPrefs(context, username).edit()
            .putString(KEY_ALARM_HISTORY, json)
            .apply()
    }

    fun getAlarmHistory(context: Context, username: String): List<AlarmHistoryItem> {
        val json = getUserPrefs(context, username).getString(KEY_ALARM_HISTORY, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<AlarmHistoryItem>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    fun addAlarmToHistory(context: Context, username: String, hour: Int, minute: Int) {
        val currentHistory = getAlarmHistory(context, username).toMutableList()
        val newAlarm = AlarmHistoryItem(
            id = System.currentTimeMillis().toString(),
            hour = hour,
            minute = minute,
            dateCreated = System.currentTimeMillis()
        )
        currentHistory.add(newAlarm)
        setAlarmHistory(context, username, currentHistory)
    }

    // Clear all data for a user
    fun clearUserData(context: Context, username: String) {
        getUserPrefs(context, username).edit().clear().apply()
    }

    // Migrate from legacy storage (if needed)
    fun migrateFromLegacyStorage(context: Context, username: String) {
        // This can be used to migrate data from old storage format
        // For now, we'll just ensure the user has default settings
        val settings = getAppSettings(context, username)
        if (settings == AppSettings()) {
            setAppSettings(context, username, AppSettings())
        }
    }
}