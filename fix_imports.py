from pathlib import Path

path = Path("android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = path.read_text()

# Восстанавливаем отдельные строки импортов
replacements = {
    "import androidx.compose.animation.scaleIn                         import androidx.compose.animation.scaleOut":
        "import androidx.compose.animation.scaleIn\nimport androidx.compose.animation.scaleOut",

    "import android.os.Bundle\nimport androidx.activity.ComponentActivity                        import androidx.activity.compose.setContent":
        "import android.os.Bundle\nimport androidx.activity.ComponentActivity\nimport androidx.activity.compose.setContent",

    "import androidx.compose.animation.AnimatedContent                 import androidx.compose.animation.AnimatedVisibility":
        "import androidx.compose.animation.AnimatedContent\nimport androidx.compose.animation.AnimatedVisibility",

    "import androidx.compose.animation.fadeIn                          import androidx.compose.animation.fadeOut":
        "import androidx.compose.animation.fadeIn\nimport androidx.compose.animation.fadeOut",

    "import androidx.compose.foundation.layout.Box                     import androidx.compose.foundation.layout.Column":
        "import androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.layout.Column",

    "import androidx.compose.foundation.layout.ColumnScope             import androidx.compose.foundation.layout.Row":
        "import androidx.compose.foundation.layout.ColumnScope\nimport androidx.compose.foundation.layout.Row",

    "import androidx.compose.foundation.layout.fillMaxWidth            import androidx.compose.foundation.layout.height":
        "import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.height",
}

for old, new in replacements.items():
    text = text.replace(old, new)

path.write_text(text)
print("Импорты исправлены.")
