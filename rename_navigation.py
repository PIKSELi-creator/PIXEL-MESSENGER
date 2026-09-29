from pathlib import Path

path = Path("android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt")

text = path.read_text(encoding="utf-8")

text = text.replace("PixelTelegramNavigation", "PixelChatNavigation")

path.write_text(text, encoding="utf-8")

print("Готово: PixelTelegramNavigation → PixelChatNavigation")
