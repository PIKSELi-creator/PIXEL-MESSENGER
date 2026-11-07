package com.pixelchat.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedScroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

/* ------------------------------------------------------------------ */
/*  Persisted settings (SharedPreferences, no new dependencies)       */
/* ------------------------------------------------------------------ */

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
        context.applicationContext.getSharedPreferences("pixel_settings", Context.MODE_PRIVATE)

    // Appearance (applied right away by PixelAppTheme)
    val themeMode = PixelIntPref(prefs, "theme_mode", 0)          // 0 system, 1 light, 2 dark
    val dynamicColor = PixelBoolPref(prefs, "dynamic_color", true)
    val textScale = PixelFloatPref(prefs, "text_scale", 1f)

    // Stored now, used later by notifications / server features
    val notifyMessages = PixelBoolPref(prefs, "notify_messages", true)
    val notifyPreview = PixelBoolPref(prefs, "notify_preview", true)
    val notifySound = PixelBoolPref(prefs, "notify_sound", true)
    val notifyVibrate = PixelBoolPref(prefs, "notify_vibrate", true)
    val lastSeen = PixelIntPref(prefs, "last_seen", 0)            // 0 everyone, 1 contacts, 2 nobody
    val readReceipts = PixelBoolPref(prefs, "read_receipts", true)
    val enterToSend = PixelBoolPref(prefs, "enter_to_send", false)
    val autoDownloadWifi = PixelBoolPref(prefs, "auto_download_wifi", true)
}

val LocalPixelSettings = staticCompositionLocalOf<PixelSettings> {
    error("PixelSettings is not provided. Wrap the app in PixelAppTheme { ... }")
}

/**
 * Replaces the body of PixelChatTheme in MainActivity.kt:
 *   setContent { PixelAppTheme { PixelChatApp() } }
 */
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
        dark -> darkColorScheme()
        else -> lightColorScheme()
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

