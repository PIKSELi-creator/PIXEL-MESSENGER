from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

start = text.find("@Composable\nprivate fun ProfileTab(")
if start == -1:
    print("Ошибка: ProfileTab не найден.")
    raise SystemExit(1)

# Ищем следующую функцию после ProfileTab
end = text.find("\n@Composable\nprivate fun ProfileAction(", start)

if end == -1:
    print("Ошибка: конец ProfileTab не найден.")
    raise SystemExit(1)

backup = file.with_name("MainActivity.before-name-editor-connect.bak")
backup.write_text(text, encoding="utf-8")

new_profile = '''@Composable
private fun ProfileTab(
    modifier: Modifier
) {
    var showNameEditor by remember { mutableStateOf(false) }
    var profileName by remember { mutableStateOf("Pixel User") }

    if (showNameEditor) {
        NameEditorScreen(
            currentName = profileName,
            onBack = {
                showNameEditor = false
            },
            onSave = { newName ->
                profileName = newName
                showNameEditor = false
            }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Профиль",
            color = PixelText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                animationSpec = tween(350)
            ) + slideInVertically(
                animationSpec = tween(350),
                initialOffsetY = { it / 10 }
            )
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(28.dp)
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AppAvatar(
                            initials = "P",
                            size = 82.dp
                        )

                        Spacer(modifier = Modifier.width(18.dp))

                        Column {

                            Text(
                                text = profileName,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "@pixeluser",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Я в PIXEL CHAT",
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = 0.72f
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileAction(
            title = "Изменить профиль",
            subtitle = "Имя, username и аватар",
            onClick = {
                showNameEditor = true
            }
        )

        ProfileAction(
            title = "QR-профиль",
            subtitle = "Быстрый обмен профилями"
        )

        ProfileAction(
            title = "Активные сессии",
            subtitle = "Устройства, где открыт аккаунт"
        )
    }
}
'''

text = text[:start] + new_profile + text[end:]

file.write_text(text, encoding="utf-8")

print("Готово: ProfileTab подключён к редактору имени.")
print(f"Резервная копия: {backup.name}")
