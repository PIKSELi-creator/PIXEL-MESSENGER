from pathlib import Path

path = Path("android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt")

text = path.read_text(encoding="utf-8")

old = '"Скоро подключим настоящий сервер."'
new = '"Как дела?"'

if old not in text:
    print("Текст не найден — возможно, он уже заменён.")
else:
    text = text.replace(old, new, 1)
    path.write_text(text, encoding="utf-8")
    print("Готово: технический текст заменён на обычное сообщение.")

