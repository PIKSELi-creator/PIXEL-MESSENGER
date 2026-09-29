from pathlib import Path

gradle = Path("android-ui/app/build.gradle.kts")

text = gradle.read_text(encoding="utf-8")

target = 'implementation("androidx.compose.material3:material3:1.5.0-alpha29")'
addition = 'implementation("androidx.compose.material:material-icons-extended:1.7.8")'

if addition in text:
    print("Material Icons уже добавлены.")
elif target not in text:
    print("Ошибка: строка Material 3 не найдена.")
    raise SystemExit(1)
else:
    text = text.replace(
        target,
        target + "\n    " + addition
    )
    gradle.write_text(text, encoding="utf-8")
    print("Готово: Material Icons Extended добавлены.")

print("\nПроверка:")
for line in gradle.read_text(encoding="utf-8").splitlines():
    if "material-icons-extended" in line:
        print(line)
