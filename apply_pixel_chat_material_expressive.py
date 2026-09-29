#!/usr/bin/env python3
from pathlib import Path
import re
import shutil

ROOT = Path.home() / "PIXEL-MESSENGER"
UI_FILE = ROOT / "android-ui/app/src/main/java/com/pixelchat/ui/MainActivity.kt"
M3_VERSION = "1.5.0-alpha29"


def find_function_span(src: str, name: str):
    m = re.search(r"(?:@Composable\s+)?(?:@OptIn\([^\n]+\)\s+)?(?:private\s+)?fun\s+" + re.escape(name) + r"\s*\(", src)
    if not m:
        raise RuntimeError(f"Не найдена функция {name}()")
    brace = src.find("{", m.end())
    if brace < 0:
        raise RuntimeError(f"Не найдено тело {name}()")
    depth = 0
    i = brace
    state = "code"
    escaped = False
    while i < len(src):
        c = src[i]
        n = src[i + 1] if i + 1 < len(src) else ""
        if state == "code":
            if c == "/" and n == "/":
                state = "line"; i += 2; continue
            if c == "/" and n == "*":
                state = "block"; i += 2; continue
            if c == '"':
                state = "string"; escaped = False; i += 1; continue
            if c == "'":
                state = "char"; escaped = False; i += 1; continue
            if c == "{": depth += 1
            elif c == "}":
                depth -= 1
                if depth == 0:
                    return m.start(), i + 1
            i += 1; continue
        if state == "string":
            if escaped: escaped = False
            elif c == "\\": escaped = True
            elif c == '"': state = "code"
            i += 1; continue
        if state == "char":
            if escaped: escaped = False
            elif c == "\\": escaped = True
            elif c == "'": state = "code"
            i += 1; continue
        if state == "line":
            if c == "\n": state = "code"
            i += 1; continue
        if state == "block":
            if c == "*" and n == "/": state = "code"; i += 2
            else: i += 1
    raise RuntimeError(f"Не найден конец {name}()")


def replace_function(src: str, name: str, replacement: str):
    s, e = find_function_span(src, name)
    return src[:s] + replacement.strip() + src[e:]


def ensure_imports(src: str, imports):
    missing = [x for x in imports if f"import {x}" not in src]
    if not missing:
        return src
    m = re.search(r"^package\s+[^\n]+\n", src, re.M)
    if not m:
        raise RuntimeError("Не найден package")
    block = "\n" + "\n".join(f"import {x}" for x in missing) + "\n"
    return src[:m.end()] + block + src[m.end():]


THEME = r'''@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PixelChatTheme() {
    val scheme = darkColorScheme(
        primary = PixelBlueBright,
        onPrimary = Color(0xFF06213A),
        primaryContainer = PixelBlueContainer,
        onPrimaryContainer = Color(0xFFD8EDFF),
        secondary = Color(0xFFB9CAD9),
        onSecondary = Color(0xFF172530),
        secondaryContainer = Color(0xFF2D3D49),
        onSecondaryContainer = Color(0xFFDCEAF6),
        tertiary = Color(0xFFD3C0FF),
        onTertiary = Color(0xFF2C214A),
        tertiaryContainer = Color(0xFF4B3A68),
        onTertiaryContainer = Color(0xFFEDDEFF),
        background = PixelBackground,
        onBackground = PixelText,
        surface = PixelBackground,
        onSurface = PixelText,
        surfaceVariant = PixelSurfaceHigh,
        onSurfaceVariant = PixelMuted,
        surfaceContainer = PixelSurface,
        surfaceContainerHigh = PixelSurfaceHigh,
        surfaceContainerHighest = Color(0xFF1E2F3D),
        outline = PixelOutline
    )

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(18.dp),
        large = RoundedCornerShape(28.dp),
        extraLarge = RoundedCornerShape(32.dp),
        largeIncreased = RoundedCornerShape(36.dp),
        extraLargeIncreased = RoundedCornerShape(40.dp)
    )

    MaterialExpressiveTheme(
        colorScheme = scheme,
        motionScheme = MotionScheme.expressive(),
        shapes = shapes
    ) {
        PixelChatApp()
    }
}'''

