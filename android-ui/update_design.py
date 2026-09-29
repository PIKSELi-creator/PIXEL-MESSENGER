from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")

text = file.read_text(encoding="utf-8")

start = text.index("@Composable\nprivate fun ChatsTab(")
end = text.index("@Composable\nprivate fun ContactsTab(", start)

new_chats_tab = '''@Composable
private fun ChatsTab(
    modifier: Modifier,
    chats: List<ChatItem>,
    onOpen: (ChatItem) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filtered = chats.filter {
        it.name.contains(query, ignoreCase = true) ||
        it.message.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Чаты",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (filtered.isEmpty()) {
                        "Ничего не найдено"
                    } else {
                        "${filtered.size} диалога"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "P",
                    modifier = Modifier.padding(
                        horizontal = 15.dp,
                        vertical = 11.dp
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text("Поиск чатов")
            },
            leadingIcon = {
                Text(
                    text = "⌕",
                    fontSize = 23.sp
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    Text(
                        text = "×",
                        modifier = Modifier.clickable {
                            query = ""
                        },
                        fontSize = 24.sp
                    )
                }
            },
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filtered.isEmpty()) {

            EmptyState(
                title = "Ничего не найдено",
                subtitle = "Попробуйте другой запрос"
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                items(filtered) { chat ->

                    ChatRow(
                        chat = chat,
                        onClick = {
                            onOpen(chat)
                        }
                    )
                }
            }
        }
    }
}

'''

backup = file.with_name("MainActivity.before-expressive.py.bak")
backup.write_text(text, encoding="utf-8")

file.write_text(
    text[:start] + new_chats_tab + text[end:],
    encoding="utf-8"
)

print("Готово: ChatsTab обновлён.")
print(f"Резервная копия: {backup.name}")
