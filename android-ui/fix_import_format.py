from pathlib import Path

p = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = p.read_text()

# Разделяем склеившиеся import'ы
imports = [
    "import androidx.compose.material3.ButtonDefaults",
    "import androidx.compose.material3.Card",
    "import androidx.compose.material3.CardDefaults",
    "import androidx.compose.material3.CenterAlignedTopAppBar",
    "import androidx.compose.material3.ExperimentalMaterial3Api",
    "import androidx.compose.material3.FloatingActionButton",
    "import androidx.compose.material3.FloatingActionButtonDefaults",
    "import androidx.compose.material3.MaterialTheme",
    "import androidx.compose.material3.NavigationBar",
    "import androidx.compose.material.icons.Icons",
    "import androidx.compose.material.icons.outlined.Chat",
    "import androidx.compose.material.icons.outlined.Contacts",
    "import androidx.compose.material.icons.outlined.Person",
    "import androidx.compose.material.icons.outlined.Settings",
    "import androidx.compose.material3.Icon",
    "import androidx.compose.material3.NavigationBarItem",
    "import androidx.compose.material3.NavigationBarItemDefaults",
    "import androidx.compose.material3.OutlinedButton",
    "import androidx.compose.material3.OutlinedTextField",
    "import androidx.compose.material3.Scaffold",
    "import androidx.compose.material3.Surface",
    "import androidx.compose.runtime.saveable.rememberSaveable",
    "import androidx.compose.runtime.setValue",
]

for imp in imports:
    text = text.replace(imp, "\n" + imp)

# Убираем случайные пустые строки перед import'ами
lines = text.splitlines()
out = []
for line in lines:
    if line.startswith("import "):
        line = line.strip()
    out.append(line)

p.write_text("\n".join(out) + "\n")
print("Импорты исправлены.")