HOME = r'''@Composable
private fun HomeScreen(onLogout: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(0) }
    var openedChat by remember { mutableStateOf<ChatItem?>(null) }
    var showNewChat by remember { mutableStateOf(false) }

    val chats = remember {
        listOf(
            ChatItem("PIXEL CHAT", "Добро пожаловать в новый интерфейс", "19:42", "P"),
            ChatItem("Алекс", "Увидимся позже", "18:27", "A", 2, true),
            ChatItem("Дима", "Смотри, что я нашёл", "17:51", "D"),
            ChatItem("Game Dev", "Новый билд уже готов", "16:05", "G", 4),
            ChatItem("София", "Напиши, когда будешь онлайн", "15:10", "S", online = true)
        )
    }

    if (openedChat != null) {
        ChatScreen(chat = openedChat!!, onBack = { openedChat = null })
        return
    }
    if (showNewChat) {
        NewChatScreen(
            onBack = { showNewChat = false },
            onSelect = {
                openedChat = it
                showNewChat = false
            }
        )
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            AnimatedVisibility(
                visible = tab == 0,
                enter = fadeIn() + scaleIn(animationSpec = MotionScheme.expressive().defaultSpatialSpec()),
                exit = fadeOut() + scaleOut(animationSpec = MotionScheme.expressive().defaultSpatialSpec())
            ) {
                LargeFloatingActionButton(
                    onClick = { showNewChat = true },
                    shape = MaterialTheme.shapes.largeIncreased,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = {
            PixelTelegramNavigation(selected = tab, onSelected = { tab = it })
        }
    ) { padding ->
        AnimatedContent(
            targetState = tab,
            modifier = Modifier.fillMaxSize().padding(padding),
            transitionSpec = {
                fadeIn(animationSpec = MotionScheme.expressive().defaultEffectsSpec()) +
                    slideInHorizontally(
                        animationSpec = MotionScheme.expressive().defaultSpatialSpec(),
                        initialOffsetX = { it / 10 }
                    ) togetherWith
                    fadeOut(animationSpec = MotionScheme.expressive().fastEffectsSpec()) +
                    slideOutHorizontally(
                        animationSpec = MotionScheme.expressive().fastSpatialSpec(),
                        targetOffsetX = { -it / 14 }
                    )
            },
            label = "pixel_main_tab"
        ) { current ->
            when (current) {
                0 -> ChatsTab(chats = chats, onOpen = { openedChat = it })
                1 -> ContactsTab(onOpen = { openedChat = it })
                2 -> SettingsTab(onLogout = onLogout)
                else -> ProfileTab(onSettings = { tab = 2 })
            }
        }
    }
}'''

NAV = r'''@Composable
private fun PixelTelegramNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        "Чаты" to "◉",
        "Контакты" to "◎",
        "Настройки" to "⚙",
        "Профиль" to "●"
    )

    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 4.dp
    ) {
        items.forEachIndexed { index, item ->
            val active = selected == index
            NavigationBarItem(
                selected = active,
                onClick = { onSelected(index) },
                icon = {
                    AnimatedContent(
                        targetState = active,
                        label = "nav_$index"
                    ) { isSelected ->
                        Surface(
                            shape = if (isSelected) {
                                MaterialTheme.shapes.large
                            } else {
                                MaterialTheme.shapes.medium
                            },
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                Color.Transparent
                            }
                        ) {
                            Text(
                                text = item.second,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontSize = 21.sp
                            )
                        }
                    }
                },
                label = {
                    Text(item.first, style = MaterialTheme.typography.labelMedium)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}'''

CHATS = r'''@Composable
private fun ChatsTab(
    chats: List<ChatItem>,
    onOpen: (ChatItem) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(0) }

    val filtered = chats.filter { chat ->
        val matchesSearch = chat.name.contains(query, true) || chat.message.contains(query, true)
        val matchesFilter = when (filter) {
            0 -> true
            1 -> chat.online
            2 -> chat.unread > 0
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        LargeTopAppBar(
            title = {
                Column {
                    Text(
                        "Чаты",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "${filtered.size} диалога",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        "P",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            },
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            singleLine = true,
            label = { Text("Поиск") },
            placeholder = { Text("Чаты и сообщения") },
            leadingIcon = {
                Text("⌕", fontSize = 25.sp, color = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    TextButton(onClick = { query = "" }) { Text("Очистить") }
                }
            },
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PixelFilterChip("Все", filter == 0) { filter = 0 }
            PixelFilterChip("В сети", filter == 1) { filter = 1 }
            PixelFilterChip("Непрочитанные", filter == 2) { filter = 2 }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            items(
                items = filtered,
                key = { "${it.name}_${it.time}" }
            ) { chat ->
                PixelExpressiveChatItem(chat = chat, onClick = { onOpen(chat) })
            }
        }
    }
}'''

FILTER = r'''@Composable
private fun PixelFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        shape = if (selected) MaterialTheme.shapes.large else MaterialTheme.shapes.medium
    )
}'''

CHAT_ITEM = r'''@Composable
private fun PixelExpressiveChatItem(
    chat: ChatItem,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (pressed) 6.dp else 1.dp,
        animationSpec = MotionScheme.expressive().defaultEffectsSpec(),
        label = "chat_elevation"
    )

    ListItem(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        leadingContent = {
            Box {
                AppAvatar(chat.initials, 56.dp)
                AnimatedVisibility(
                    visible = chat.online,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
            }
        },
        supportingContent = {
            Text(
                chat.message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    chat.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnimatedVisibility(
                    visible = chat.unread > 0,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Badge(modifier = Modifier.padding(top = 5.dp)) {
                        Text(chat.unread.toString())
                    }
                }
            }
        },
        shapes = ListItemDefaults.shapes(),
        colors = ListItemDefaults.colors(
            containerColor = if (pressed) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ListItemDefaults.elevation(
            elevation = elevation,
            draggedElevation = 8.dp
        ),
        interactionSource = interaction
    ) {
        Text(
            chat.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}'''

