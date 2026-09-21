package com.pixelchat.ui

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.pixelChatDataStore by preferencesDataStore(
    name = "pixel_chat_session"
)

data class PixelSession(
    val email: String,
    val displayName: String,
    val username: String
)

class SessionStore(
    private val context: Context
) {

    private companion object {
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val EMAIL = stringPreferencesKey("email")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val USERNAME = stringPreferencesKey("username")
    }

    suspend fun saveSession(
        email: String,
        displayName: String,
        username: String
    ) {
        context.pixelChatDataStore.edit { preferences ->
            preferences[LOGGED_IN] = true
            preferences[EMAIL] = email
            preferences[DISPLAY_NAME] = displayName
            preferences[USERNAME] = username
        }
    }

    suspend fun getSession(): PixelSession? {
        val preferences = context.pixelChatDataStore.data.first()

        if (preferences[LOGGED_IN] != true) {
            return null
        }

        val email = preferences[EMAIL] ?: return null

        return PixelSession(
            email = email,
            displayName = preferences[DISPLAY_NAME].orEmpty(),
            username = preferences[USERNAME].orEmpty()
        )
    }

    suspend fun clearSession() {
        context.pixelChatDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
