package edu.merostate.assignment2

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREFS_NAME = "todo_prefs"
    private const val KEY_USER_TOKEN = "user_token"
    private const val KEY_USER_ID = "user_id"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_TOKEN, token)
        editor.apply()
    }

    fun getUserToken(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_TOKEN, null)
    }

    fun saveUserId(context: Context, userId: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ID, null)
    }
}
