package com.pixelchat.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

class PixelBoolPref internal constructor(
    private val prefs: SharedPreferences,
    private val key: String,
    default: Boolean
) {
    var value by mutableStateOf(prefs.getBoolean(key, default))
        private set

    fun set(v: Boolean) {
        value = v
        prefs.edit().putBoolean(key, v).apply()
    }
}

class PixelIntPref internal constructor(
    private val prefs: SharedPreferences,
    private val key: String,
    default: Int
) {
    var value by mutableStateOf(prefs.getInt(key, default))
        private set

    fun set(v: Int) {
        value = v
        prefs.edit().putInt(key, v).apply()
    }
}

class PixelFloatPref internal constructor(
    private val prefs: SharedPreferences,
    private val key: String,
    default: Float
) {
    var value by mutableStateOf(prefs.getFloat(key, default))
        private set

    fun set(v: Float) {
        value = v
        prefs.edit().putFloat(key, v).apply()
    }
}

class PixelSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            "pixel_settings",
            Context.MODE_PRIVATE
        )

    val themeMode = PixelIntPref(prefs, "theme_mode", 0)
    val dynamicColor = PixelBoolPref(prefs, "dynamic_color", true)
    val textScale = PixelFloatPref(prefs, "text_scale", 1f)

    val notifyMessages = PixelBoolPref(prefs, "notify_messages", true)
    val notifyPreview = PixelBoolPref(prefs, "notify_preview", true)
    val notifySound = PixelBoolPref(prefs, "notify_sound", true)
    val notifyVibrate = PixelBoolPref(prefs, "notify_vibrate", true)
    val lastSeen = PixelIntPref(prefs, "last_seen", 0)
    val readReceipts = PixelBoolPref(prefs, "read_receipts", true)
    val enterToSend = PixelBoolPref(prefs, "enter_to_send", false)
    val autoDownloadWifi = PixelBoolPref(prefs, "auto_download_wifi", true)
}

val LocalPixelSettings = staticCompositionLocalOf<PixelSettings> {
    error("PixelSettings is not provided. Wrap the app in PixelAppTheme { ... }")
}

@Composable
fun PixelAppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settings = remember { PixelSettings(context) }
    val systemDark = isSystemInDarkTheme()

    val dark = when (settings.themeMode.value) {
        1 -> false
        2 -> true
        else -> systemDark
    }

    val colorScheme = when {
        settings.dynamicColor.value && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        dark -> darkColorScheme(
            primary = Color(0xFFA7C7FF),
            onPrimary = Color(0xFF10243A),
            secondary = Color(0xFFD8B89A),
            background = Color(0xFF151311),
            surface = Color(0xFF1D1A18),
            onSurface = Color(0xFFF0E9E2),
            surfaceVariant = Color(0xFF3B342F),
            onSurfaceVariant = Color(0xFFC9BDB2),
            error = Color(0xFFFFB4AB)
        )

        else -> lightColorScheme(
            primary = Color(0xFF42658A),
            background = Color(0xFFF7F2ED),
            surface = Color(0xFFFFF9F4),
            onSurface = Color(0xFF211B17),
            surfaceVariant = Color(0xFFECE2D9),
            onSurfaceVariant = Color(0xFF51463E)
        )
    }

    val density = LocalDensity.current

    CompositionLocalProvider(
        LocalPixelSettings provides settings,
        LocalDensity provides Density(
            density = density.density,
            fontScale = density.fontScale * settings.textScale.value
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            shapes = Shapes(),
            content = content
        )
    }
}
