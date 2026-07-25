package com.example.quotes_app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SessionManager @Inject constructor(
    @ApplicationContext context: Context?
) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    open fun login(username: String) {
        prefs?.edit()
            ?.putBoolean(KEY_IS_LOGGED_IN, true)
            ?.putString(KEY_USERNAME, username)
            ?.apply()
    }

    open fun logout() {
        prefs?.edit()
            ?.putBoolean(KEY_IS_LOGGED_IN, false)
            ?.remove(KEY_USERNAME)
            ?.apply()
    }

    open fun isLoggedIn(): Boolean {
        return prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
    }

    open fun getUsername(): String? {
        return prefs?.getString(KEY_USERNAME, null)
    }

    companion object {
        private const val PREF_NAME = "quotes_app_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
    }
}
