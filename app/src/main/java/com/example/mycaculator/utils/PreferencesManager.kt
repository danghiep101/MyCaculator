package com.example.mycaculator.utils

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {

    private const val PREF_NAME = "calculator_preferences"
    private const val KEY_DARK_MODE = "dark_mode"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isDarkMode(context: Context): Boolean {
        val preferences = getPreferences(context)
        return preferences.getBoolean(KEY_DARK_MODE, false)  // Default to light mode
    }

    fun setDarkMode(context: Context, isDarkMode: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_DARK_MODE, isDarkMode)
        editor.apply()
    }
}