from pathlib import Path
import re
import shutil

FILE = Path(
    "android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt"
)

NEW_PROFILE = r'''
@Composable
private fun ProfileTab(
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(10.dp))

        Text(
            text = "Профиль",
            color = PixelText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelSurface,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppAvatar(
                        initials = "P",
                        size = 86.dp
                    )

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pixel User",
                            color = PixelText,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "@pixeluser",
                            color = PixelBlueBright,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(5.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(8.dp),
                                shape = CircleShape,
                                color = PixelSuccess
                            ) {}

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = "в сети",
                                color = PixelSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                ProfileInfoRow(
                    title = "Gmail",
                    subtitle = "Подтверждённый аккаунт"
                )

                ProfileInfoRow(
                    title = "О себе",
                    subtitle = "Я в PIXEL CHAT"
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelBlueContainer,
            shape = RoundedCornerShape(22.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = PixelBlue
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "★",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Подарки",
                        color = PixelText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = "Пиксельные подарки • звёзды • кристаллы",
                        color = PixelMuted,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "›",
                    color = PixelBlueBright,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        ProfileAction(
            title = "Изменить профиль",
            subtitle = "Имя, username и аватар"
        )

        ProfileAction(
            title = "QR-профиль",
            subtitle = "Поделиться своим профилем"
        )

        ProfileAction(
            title = "Активные сессии",
            subtitle = "Устройства, где открыт аккаунт"
        )

        ProfileAction(
            title = "Безопасность",
            subtitle = "Шифрование и защита аккаунта"
        )
    }
}
'''

def find_function_end(text: str, start: int) -> int:
    brace_start = text.find("{", start)

    if brace_start == -1:
        raise RuntimeError("Не найдено начало функции ProfileTab.")

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

    raise RuntimeError("Не найден конец функции ProfileTab.")


def main():
    if not FILE.exists():
        raise FileNotFoundError(
            f"Файл не найден: {FILE}"
        )

    text = FILE.read_text(encoding="utf-8")

    match = re.search(
        r"(?m)^\s*@Composable\s*$\n\s*private fun ProfileTab\s*\(",
        text
    )

    if not match:
        raise RuntimeError(
            "ProfileTab не найдена."
        )

    start = match.start()
    end = find_function_end(text, match.start())

    backup = FILE.with_suffix(
        FILE.suffix + ".before_profile"
    )

    shutil.copy2(FILE, backup)

    updated = (
        text[:start]
        + NEW_PROFILE.strip()
        + "\n\n"
        + text[end:]
    )

    FILE.write_text(
        updated,
        encoding="utf-8"
    )

    print("Готово.")
    print()
    print("ProfileTab обновлена.")
    print("Кнопка «Настройки» сверху убрана.")
    print("Добавлены подарки, безопасность и сессии.")
    print()
    print(f"Файл: {FILE}")
    print(f"Резервная копия: {backup}")


if __name__ == "__main__":
    main()
