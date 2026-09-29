from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

old = '''                FloatingActionButton(
                    onClick = {
                        showNewChat = true
                    },
                    containerColor = PixelBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {

                    Text(
                        text = "+",
                        fontSize = 28.sp
                    )
                }
'''

new = '''                FloatingActionButton(
                    onClick = {
                        showNewChat = true
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {

                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
'''

if old not in text:
    print("Ошибка: старый FAB не найден.")
    raise SystemExit(1)

backup = file.with_name("MainActivity.before-fab.py.bak")
backup.write_text(text, encoding="utf-8")

file.write_text(
    text.replace(old, new, 1),
    encoding="utf-8"
)

print("Готово: FAB обновлён.")
print(f"Резервная копия: {backup.name}")
