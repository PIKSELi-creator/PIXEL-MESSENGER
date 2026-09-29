from pathlib import Path

path = Path("app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = path.read_text(encoding="utf-8")

old = """private val PixelBlue = Color(0xFF4D9FFF)
private val PixelBlueBright = Color(0xFF69B5FF)
private val PixelBlueContainer = Color(0xFF183B59)

private val PixelBackground = Color(0xFF071018)
private val PixelSurface = Color(0xFF101B25)
private val PixelSurfaceHigh = Color(0xFF172532)
private val PixelOutline = Color(0xFF425565)

private val PixelText = Color(0xFFF4F8FC)
private val PixelMuted = Color(0xFF97A9B9)
private val PixelSuccess = Color(0xFF55D6A7)
private val PixelDanger = Color(0xFFFF6D7A)
"""

new = """private val PixelBlue = Color(0xFF000000)
private val PixelBlueBright = Color(0xFFFFFFFF)
private val PixelBlueContainer = Color(0xFF2A2A2A)

private val PixelBackground = Color(0xFF000000)
private val PixelSurface = Color(0xFF151515)
private val PixelSurfaceHigh = Color(0xFF202020)
private val PixelOutline = Color(0xFF666666)

private val PixelText = Color(0xFFFFFFFF)
private val PixelMuted = Color(0xFFAAAAAA)
private val PixelSuccess = Color(0xFFFFFFFF)
private val PixelDanger = Color(0xFFFFFFFF)
"""

if old not in text:
    raise RuntimeError("Старая палитра не найдена — файл не изменён.")

text = text.replace(old, new, 1)

path.write_text(text, encoding="utf-8")
print("Monochrome palette applied.")
