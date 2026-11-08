package com.pixelchat.ui.settings

import com.pixelchat.ui.R

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Все переходы с экрана настроек. Навигацию подключаешь снаружи. */
data class SettingsActions(
    val onThemes: () -> Unit,
    val onCustomize: () -> Unit,
    val onLanguage: () -> Unit,
    val onFavorites: () -> Unit,
    val onStories: () -> Unit,
    val onFolders: () -> Unit,
    val onPrivacy: () -> Unit,
    val onDevices: () -> Unit,
    val onNotifications: () -> Unit,
    val onPowerSaving: () -> Unit,
    val onUpdates: () -> Unit,
    val onProfile: () -> Unit,
    val onReorder: () -> Unit,
    val onAddAccount: () -> Unit,
    val onLogout: () -> Unit,
    val onDeleteAccount: () -> Unit,
)

private data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val showChevron: Boolean = true,
    val danger: Boolean = false,
)

private data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>,
)

private fun buildSections(a: SettingsActions): List<SettingsSection> = listOf(
    SettingsSection(
        "Оформление и язык",
        listOf(
            SettingsItem("Темы", "Оформление и цвет переписки", Icons.Filled.Create, a.onThemes),
            SettingsItem("Изменить", "Аватарки, фон чата, эмодзи, реакции", Icons.Filled.Face, a.onCustomize),
            SettingsItem("Язык", "Язык приложения и свои локализации", Icons.Filled.Place, a.onLanguage),
        ),
    ),
    SettingsSection(
        "Моё",
        listOf(
            SettingsItem("Избранное", "Заметки и файлы", Icons.Filled.Favorite, a.onFavorites),
            SettingsItem("Истории", "Ваши и тех, кто в контактах", Icons.Filled.Star, a.onStories),
            SettingsItem("Папки с чатами", "Свои вкладки над списком чатов", Icons.Filled.List, a.onFolders),
        ),
    ),
    SettingsSection(
        "Безопасность",
        listOf(
            SettingsItem("Конфиденциальность", "Чёрный список и кто вас видит", Icons.Filled.Lock, a.onPrivacy),
            SettingsItem("Устройства", "Где выполнен вход в аккаунт", Icons.Filled.Phone, a.onDevices),
            SettingsItem("Уведомления", "Сигнал на отправку и на входящее", Icons.Filled.Notifications, a.onNotifications),
        ),
    ),
    SettingsSection(
        "Телефон",
        listOf(
            SettingsItem("Энергосбережение", "Анимации и фоновые эффекты", Icons.Filled.Build, a.onPowerSaving),
            SettingsItem("Обновления", "Новые версии приложения", Icons.Filled.Refresh, a.onUpdates),
        ),
    ),
    SettingsSection(
        "Профиль",
        listOf(
            SettingsItem("Профиль", "Аватар, имя, юзернейм, о себе", Icons.Filled.Person, a.onProfile),
        ),
    ),
    SettingsSection(
        "Список настроек",
        listOf(
            SettingsItem("Порядок разделов", "Переставить разделы и категории", Icons.Filled.Info, a.onReorder),
        ),
    ),
)

@Composable
fun SettingsScreen(
    username: String,
    accountCount: Int,
    actions: SettingsActions,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var accountsExpanded by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val searchFocus = remember { FocusRequester() }

    val sections = remember(actions) { buildSections(actions) }
    val accountItems = remember(actions) {
        listOf(
            SettingsItem(
                "Выйти из аккаунта", "Войти снова можно в любой момент",
                Icons.AutoMirrored.Filled.ExitToApp, actions.onLogout, showChevron = false,
            ),
            SettingsItem(
                "Удалить аккаунт", "Навсегда, вместе с перепиской",
                Icons.Filled.Delete, { showDeleteDialog = true },
                showChevron = false, danger = true,
            ),
        )
    }

    val searching = query.isNotBlank()
    val found = remember(query, sections, accountItems) {
        (sections.flatMap { it.items } + accountItems).filter {
            it.title.contains(query, ignoreCase = true) ||
                it.subtitle.contains(query, ignoreCase = true)
        }
    }

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(searchFocus),
                    placeholder = { Text("Найти настройки $username") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searching) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Очистить")
                            }
                        }
                    },
                    singleLine = true,
                    shape = CircleShape,
                )
            }

            if (searching) {
                if (found.isEmpty()) {
                    item {
                        Text(
                            "Ничего не найдено",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(found.size) { SettingsRow(found[it]) }
                }
            } else {
                item { ProfileCard(username = username, onClick = actions.onProfile) }
                item {
                    AccountsCard(
                        username = username,
                        count = accountCount,
                        expanded = accountsExpanded,
                        onToggle = { accountsExpanded = !accountsExpanded },
                        onAdd = actions.onAddAccount,
                    )
                }
                sections.forEach { section ->
                    item { SectionTitle(section.title) }
                    items(section.items.size) { SettingsRow(section.items[it]) }
                }
                item { SectionTitle("Аккаунт") }
                items(accountItems.size) { SettingsRow(accountItems[it]) }
            }
        }

        // Нижние кнопки: настройки (текущий экран) и поиск
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledTonalIconButton(onClick = { query = "" }, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Filled.Settings, contentDescription = "Настройки")
            }
            FilledTonalIconButton(
                onClick = { searchFocus.requestFocus() },
                modifier = Modifier.size(56.dp),
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Поиск")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить аккаунт?") },
            text = { Text("Аккаунт и вся переписка будут удалены навсегда. Это нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    actions.onDeleteAccount()
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 2.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ProfileCard(username: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
        modifier = Modifier.fillMaxWidth().clip(shape).clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.pixel_bubble),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(CircleShape),
            )
            Spacer(Modifier.size(16.dp))
            Column {
                Text(
                    "PIXEL CHAT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Аккаунт $username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AccountsCard(
    username: String,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAdd: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
        modifier = Modifier.fillMaxWidth().clip(shape),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.AccountBox, contentDescription = null)
                Spacer(Modifier.size(16.dp))
                Text(
                    "Аккаунт ($count)",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть",
                    modifier = Modifier.rotate(if (expanded) 180f else 0f),
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.size(16.dp))
                        Text(username, style = MaterialTheme.typography.bodyLarge)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onAdd)
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(Modifier.size(16.dp))
                        Text("Добавить аккаунт", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(item: SettingsItem) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)
    Surface(
        color = scheme.surfaceContainer,
        shape = shape,
        modifier = Modifier.fillMaxWidth().clip(shape).clickable(onClick = item.onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (item.danger) scheme.errorContainer else scheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    item.icon,
                    contentDescription = null,
                    tint = if (item.danger) scheme.onErrorContainer else scheme.onSecondaryContainer,
                )
            }
            Spacer(Modifier.size(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.danger) scheme.error else scheme.onSurface,
                )
                Text(
                    item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (item.showChevron) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = scheme.onSurfaceVariant,
                )
            }
        }
    }
}
