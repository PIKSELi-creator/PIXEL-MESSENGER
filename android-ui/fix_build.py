from pathlib import Path

path = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = path.read_text(encoding="utf-8")

imports = [
    "import androidx.compose.animation.slideOutVertically",
    "import androidx.compose.animation.togetherWith",
    "import androidx.compose.foundation.layout.PaddingValues",
    "import androidx.compose.material3.FloatingActionButtonDefaults",
    "import androidx.compose.material3.NavigationBarItemDefaults",
]

anchor = "import androidx.compose.animation.slideInVertically\n"

for imp in imports:
    if imp not in text:
        if anchor not in text:
            raise SystemExit(f"Не найден импорт-якорь: {anchor}")
        text = text.replace(
            anchor,
            anchor + imp + "\n",
            1
        )

name_editor = r'''
@Composable
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
            label = { Text("Имя") },
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

if "private fun NameEditorScreen(" not in text:
    marker = "@Composable\nprivate fun ProfileAction("
    if marker not in text:
        raise SystemExit("Не найдено место для NameEditorScreen")
    text = text.replace(marker, name_editor + marker, 1)

path.write_text(text, encoding="utf-8")

print("Готово: исправлены импорты и добавлен NameEditorScreen.")