imports = [
    "androidx.compose.animation.scaleIn",
    "androidx.compose.animation.scaleOut",
    "androidx.compose.animation.animateDpAsState",
    "androidx.compose.foundation.interaction.MutableInteractionSource",
    "androidx.compose.foundation.interaction.collectIsPressedAsState",
    "androidx.compose.material3.Badge",
    "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
    "androidx.compose.material3.FilterChip",
    "androidx.compose.material3.LargeFloatingActionButton",
    "androidx.compose.material3.LargeTopAppBar",
    "androidx.compose.material3.ListItem",
    "androidx.compose.material3.ListItemDefaults",
    "androidx.compose.material3.MaterialExpressiveTheme",
    "androidx.compose.material3.MotionScheme",
    "androidx.compose.material3.Shapes",
]

source = UI_FILE.read_text(encoding="utf-8")

if "@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)" not in source:
    source = "@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)\n" + source
backup = UI_FILE.with_suffix(".kt.before_true_expressive")
if not backup.exists():
    shutil.copy2(UI_FILE, backup)

source = ensure_imports(source, imports)
source = replace_function(source, "PixelChatTheme", THEME)
source = replace_function(source, "HomeScreen", HOME)
source = replace_function(source, "PixelTelegramNavigation", NAV)
source = replace_function(source, "ChatsTab", CHATS)

if re.search(r"(?:@Composable\s+)?(?:private\s+)?fun\s+ChatRow\s*\(", source):
    source = replace_function(source, "ChatRow", "")

# Insert helpers immediately after ChatsTab.
anchor_start, anchor_end = find_function_span(source, "ChatsTab")
helpers = FILTER + "\n\n" + CHAT_ITEM
if "private fun PixelFilterChip(" not in source:
    source = source[:anchor_end] + "\n\n" + helpers + source[anchor_end:]

UI_FILE.write_text(source, encoding="utf-8")

# Upgrade literal material3 dependency declarations and version catalogs.
touched = []
android_ui = ROOT / "android-ui"
if android_ui.exists():
    for p in list(android_ui.rglob("build.gradle")) + list(android_ui.rglob("build.gradle.kts")):
        try:
            txt = p.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
        new = re.sub(r"(androidx\.compose\.material3:material3:)[0-9A-Za-z_.+\-]+", rf"\g<1>{M3_VERSION}", txt)
        if new != txt:
            b = p.with_name(p.name + ".before_true_expressive")
            if not b.exists(): shutil.copy2(p, b)
            p.write_text(new, encoding="utf-8")
            touched.append(str(p))

# Patch Gradle version catalogs if the project uses one.
for p in [*ROOT.rglob("libs.versions.toml"), *ROOT.rglob("gradle/libs.versions.toml")]:
    if not p.is_file():
        continue
    try:
        txt = p.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        continue

    new = txt

    # Common catalog form: material3 = "1.5.0-alphaXX"
    new = re.sub(
        r'(?m)^(\s*material3\s*=\s*")[^"]+("\s*)$',
        rf'\g<1>{M3_VERSION}\g<2>',
        new,
    )

    # Also patch inline module version declarations when used.
    new = re.sub(
        r'(androidx\.compose\.material3:material3:)[0-9A-Za-z_.+\-]+',
        rf'\g<1>{M3_VERSION}',
        new,
    )

    if new != txt:
        backup = p.with_name(p.name + ".before_true_expressive")
        if not backup.exists():
            shutil.copy2(p, backup)
        p.write_text(new, encoding="utf-8")
        touched.append(str(p))

# Basic structural checks.
for required in [
    "private fun PixelChatTheme",
    "private fun HomeScreen",
    "private fun PixelTelegramNavigation",
    "private fun ChatsTab",
    "private fun PixelFilterChip",
    "private fun PixelExpressiveChatItem",
    "MaterialExpressiveTheme(",
    "MotionScheme.expressive()",
]:
    if required not in source:
        raise RuntimeError(f"Проверка не пройдена: {required}")

print("==============================================")
print(" PIXEL CHAT — TRUE MATERIAL 3 EXPRESSIVE")
print("==============================================")
print("Файл UI обновлён:", UI_FILE)
print("Бэкап:", backup)
print("Material 3:", M3_VERSION)
print()
print("Включено:")
print("  ✓ MaterialExpressiveTheme")
print("  ✓ MotionScheme.expressive()")
print("  ✓ Material Expressive Shapes")
print("  ✓ Expressive ListItem")
print("  ✓ Expressive FilterChip")
print("  ✓ Material Large FAB")
print("  ✓ Material NavigationBar")
print("  ✓ expressive screen transitions")
print("  ✓ animated pressed states")
print("  ✓ animated badges / online state")
print("  ✓ real Material color roles")
print()
print("Обновлены Gradle-файлы:")
for item in touched:
    print("  -", item)
if not touched:
    print("  - literal Material3 dependency не найден; проверь version catalog при необходимости")
print()
print("APK локально не собирай. После проверки: git -> GitHub -> GitHub Actions.")