/* ------------------------------------------------------------------ */
/*  Settings screen                                                   */
/* ------------------------------------------------------------------ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixelSettingsScreen(
    name: String,
    username: String,
    photoUri: Uri?,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val settings = LocalPixelSettings.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showLastSeen by rememberSaveable { mutableStateOf(false) }
    var showClearCache by rememberSaveable { mutableStateOf(false) }
    var showLogout by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    var cacheBytes by remember { mutableStateOf(0L) }
    var cacheTick by remember { mutableStateOf(0) }
    LaunchedEffect(cacheTick) {
        cacheBytes = withContext(Dispatchers.IO) { dirSize(context.cacheDir) }
    }

    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
        } catch (_: Exception) {
            ""
        }
    }

    var textScale by remember { mutableStateOf(settings.textScale.value) }
    val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    fun soon() {
        scope.launch { snackbar.showSnackbar("Появится вместе с серверной частью") }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            LargeTopAppBar(
                title = { Text("Настройки") },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            /* Account */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(onClick = onOpenProfile),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Фото профиля",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = name.trim().firstOrNull()?.uppercase() ?: "P",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = name.ifBlank { "PIXEL" },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "@" + username.removePrefix("@").ifBlank { "pixelchat" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* Appearance */
            SettingsHeader("Оформление")

            ListItem(
                leadingContent = { Icon(Icons.Outlined.Palette, contentDescription = null) },
                headlineContent = { Text("Тема") },
                supportingContent = {
                    val labels = listOf("Системная", "Светлая", "Тёмная")
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        labels.forEachIndexed { index, label ->
                            SegmentedButton(
                                selected = settings.themeMode.value == index,
                                onClick = { settings.themeMode.set(index) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = labels.size
                                )
                            ) {
                                Text(label, maxLines = 1)
                            }
                        }
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            SettingsSwitchRow(
                icon = Icons.Outlined.Wallpaper,
                title = "Динамические цвета",
                subtitle = if (dynamicSupported) {
                    "Цвета берутся из обоев системы"
                } else {
                    "Нужен Android 12 или новее"
                },
                checked = settings.dynamicColor.value && dynamicSupported,
                enabled = dynamicSupported,
                onCheckedChange = { settings.dynamicColor.set(it) }
            )

            ListItem(
                leadingContent = { Icon(Icons.Outlined.TextFields, contentDescription = null) },
                headlineContent = { Text("Размер текста") },
                supportingContent = {
                    Slider(
                        value = textScale,
                        onValueChange = { textScale = it },
                        onValueChangeFinished = { settings.textScale.set(textScale) },
                        valueRange = 0.85f..1.3f,
                        steps = 2
                    )
                },
                trailingContent = {
                    Text(
                        text = "${(textScale * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            /* Notifications */
            SettingsHeader("Уведомления")
            SettingsSwitchRow(
                icon = Icons.Outlined.Notifications,
                title = "Новые сообщения",
                subtitle = "Показывать уведомления чатов",
                checked = settings.notifyMessages.value,
                onCheckedChange = { settings.notifyMessages.set(it) }
            )
            SettingsSwitchRow(
                icon = Icons.Outlined.Visibility,
                title = "Предпросмотр",
                subtitle = "Показывать текст сообщения",
                checked = settings.notifyPreview.value,
                enabled = settings.notifyMessages.value,
                onCheckedChange = { settings.notifyPreview.set(it) }
            )
            SettingsSwitchRow(
                icon = Icons.Outlined.MusicNote,
                title = "Звук",
                subtitle = null,
                checked = settings.notifySound.value,
                enabled = settings.notifyMessages.value,
                onCheckedChange = { settings.notifySound.set(it) }
            )
            SettingsSwitchRow(
                icon = Icons.Outlined.Vibration,
                title = "Вибрация",
                subtitle = null,
                checked = settings.notifyVibrate.value,
                enabled = settings.notifyMessages.value,
                onCheckedChange = { settings.notifyVibrate.set(it) }
            )

            /* Privacy */
            SettingsHeader("Конфиденциальность")
            SettingsNavRow(
                icon = Icons.Outlined.Schedule,
                title = "Время захода",
                subtitle = lastSeenLabels[settings.lastSeen.value.coerceIn(0, 2)],
                onClick = { showLastSeen = true }
            )
            SettingsSwitchRow(
                icon = Icons.Outlined.DoneAll,
                title = "Отметки о прочтении",
                subtitle = "Собеседники видят, что вы прочитали сообщение",
                checked = settings.readReceipts.value,
                onCheckedChange = { settings.readReceipts.set(it) }
            )
            SettingsNavRow(
                icon = Icons.Outlined.Block,
                title = "Заблокированные",
                subtitle = "Скоро",
                onClick = { soon() }
            )

            /* Chats */
            SettingsHeader("Чаты")
            SettingsSwitchRow(
                icon = Icons.Outlined.Keyboard,
                title = "Отправка по Enter",
                subtitle = "Клавиша Enter отправляет сообщение",
                checked = settings.enterToSend.value,
                onCheckedChange = { settings.enterToSend.set(it) }
            )
            SettingsSwitchRow(
                icon = Icons.Outlined.CloudDownload,
                title = "Автозагрузка по Wi-Fi",
                subtitle = "Фото и видео из чатов",
                checked = settings.autoDownloadWifi.value,
                onCheckedChange = { settings.autoDownloadWifi.set(it) }
            )

            /* Data */
            SettingsHeader("Данные и память")
            SettingsNavRow(
                icon = Icons.Outlined.Storage,
                title = "Очистить кэш",
                subtitle = Formatter.formatShortFileSize(context, cacheBytes),
                onClick = { showClearCache = true }
            )

            /* Security */
            SettingsHeader("Безопасность")
            SettingsNavRow(
                icon = Icons.Outlined.Devices,
                title = "Устройства",
                subtitle = "Скоро",
                onClick = { soon() }
            )

            /* About */
            SettingsHeader("О приложении")
            SettingsNavRow(
                icon = Icons.Outlined.Info,
                title = "PIXEL CHAT",
                subtitle = if (versionName.isBlank()) "Версия неизвестна" else "Версия $versionName",
                onClick = { showAbout = true }
            )

            Spacer(Modifier.height(12.dp))

            ListItem(
                modifier = Modifier.clickable { showLogout = true },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.PowerSettingsNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                headlineContent = {
                    Text("Выйти из аккаунта", color = MaterialTheme.colorScheme.error)
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }

    if (showLastSeen) {
        SettingsChoiceDialog(
            title = "Кто видит время захода",
            options = lastSeenLabels,
            selected = settings.lastSeen.value.coerceIn(0, 2),
            onSelect = { settings.lastSeen.set(it) },
            onDismiss = { showLastSeen = false }
        )
    }

    if (showClearCache) {
        AlertDialog(
            onDismissRequest = { showClearCache = false },
            icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
            title = { Text("Очистить кэш?") },
            text = {
                Text("Временные файлы (например, миниатюры) будут удалены. Аккаунт и настройки не изменятся.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCache = false
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                context.cacheDir.listFiles()?.forEach { it.deleteRecursively() }
                            }
                            cacheTick += 1
                            snackbar.showSnackbar("Кэш очищен")
                        }
                    }
                ) { Text("Очистить") }
            },
            dismissButton = {
                TextButton(onClick = { showClearCache = false }) { Text("Отмена") }
            }
        )
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            title = { Text("PIXEL CHAT") },
            text = {
                Text(
                    if (versionName.isBlank()) {
                        "Мессенджер на Kotlin, Jetpack Compose и Material 3."
                    } else {
                        "Версия $versionName\nМессенджер на Kotlin, Jetpack Compose и Material 3."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) { Text("Понятно") }
            }
        )
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.PowerSettingsNew,
                    contentDescription = null
                )
            },
            title = { Text("Выйти из аккаунта?") },
            text = { Text("Сессия на этом устройстве будет удалена.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogout = false
                        onLogout()
                    }
                ) {
                    Text("Выйти", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) { Text("Отмена") }
            }
        )
    }
}

private val lastSeenLabels = listOf("Все", "Мои контакты", "Никто")

/* ------------------------------------------------------------------ */
/*  Building blocks                                                   */
/* ------------------------------------------------------------------ */

@Composable
private fun SettingsHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onCheckedChange
        ),
        leadingContent = { Icon(icon, contentDescription = null) },
        headlineContent = { Text(title) },
        supportingContent = if (subtitle != null) ({ Text(subtitle) }) else null,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                thumbContent = if (checked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { Icon(icon, contentDescription = null) },
        headlineContent = { Text(title) },
        supportingContent = if (subtitle != null) ({ Text(subtitle) }) else null,
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun SettingsChoiceDialog(
    title: String,
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.selectableGroup()) {
                options.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = index == selected,
                                role = Role.RadioButton,
                                onClick = {
                                    onSelect(index)
                                    onDismiss()
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = index == selected, onClick = null)
                        Spacer(Modifier.width(16.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

private fun dirSize(dir: File): Long =
    dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
