from pathlib import Path
import re
import shutil

FILE = Path(
    "android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt"
)

NEW_CHAT_SCREEN = r'''
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(
    chat: ChatItem,
    onBack: () -> Unit
) {
    var input by rememberSaveable { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            "Привет! Это PIXEL CHAT.",
            "Дизайн уже обновили.",
            "Скоро подключим настоящий сервер."
        )
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = PixelBackground,
        topBar = {
            Surface(
                color = PixelBackground,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack
                    ) {
                        Text(
                            text = "‹",
                            color = PixelBlueBright,
                            fontSize = 32.sp
                        )
                    }

                    AppAvatar(
                        initials = chat.initials,
                        size = 42.dp
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = chat.name,
                            color = PixelText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = if (chat.online) {
                                "в сети"
                            } else {
                                "был недавно"
                            },
                            color = if (chat.online) {
                                PixelSuccess
                            } else {
                                PixelMuted
                            },
                            fontSize = 11.sp
                        )
                    }

                    TextButton(
                        onClick = {
                            // Позже подключим настоящий звонок.
                        }
                    ) {
                        Text(
                            text = "☎",
                            color = PixelBlueBright,
                            fontSize = 22.sp
                        )
                    }
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                contentPadding = PaddingValues(
                    top = 10.dp,
                    bottom = 10.dp
                ),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(
                    count = messages.size,
                    key = { index -> index }
                ) { index ->

                    val message = messages[index]

                    PixelChatMessage(
                        text = message,
                        outgoing = true
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PixelSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .clickable {
                                // Позже здесь будет меню:
                                // фото, видео, файл, контакт и т.д.
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            color = PixelBlueBright,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(5.dp))

                    OutlinedTextField(
                        value = input,
                        onValueChange = {
                            input = it
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Сообщение",
                                color = PixelMuted
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(22.dp)
                    )

                    Spacer(Modifier.width(7.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (input.isNotBlank()) {
                                    PixelBlue
                                } else {
                                    PixelBlueContainer
                                }
                            )
                            .clickable {
                                val message = input.trim()

                                if (message.isNotEmpty()) {
                                    messages.add(message)
                                    input = ""

                                    scope.launch {
                                        if (messages.isNotEmpty()) {
                                            listState.animateScrollToItem(
                                                messages.lastIndex
                                            )
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "➤",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PixelChatMessage(
    text: String,
    outgoing: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (outgoing) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 310.dp),
            color = if (outgoing) {
                PixelBlueContainer
            } else {
                PixelSurfaceHigh
            },
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (outgoing) 18.dp else 6.dp,
                bottomEnd = if (outgoing) 6.dp else 18.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 13.dp,
                    vertical = 9.dp
                )
            ) {
                Text(
                    text = text,
                    color = PixelText,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "сейчас",
                    color = PixelMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
'''


def find_function_end(text: str, start: int) -> int:
    brace_start = text.find("{", start)

    if brace_start == -1:
        raise RuntimeError(
            "Не найдено начало тела ChatScreen."
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
        "Не найден конец ChatScreen."
    )


def main():
    if not FILE.exists():
        raise FileNotFoundError(
            f"Файл не найден: {FILE}"
        )

    text = FILE.read_text(encoding="utf-8")

    match = re.search(
        r"(?m)^\s*@OptIn\(ExperimentalMaterial3Api::class\)\s*\n"
        r"\s*@Composable\s*\n"
        r"\s*private fun ChatScreen\s*\(",
        text
    )

    if not match:
        raise RuntimeError(
            "ChatScreen не найдена."
        )

    start = match.start()
    end = find_function_end(text, match.start())

    backup = FILE.with_suffix(
        FILE.suffix + ".before_chat_screen"
    )

    shutil.copy2(FILE, backup)

    updated = (
        text[:start]
        + NEW_CHAT_SCREEN.strip()
        + "\n\n"
        + text[end:]
    )

    FILE.write_text(
        updated,
        encoding="utf-8"
    )

    print("Готово.")
    print()
    print("ChatScreen обновлён.")
    print("Добавлено:")
    print(" - новая шапка чата")
    print(" - статус пользователя")
    print(" - сообщения")
    print(" - поле ввода")
    print(" - кнопка вложений")
    print(" - кнопка отправки")
    print(" - автопрокрутка")
    print()
    print(f"Файл: {FILE}")
    print(f"Резервная копия: {backup}")


if __name__ == "__main__":
    main()
