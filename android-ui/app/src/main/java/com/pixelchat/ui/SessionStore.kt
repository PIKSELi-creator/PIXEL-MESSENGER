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
    val username: String,
    val bio: String = "",
    val birthDate: String = "",
    val photoUri: String = "",
    val backgroundUri: String = "",
    val musicUri: String = "",
    val musicName: String = ""
)

class SessionStore(
    private val context: Context
) {

    private companion object {
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val EMAIL = stringPreferencesKey("email")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val USERNAME = stringPreferencesKey("username")
        val BIO = stringPreferencesKey("bio")
        val BIRTH_DATE = stringPreferencesKey("birth_date")
        val PHOTO_URI = stringPreferencesKey("photo_uri")
        val BACKGROUND_URI = stringPreferencesKey("background_uri")
        val MUSIC_URI = stringPreferencesKey("music_uri")
        val MUSIC_NAME = stringPreferencesKey("music_name")
    }

    suspend fun saveSession(
        email: String,
        displayName: String,
        username: String,
        bio: String = "",
        birthDate: String = "",
        photoUri: String = "",
        backgroundUri: String = ""
    ) {
        context.pixelChatDataStore.edit { preferences ->
            preferences[LOGGED_IN] = true
            preferences[EMAIL] = email
            preferences[DISPLAY_NAME] = displayName
            preferences[USERNAME] = username
            preferences[BIO] = bio
            preferences[BIRTH_DATE] = birthDate
            preferences[PHOTO_URI] = photoUri
            preferences[BACKGROUND_URI] = backgroundUri
        }
    }

    suspend fun saveMusic(uri: String, name: String) {
        context.pixelChatDataStore.edit { preferences ->
            preferences[MUSIC_URI] = uri
            preferences[MUSIC_NAME] = name
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
            username = preferences[USERNAME].orEmpty(),
            bio = preferences[BIO].orEmpty(),
            birthDate = preferences[BIRTH_DATE].orEmpty(),
            photoUri = preferences[PHOTO_URI].orEmpty(),
            backgroundUri = preferences[BACKGROUND_URI].orEmpty(),
            musicUri = preferences[MUSIC_URI].orEmpty(),
            musicName = preferences[MUSIC_NAME].orEmpty()
        )
    }

    suspend fun clearSession() {
        context.pixelChatDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
