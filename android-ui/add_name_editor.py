from pathlib import Path

file = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = file.read_text(encoding="utf-8")

text = text.replace(
    'var showNewChat by remember { mutableStateOf(false) }',
    'var showNewChat by remember { mutableStateOf(false) }'
)

marker = '@Composable\nprivate fun ProfileAction('

editor = '''@Composable
private fun NameEditorScreen(
    currentName: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PixelBackground)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                modifier = Modifier.clickable { onBack() },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 36.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Имя",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Имя")
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onSave(name.trim())
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Сохранить")
        }
    }
}

'''

if 'private fun NameEditorScreen(' not in text:
    text = text.replace(marker, editor + marker, 1)

file.write_text(text, encoding="utf-8")
print("Готово: редактор имени добавлен.")
