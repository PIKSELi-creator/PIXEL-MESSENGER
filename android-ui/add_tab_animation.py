from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

# Добавляем импорт AnimatedContent
if "import androidx.compose.animation.AnimatedContent" not in text:
    marker = "import androidx.compose.animation.AnimatedVisibility"
    text = text.replace(
        marker,
        marker + "\nimport androidx.compose.animation.AnimatedContent",
        1
    )

old_start = "        when (tab) {"
old_end = """            else -> SettingsTab(
                modifier = Modifier.padding(innerPadding)
            )
        }"""

old = old_start + """

            0 -> AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(350)
                ) + slideInVertically(
                    animationSpec = tween(350),
                    initialOffsetY = { it / 18 }
                )
            ) {
                ChatsTab(
                    modifier = Modifier.padding(innerPadding),
                    chats = chats,
                    onOpen = {
                        openedChat = it
                    }
                )
            }

            1 -> ContactsTab(
                modifier = Modifier.padding(innerPadding),
                onOpen = {
                    openedChat = it
                }
            )

            2 -> ProfileTab(
                modifier = Modifier.padding(innerPadding)
            )

            else -> SettingsTab(
                modifier = Modifier.padding(innerPadding)
            )
""" + old_end.split("\n", 1)[1]

# Более надёжно берём реальный диапазон
start = text.find(old_start)
if start == -1:
    print("Ошибка: when (tab) не найден.")
    raise SystemExit(1)

end_marker = """        }
    }
}"""

end = text.find(end_marker, start)
if end == -1:
    print("Ошибка: конец блока when (tab) не найден.")
    raise SystemExit(1)

end += len("        }")

old_block = text[start:end]

new_block = """        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(220)
                ) togetherWith fadeOut(
                    animationSpec = tween(160)
                )
            },
            label = "tab_transition"
        ) { currentTab ->

            when (currentTab) {

                0 -> ChatsTab(
                    modifier = Modifier.padding(innerPadding),
                    chats = chats,
                    onOpen = {
                        openedChat = it
                    }
                )

                1 -> ContactsTab(
                    modifier = Modifier.padding(innerPadding),
                    onOpen = {
                        openedChat = it
                    }
                )

                2 -> ProfileTab(
                    modifier = Modifier.padding(innerPadding)
                )

                else -> SettingsTab(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }"""

backup = file.with_name("MainActivity.before-tab-animation.py.bak")
backup.write_text(text, encoding="utf-8")

text = text[:start] + new_block + text[end:]

file.write_text(text, encoding="utf-8")

print("Готово: анимация переключения вкладок добавлена.")
print(f"Резервная копия: {backup.name}")
