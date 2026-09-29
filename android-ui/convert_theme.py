from pathlib import Path

path = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = path.read_text(encoding="utf-8")

# 1. Убираем OptIn именно для Expressive
text = text.replace(
    '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun PixelChatTheme()',
    '@Composable\nprivate fun PixelChatTheme()'
)

# 2. Меняем сам MaterialExpressiveTheme на обычный MaterialTheme
text = text.replace(
    '''        MaterialExpressiveTheme(
            colorScheme = scheme,
            motionScheme = MotionScheme.expressive(),
            shapes = shapes
        ) {
            PixelChatApp()
        }''',
    '''        MaterialTheme(
            colorScheme = scheme,
            shapes = shapes
        ) {
            PixelChatApp()
        }'''
)

# 3. Убираем expressive shape-поля
text = text.replace(
    '        largeIncreased = RoundedCornerShape(36.dp),\n',
    ''
)

text = text.replace(
    '        extraLargeIncreased = RoundedCornerShape(40.dp)\n',
    ''
)

# 4. Заменяем только существующие MotionScheme-вызовы
motion_replacements = {
    'MotionScheme.expressive().defaultSpatialSpec()':
        'tween(300)',
    'MotionScheme.expressive().defaultEffectsSpec()':
        'tween(250)',
    'MotionScheme.expressive().fastEffectsSpec()':
        'tween(180)',
    'MotionScheme.expressive().fastSpatialSpec()':
        'tween(180)',
}

for old, new in motion_replacements.items():
    text = text.replace(old, new)

# 5. Исправляем подпись версии
text = text.replace(
    'Material 3 Expressive • Server 10.0',
    'Material 3 • Server 10.0'
)

# 6. Полностью заменяем PixelChatTheme.
# Ищем границы именно функции, а не первого @Composable.
start = text.find('@Composable\nprivate fun PixelChatTheme()')
if start == -1:
    raise RuntimeError("PixelChatTheme не найден")

next_function = text.find('\n@Composable', start + 20)
if next_function == -1:
    raise RuntimeError("Следующая Composable-функция не найдена")

theme = '''@Composable
private fun PixelChatTheme() {
    val darkScheme = darkColorScheme(
        primary = Color.White,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF2A2A2A),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFFD0D0D0),
        onSecondary = Color.Black,
        secondaryContainer = Color(0xFF303030),
        onSecondaryContainer = Color.White,
        tertiary = Color(0xFFBDBDBD),
        onTertiary = Color.Black,
        tertiaryContainer = Color(0xFF383838),
        onTertiaryContainer = Color.White,
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF202020),
        onSurfaceVariant = Color(0xFFBDBDBD),
        surfaceContainer = Color(0xFF151515),
        surfaceContainerHigh = Color(0xFF202020),
        surfaceContainerHighest = Color(0xFF292929),
        outline = Color(0xFF666666)
    )

    val lightScheme = lightColorScheme(
        primary = Color.Black,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE5E5E5),
        onPrimaryContainer = Color.Black,
        secondary = Color(0xFF444444),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE8E8E8),
        onSecondaryContainer = Color.Black,
        tertiary = Color(0xFF555555),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE0E0E0),
        onTertiaryContainer = Color.Black,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFE8E8E8),
        onSurfaceVariant = Color(0xFF555555),
        surfaceContainer = Color(0xFFF4F4F4),
        surfaceContainerHigh = Color(0xFFECECEC),
        surfaceContainerHighest = Color(0xFFE2E2E2),
        outline = Color(0xFF777777)
    )

    val isDark = isSystemInDarkTheme()

    val scheme = if (isDark) darkScheme else lightScheme

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(18.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(32.dp)
    )

    MaterialTheme(
        colorScheme = scheme,
        shapes = shapes
    ) {
        PixelChatApp()
    }
}
'''

text = text[:start] + theme + text[next_function:]

path.write_text(text, encoding="utf-8")
print("Safe Material 3 theme update completed.")

