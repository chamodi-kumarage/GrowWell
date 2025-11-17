package com.example.growwell.prefs

import android.content.Context
import android.content.SharedPreferences

object Storage {
    private const val PREFS_NAME = "growwell_storage"
    private const val KEY_USER = "user"
    private const val KEY_ONBOARDED = "onboarded"

    data class User(val username: String, val password: String)
    data class Sleep(val hour: Int, val minute: Int, val enabled: Boolean, val req: Int)
    data class Reminder(val timeMillis: Long, val requestCode: Int)

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, username: String, password: String) {
        try {
            val user = User(username, password)
            val gson = com.google.gson.Gson()
            val json = gson.toJson(user)
            getPrefs(context).edit()
                .putString(KEY_USER, json)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUser(context: Context): Pair<String, String>? {
        return try {
            val json = getPrefs(context).getString(KEY_USER, null)
            if (json != null) {
                val gson = com.google.gson.Gson()
                val user = gson.fromJson(json, User::class.java)
                Pair(user.username, user.password)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getUsername(context: Context): String? {
        return getUser(context)?.first
    }

    fun setOnboarded(context: Context, onboarded: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_ONBOARDED, onboarded)
            .apply()
    }

    fun isOnboarded(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDED, false)
    }

    // Legacy methods for backward compatibility
    fun setSleep(context: Context, sleep: Sleep) {
        val gson = com.google.gson.Gson()
        val json = gson.toJson(sleep)
        getPrefs(context).edit()
            .putString("sleep", json)
            .apply()
    }

    fun getSleep(context: Context): Sleep? {
        val json = getPrefs(context).getString("sleep", null)
        return if (json != null) {
            val gson = com.google.gson.Gson()
            gson.fromJson(json, Sleep::class.java)
        } else null
    }

    fun setHydrationReminders(context: Context, reminders: List<Reminder>) {
        val gson = com.google.gson.Gson()
        val json = gson.toJson(reminders)
        getPrefs(context).edit()
            .putString("hydration_reminders", json)
            .apply()
    }

    fun getHydrationReminders(context: Context): List<Reminder> {
        val json = getPrefs(context).getString("hydration_reminders", null)
        return if (json != null) {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<Reminder>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }
}