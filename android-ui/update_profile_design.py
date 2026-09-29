from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

# Добавляем нужные импорты
imports = [
    "import androidx.compose.animation.AnimatedVisibility",
    "import androidx.compose.animation.fadeIn",
    "import androidx.compose.animation.slideInVertically",
    "import androidx.compose.animation.core.tween",
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

start = text.find("private fun ProfileTab(")
if start == -1:
    print("Ошибка: ProfileTab не найден.")
    raise SystemExit(1)

end = text.find("\n@Composable", start + 20)
if end == -1:
    print("Ошибка: конец ProfileTab не найден.")
    raise SystemExit(1)

section = text[start:end]

old_card = '''        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PixelSurface
            ),
            shape = RoundedCornerShape(22.dp)
        ) {

            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AppAvatar(
                    initials = "P",
                    size = 70.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {

                    Text(
                        text = "Pixel User",
                        color = PixelText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "@pixeluser",
                        color = PixelBlue,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Я в PIXEL CHAT",
                        color = PixelMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }'''

new_card = '''        AnimatedVisibility(
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
                                text = "Pixel User",
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
        }'''

if old_card not in section:
    print("Ошибка: карточка профиля не найдена.")
    raise SystemExit(1)

backup = file.with_name("MainActivity.before-profile-design.py.bak")
backup.write_text(text, encoding="utf-8")

section = section.replace(old_card, new_card, 1)
text = text[:start] + section + text[end:]

file.write_text(text, encoding="utf-8")

print("Готово: дизайн профиля обновлён.")
print(f"Резервная копия: {backup.name}")
