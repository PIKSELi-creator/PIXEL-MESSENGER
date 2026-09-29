from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

# Импорты
imports = [
    "import androidx.compose.foundation.interaction.MutableInteractionSource",
    "import androidx.compose.foundation.interaction.collectIsPressedAsState",
    "import androidx.compose.ui.graphics.graphicsLayer",
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

# Находим ChatRow
start = text.find("private fun ChatRow(")
if start == -1:
    print("Ошибка: ChatRow не найден.")
    raise SystemExit(1)

end = text.find("\n@Composable", start)
if end == -1:
    print("Ошибка: конец ChatRow не найден.")
    raise SystemExit(1)

section = text[start:end]

# Добавляем состояние нажатия
old_header = '''private fun ChatRow(
    chat: ChatItem,
    onClick: () -> Unit
) {

    Column {'''

new_header = '''private fun ChatRow(
    chat: ChatItem,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    val rowScale = if (pressed) 0.985f else 1f

    Column {'''

if old_header not in section:
    print("Ошибка: начало ChatRow отличается.")
    raise SystemExit(1)

section = section.replace(old_header, new_header, 1)

# Меняем modifier реального Row
old_modifier = '''            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding('''

new_modifier = '''            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = rowScale,
                    scaleY = rowScale
                )
                .clip(RoundedCornerShape(18.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding('''

if old_modifier not in section:
    print("Ошибка: modifier Row не найден.")
    raise SystemExit(1)

section = section.replace(old_modifier, new_modifier, 1)

backup = file.with_name("MainActivity.before-chat-press.py.bak")
backup.write_text(text, encoding="utf-8")

text = text[:start] + section + text[end:]

file.write_text(text, encoding="utf-8")

print("Готово: press-анимация ChatRow добавлена.")
print(f"Резервная копия: {backup.name}")
