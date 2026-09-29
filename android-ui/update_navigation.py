from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

start = text.index("private fun PixelBottomNavigation(")
end = text.index("@OptIn(ExperimentalMaterial3Api::class)", start)

new_navigation = '''private fun PixelBottomNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        "Чаты" to "✉",
        "Контакты" to "●",
        "Профиль" to "P",
        "Настройки" to "⚙"
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp
    ) {

        items.forEachIndexed { index, item ->

            NavigationBarItem(
                selected = selected == index,

                onClick = {
                    onSelected(index)
                },

                icon = {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected == index) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        }
                    ) {
                        Text(
                            text = item.second,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 5.dp
                            ),
                            color = if (selected == index) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 20.sp
                        )
                    }
                },

                label = {
                    Text(
                        text = item.first,
                        style = MaterialTheme.typography.labelMedium
                    )
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor =
                        MaterialTheme.colorScheme.primary,
                    unselectedIconColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

'''

backup = file.with_name("MainActivity.before-navigation-expressive.py.bak")
backup.write_text(text, encoding="utf-8")

file.write_text(
    text[:start] + new_navigation + text[end:],
    encoding="utf-8"
)

print("Готово: NavigationBar обновлён.")
print(f"Резервная копия: {backup.name}")
