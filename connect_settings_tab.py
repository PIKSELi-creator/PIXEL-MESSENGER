from pathlib import Path
import shutil
import re

FILE = Path(
    "android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt"
)

OLD = '''                2 -> CallsTab()
                else -> ProfileTab(onSettings = { showSettings = true })'''

NEW = '''                2 -> SettingsTab(onLogout = onLogout)
                else -> ProfileTab(onSettings = { showSettings = true })'''

SETTINGS_TAB = r'''
@Composable
private fun SettingsTab(
    onLogout: () -> Unit
) {
    SettingsScreen(
        onBack = { },
        onLogout = onLogout
    )
}
'''

def find_function_end(text, start):
    brace = text.find("{", start)
    if brace == -1:
        raise RuntimeError("Не найдено начало функции.")

    depth = 0
    in_string = False
    in_char = False
    escape = False
    line_comment = False
    block_comment = False

    i = brace

    while i < len(text):
        ch = text[i]
        nxt = text[i + 1] if i + 1 < len(text) else ""

        if line_comment:
            if ch == "\n":
                line_comment = False
            i += 1
            continue

        if block_comment:
            if ch == "*" and nxt == "/":
                block_comment = False
                i += 2
                continue
            i += 1
            continue

        if in_string:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_string = False
            i += 1
            continue

        if in_char:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == "'":
                in_char = False
            i += 1
            continue

        if ch == "/" and nxt == "/":
            line_comment = True
            i += 2
            continue

        if ch == "/" and nxt == "*":
            block_comment = True
            i += 2
            continue

        if ch == '"':
            in_string = True
            i += 1
            continue

        if ch == "'":
            in_char = True
            i += 1
            continue

        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return i + 1

        i += 1

    raise RuntimeError("Не найден конец функции.")


def main():
    if not FILE.exists():
        raise FileNotFoundError(FILE)

    text = FILE.read_text(encoding="utf-8")

    if OLD not in text:
        raise RuntimeError(
            "Не найден старый участок HomeScreen с CallsTab(). "
            "Возможно, он уже был заменён."
        )

    backup = FILE.with_suffix(
        FILE.suffix + ".before_settings_tab"
    )

    shutil.copy2(FILE, backup)

    text = text.replace(OLD, NEW, 1)

    # Если SettingsTab уже существует — не добавляем дубликат.
    if "private fun SettingsTab(" not in text:
        # Вставляем перед PixelTelegramNavigation.
        match = re.search(
            r"(?m)^\s*@Composable\s*$\n\s*private fun PixelTelegramNavigation\s*\(",
            text
        )

        if not match:
            raise RuntimeError(
                "Не найдена PixelTelegramNavigation для вставки SettingsTab."
            )

        insert_at = match.start()

        text = (
            text[:insert_at]
            + SETTINGS_TAB.strip()
            + "\n\n"
            + text[insert_at:]
        )

    FILE.write_text(text, encoding="utf-8")

    print("Готово.")
    print()
    print("Теперь навигация:")
    print("  0 -> Чаты")
    print("  1 -> Контакты")
    print("  2 -> Настройки")
    print("  3 -> Профиль")
    print()
    print(f"Файл: {FILE}")
    print(f"Резервная копия: {backup}")


if __name__ == "__main__":
    main()

