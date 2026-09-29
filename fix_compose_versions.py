from pathlib import Path

path = Path("android-ui/app/build.gradle.kts")

text = path.read_text(encoding="utf-8")

text = text.replace(
    'implementation("androidx.compose.ui:ui:1.12.1")',
    'implementation("androidx.compose.ui:ui:1.7.8")'
)

text = text.replace(
    'implementation("androidx.compose.ui:ui-tooling-preview:1.12.1")',
    'implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")'
)

text = text.replace(
    'implementation("androidx.compose.material3:material3:1.5.0-alpha29")',
    'implementation("androidx.compose.material3:material3:1.3.1")'
)

text = text.replace(
    'implementation("androidx.compose.material:material-icons-extended:1.7.8")',
    'implementation("androidx.compose.material:material-icons-extended:1.7.8")'
)

path.write_text(text, encoding="utf-8")

print("Compose-зависимости возвращены к совместимым версиям.")

