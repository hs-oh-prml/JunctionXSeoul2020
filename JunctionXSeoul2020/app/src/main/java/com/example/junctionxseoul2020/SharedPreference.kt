package com.example.junctionxseoul2020

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val PREF_USER_UID = "userUID"
    val PREF_USER_HASHCODE = "userHASHCODE"

    fun setUserInfo(userUID: String, hashCode: String) {
        val editor = prefs.edit()
        editor.putString(PREF_USER_UID, userUID)
        editor.putString(PREF_USER_HASHCODE, hashCode)
        editor.apply()
    }

    fun getUserUID(): String? {
        return prefs.getString(PREF_USER_UID, null)
    }

    fun getUserHASHCODE(): String? {
        return prefs.getString(PREF_USER_HASHCODE, null)
    }
}
