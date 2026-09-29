from pathlib import Path
import re
import shutil

FILE = Path(
    "android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt"
)

NEW_NAVIGATION = r'''
@Composable
private fun PixelTelegramNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        Triple("Чаты", "◉", 0),
        Triple("Контакты", "◎", 1),
        Triple("Настройки", "⚙", 2),
        Triple("Профиль", "●", 3)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = PixelSurface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val title = item.first
                val icon = item.second
                val index = item.third
                val active = selected == index

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            onSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = if (active) {
                                PixelBlueContainer
                            } else {
                                Color.Transparent
                            }
                        ) {
                            Text(
                                text = icon,
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 5.dp
                                ),
                                color = if (active) {
                                    PixelBlueBright
                                } else {
                                    PixelMuted
                                },
                                fontSize = 25.sp,
                                fontWeight = if (active) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        }

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = title,
                            color = if (active) {
                                PixelBlueBright
                            } else {
                                PixelMuted
                            },
                            fontSize = 11.sp,
                            fontWeight = if (active) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
'''

def find_function_end(text: str, start: int) -> int:
    brace_start = text.find("{", start)

    if brace_start == -1:
        raise RuntimeError(
            "Не найдено начало тела PixelTelegramNavigation."
        )

    depth = 0
    in_string = False
    in_char = False
    escape = False
    line_comment = False
    block_comment = False

    i = brace_start

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

    raise RuntimeError(
        "Не найден конец PixelTelegramNavigation."
    )


def main():
    if not FILE.exists():
        raise FileNotFoundError(
            f"Файл не найден:\n{FILE}"
        )

    text = FILE.read_text(encoding="utf-8")

    pattern = r"(?m)^\s*(?:private\s+)?fun\s+PixelTelegramNavigation\s*\("

    match = re.search(pattern, text)

    if not match:
        raise RuntimeError(
            "PixelTelegramNavigation не найдена."
        )

    start = match.start()
    end = find_function_end(text, match.start())

    backup = FILE.with_suffix(
        FILE.suffix + ".before_navigation"
    )

    shutil.copy2(FILE, backup)

    updated = (
        text[:start]
        + NEW_NAVIGATION.strip()
        + "\n\n"
        + text[end:]
    )

    FILE.write_text(
        updated,
        encoding="utf-8"
    )

    print("Готово.")
    print()
    print("PixelTelegramNavigation заменена.")
    print()
    print("Новая навигация:")
    print("  Чаты")
    print("  Контакты")
    print("  Настройки")
    print("  Профиль")
    print()
    print(f"Файл: {FILE}")
    print(f"Резервная копия: {backup}")


if __name__ == "__main__":
    main()
