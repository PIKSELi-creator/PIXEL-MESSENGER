from pathlib import Path

path = Path("android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt")
text = path.read_text(encoding="utf-8")

old = 'Text("◎", modifier = Modifier.padding(12.dp), color = PixelBlueBright, fontSize = 19.sp)'

new = '''Icon(
    imageVector = Icons.Outlined.Person,
    contentDescription = "Контакт",
    modifier = Modifier.padding(12.dp),
    tint = PixelBlueBright
)'''

if old not in text:
    print("Старый значок ◎ не найден.")
    raise SystemExit(0)

backup = path.with_name(path.name + ".before_remove_old_contact_icon")
backup.write_text(text, encoding="utf-8")

text = text.replace(old, new, 1)
path.write_text(text, encoding="utf-8")

print("Готово: старый символ ◎ заменён на Material Icon.")
print(f"Бэкап: {backup}")
