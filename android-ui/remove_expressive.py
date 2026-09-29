from pathlib import Path

path = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = path.read_text(encoding="utf-8")

text = text.replace(
    "@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)\n",
    ""
)

text = text.replace(
    "import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi\n",
    ""
)

text = text.replace(
    "import androidx.compose.material3.MaterialExpressiveTheme\n",
    ""
)

text = text.replace(
    "import androidx.compose.material3.MotionScheme\n",
    ""
)

path.write_text(text, encoding="utf-8")
print("Expressive imports removed.")
