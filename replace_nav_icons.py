from pathlib import Path

path = Path(
    "android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt"
)

text = path.read_text(encoding="utf-8")

# Добавляем импорты Material Icons
imports = [
    "import androidx.compose.material.icons.Icons",
    "import androidx.compose.material.icons.outlined.Chat",
    "import androidx.compose.material.icons.outlined.Contacts",
    "import androidx.compose.material.icons.outlined.Person",
    "import androidx.compose.material.icons.outlined.Settings",
    "import androidx.compose.material3.Icon",
]

marker = "import androidx.compose.material3.NavigationBarItem"

for imp in imports:
    if imp not in text:
        text = text.replace(
            marker,
            imp + "\n" + marker,
            1
        )

# Меняем список нижней навигации
old = '''val items = listOf(
        "Чаты" to "◉",
        "Контакты" to "◎",
        "Настройки" to "⚙",
        "Профиль" to "●"
    )'''

new = '''val items = listOf(
        "Чаты" to Icons.Outlined.Chat,
        "Контакты" to Icons.Outlined.Contacts,
        "Настройки" to Icons.Outlined.Settings,
        "Профиль" to Icons.Outlined.Person
    )'''

if old not in text:
    print("ОШИБКА: список навигации не найден.")
    raise SystemExit(1)

text = text.replace(old, new, 1)

# Заменяем Text с символом иконки на настоящий Icon
old_icon = '''Text(
                                text = item.second,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 21.sp
                            )'''

new_icon = '''Icon(
                                imageVector = item.second,
                                contentDescription = item.first,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )'''

if old_icon not in text:
    print("ОШИБКА: блок иконки не найден.")
    raise SystemExit(1)

text = text.replace(old_icon, new_icon, 1)

backup = path.with_name(path.name + ".before_nav_icons")
if not backup.exists():
    backup.write_text(path.read_text(encoding="utf-8"), encoding="utf-8")

path.write_text(text, encoding="utf-8")

print("Готово: нижняя навигация переведена на Material Icons.")
print()
print("Чаты       -> Icons.Outlined.Chat")
print("Контакты   -> Icons.Outlined.Contacts")
print("Настройки  -> Icons.Outlined.Settings")
print("Профиль    -> Icons.Outlined.Person")
print()
print(f"Бэкап: {backup}")
