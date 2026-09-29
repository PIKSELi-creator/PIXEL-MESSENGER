from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

if "import androidx.compose.animation.AnimatedVisibility" not in text:
    print("Ошибка: AnimatedVisibility не найден.")
    raise SystemExit(1)

old = '''            if (tab == 0 || tab == 1) {

                FloatingActionButton(
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
            }'''

new = '''            AnimatedVisibility(
                visible = tab == 0 || tab == 1,
                enter = fadeIn(
                    animationSpec = tween(250)
                ) + slideInVertically(
                    animationSpec = tween(250),
                    initialOffsetY = { it / 2 }
                ),
                exit = fadeOut(
                    animationSpec = tween(150)
                ) + slideOutVertically(
                    animationSpec = tween(150),
                    targetOffsetY = { it / 2 }
                )
            ) {

                FloatingActionButton(
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
            }'''

if old not in text:
    print("Ошибка: главный FAB не найден.")
    raise SystemExit(1)

backup = file.with_name("MainActivity.before-fab-animation.py.bak")
backup.write_text(text, encoding="utf-8")

text = text.replace(old, new, 1)

file.write_text(text, encoding="utf-8")

print("Готово: анимация главного FAB добавлена.")
print(f"Резервная копия: {backup.name}")
