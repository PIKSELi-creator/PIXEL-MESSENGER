from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

# Добавляем импорты
imports = [
    "import androidx.compose.animation.AnimatedVisibility",
    "import androidx.compose.animation.fadeIn",
    "import androidx.compose.animation.fadeOut",
    "import androidx.compose.animation.slideInVertically",
]

lines = text.splitlines()

last_import = -1
for i, line in enumerate(lines):
    if line.startswith("import "):
        last_import = i

for imp in imports:
    if imp not in text:
        lines.insert(last_import + 1, imp)
        last_import += 1

text = "\n".join(lines) + "\n"

# Находим именно ChatsTab
start = text.find("private fun ChatsTab(")
if start == -1:
    print("Ошибка: ChatsTab не найден.")
    raise SystemExit(1)

end = text.find("@Composable", start + 20)
if end == -1:
    end = len(text)

section = text[start:end]

old = """items(filtered) { chat ->

                    ChatRow(
                        chat = chat,
                        onClick = {
                            onOpen(chat)
                        }
                    )
                }"""

new = """items(filtered) { chat ->

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it / 5 }
                        ),
                        exit = fadeOut()
                    ) {
                        ChatRow(
                            chat = chat,
                            onClick = {
                                onOpen(chat)
                            }
                        )
                    }
                }"""

if old not in section:
    print("Ошибка: блок items(filtered) внутри ChatsTab не найден.")
    raise SystemExit(1)

backup = file.with_name("MainActivity.before-animations.py.bak")
backup.write_text(text, encoding="utf-8")

section = section.replace(old, new, 1)
text = text[:start] + section + text[end:]

file.write_text(text, encoding="utf-8")

print("Готово: анимация чатов добавлена.")
print(f"Резервная копия: {backup.name}")
