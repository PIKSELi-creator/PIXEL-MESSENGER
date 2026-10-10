package com.pixelchat.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private data class TabSpec(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private val tabs = listOf(
    TabSpec("Чаты", Icons.Outlined.Chat),
    TabSpec("Контакты", Icons.Outlined.People),
    TabSpec("Звонки", Icons.Outlined.Call),
    TabSpec("Настройки", Icons.Outlined.Settings)
)

@Composable
fun PixelChatNavigationBar(selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier, containerColor = MaterialTheme.colorScheme.surface) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label, maxLines = 1) }
            )
        }
    }
}

@Composable
fun PixelChatSearchField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, hint: String = "Поиск") {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(hint) },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Поиск") },
        shape = MaterialTheme.shapes.large
    )
}

@Composable
fun PixelChatAvatar(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.trim().take(2).uppercase().ifBlank { "PC" },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PixelChatConversationRow(
    title: String,
    preview: String,
    time: String,
    initials: String,
    unreadCount: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PixelChatAvatar(initials)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(preview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (unreadCount > 0) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                    Text(unreadCount.coerceAtMost(99).toString(), modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun PixelChatMessageBubble(text: String, mine: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Surface(
            color = if (mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (mine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            shape = MaterialTheme.shapes.large
        ) {
            Text(text = text, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun PixelChatComposer(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(modifier = modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            singleLine = false,
            placeholder = { Text("Сообщение…") },
            shape = MaterialTheme.shapes.large
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onSend, enabled = enabled && text.isNotBlank()) {
            Icon(Icons.Outlined.Send, contentDescription = "Отправить", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PixelChatCreateButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.FloatingActionButton(onClick = onClick, modifier = modifier) {
        Icon(Icons.Outlined.Add, contentDescription = "Создать")
    }
}
