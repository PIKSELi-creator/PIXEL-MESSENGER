from pathlib import Path

p = Path("android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt")
s = p.read_text()

old_fab = '''                LargeFloatingActionButton(
                    onClick = { showNewChat = true },
                    shape = MaterialTheme.shapes.largeIncreased,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }'''

new_fab = '''                SmallFloatingActionButton(
                    onClick = { showNewChat = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Создать"
                    )
                }'''

if old_fab not in s:
    raise SystemExit("[ERROR] Старый FAB не найден")

s = s.replace(old_fab, new_fab, 1)

start = s.index("@Composable\nprivate fun PixelChatNavigation(")
end = s.index("\n@Composable\nprivate fun ChatsTab(", start)

new_navigation = '''@Composable
private fun PixelChatNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        "Чаты" to Icons.Outlined.Chat,
        "Контакты" to Icons.Outlined.Contacts,
        "Настройки" to Icons.Outlined.Settings,
        "Профиль" to Icons.Outlined.Person
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val active = selected == index

                Surface(
                    onClick = {
                        if (selected != index) {
                            onSelected(index)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedContent(
                            targetState = active,
                            transitionSpec = {
                                (fadeIn(tween(160)) + scaleIn(tween(160))) togetherWith
                                    (fadeOut(tween(100)) + scaleOut(tween(100)))
                            },
                            label = "nav_icon_$index"
                        ) { isActive ->
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(
                                    if (isActive) 25.dp else 23.dp
                                ),
                                tint = if (isActive) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = item.first,
                            color = if (active) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (active) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            }
                        )
                    }
                }
            }
        }
    }
}'''

s = s[:start] + new_navigation + s[end:]

p.write_text(s)
print("[OK] Нижняя навигация обновлена")
print("[OK] FAB заменён на маленькую круглую кнопку")
