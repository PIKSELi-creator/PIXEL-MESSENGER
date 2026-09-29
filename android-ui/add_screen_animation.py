from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

# Импорт tween
if "import androidx.compose.animation.core.tween" not in text:
    marker = "import androidx.compose.animation.slideInVertically"
    if marker not in text:
        print("Ошибка: импорт slideInVertically не найден.")
        raise SystemExit(1)

    text = text.replace(
        marker,
        marker + "\nimport androidx.compose.animation.core.tween",
        1
    )

old = '''            0 -> ChatsTab(
                modifier = Modifier.padding(innerPadding),
                chats = chats,
                onOpen = {
                    openedChat = it
                }
            )'''

new = '''            0 -> AnimatedVisibility(
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
            }'''

if old not in text:
    print("Ошибка: точный блок ChatsTab не найден.")
    raise SystemExit(1)

backup = file.with_name("MainActivity.before-screen-animation.py.bak")
backup.write_text(text, encoding="utf-8")

text = text.replace(old, new, 1)

file.write_text(text, encoding="utf-8")

print("Готово: анимация экрана Чаты добавлена.")
print(f"Резервная копия: {backup.name}")
