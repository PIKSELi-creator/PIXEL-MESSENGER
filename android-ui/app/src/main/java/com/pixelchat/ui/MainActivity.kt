package com.pixelchat.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PixelBlue = Color(0xFF4D9FFF)
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

private data class ChatItem(
    val name: String,
    val message: String,
    val time: String,
    val initials: String,
    val unread: Int = 0,
    val online: Boolean = false
)

private data class ContactItem(
    val name: String,
    val username: String,
    val initials: String,
    val online: Boolean = false
)

private data class CallItem(
    val name: String,
    val time: String,
    val initials: String,
    val missed: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PixelChatTheme() }
    }
}

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

    MaterialTheme(colorScheme = scheme) {
        PixelChatApp()
    }
}

@Composable
private fun PixelChatApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionStore = remember { SessionStore(context) }
    val scope = rememberCoroutineScope()

    var screen by rememberSaveable { mutableStateOf("splash") }
    var registrationEmail by rememberSaveable { mutableStateOf("") }
    var sessionLoaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(700)
        sessionLoaded = true
        screen = if (sessionStore.getSession() != null) "home" else "welcome"
    }

    if (!sessionLoaded) {
        SplashScreen()
        return
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(tween(220)) + slideInHorizontally(
                tween(260),
                initialOffsetX = { it / 8 }
            ) togetherWith fadeOut(tween(160)) + slideOutHorizontally(
                tween(180),
                targetOffsetX = { -it / 12 }
            )
        },
        label = "root_screen_transition"
    ) { current ->
        when (current) {
            "welcome" -> WelcomeScreen(
                onLogin = { screen = "login" },
                onRegister = { screen = "register" }
            )

            "login" -> LoginScreen(
                onBack = { screen = "welcome" },
                onRegister = { screen = "register" },
                onLogin = { screen = "home" }
            )

            "register" -> RegisterScreen(
                onBack = { screen = "welcome" },
                onVerified = {
                    registrationEmail = it
                    screen = "profile_setup"
                }
            )

            "profile_setup" -> ProfileSetupScreen(
                email = registrationEmail,
                onComplete = { name, username ->
                    scope.launch {
                        sessionStore.saveSession(
                            email = registrationEmail,
                            displayName = name,
                            username = username
                        )
                        screen = "home"
                    }
                }
            )

            else -> HomeScreen(
                onLogout = {
                    scope.launch {
                        sessionStore.clearSession()
                        screen = "welcome"
                    }
                }
            )
        }
    }
}

@Composable
private fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PixelLogo(112.dp)
            Spacer(Modifier.height(18.dp))
            Text(
                "PIXEL CHAT",
                color = PixelText,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Твой мир. Твои чаты.",
                color = PixelMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun PixelLogo(size: Dp) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.66f)
                .clip(RoundedCornerShape(size * 0.20f))
                .background(PixelBlueContainer)
        )
        Box(
            modifier = Modifier
                .size(size * 0.44f)
                .clip(RoundedCornerShape(size * 0.16f))
                .background(PixelBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "P",
                color = Color.White,
                fontSize = (size.value * 0.30f).sp,
                fontWeight = FontWeight.Black
            )
        }
        Box(
            modifier = Modifier
                .size(size * 0.14f)
                .align(Alignment.TopStart)
                .offset(size * 0.28f, size * 0.08f)
                .clip(CircleShape)
                .background(PixelBlueBright)
        )
        Box(
            modifier = Modifier
                .size(size * 0.12f)
                .align(Alignment.BottomEnd)
                .offset(-size * 0.17f, -size * 0.13f)
                .clip(CircleShape)
                .background(Color(0xFF8A77FF))
        )
    }
}

@Composable
private fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PixelLogo(118.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                "PIXEL CHAT",
                color = PixelText,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(7.dp))
            Text(
                "Мессенджер с чистым интерфейсом\nи современным Material 3 Expressive стилем.",
                color = PixelMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PixelSurface,
                shape = RoundedCornerShape(30.dp),
                tonalElevation = 4.dp
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "Добро пожаловать",
                        color = PixelText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Вход и регистрация сохраняются отдельно от интерфейса.",
                        color = PixelMuted,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onLogin,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Войти", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onRegister,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Создать аккаунт", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthCard(
        title = "С возвращением",
        subtitle = "Войдите в PIXEL CHAT.",
        onBack = onBack
    ) {
        AuthField(
            label = "Gmail",
            value = email,
            onValueChange = { email = it },
            placeholder = "name@gmail.com"
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Пароль") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(18.dp)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onLogin,
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Войти", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        TextButton(onClick = {}) { Text("Забыли пароль?") }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Нет аккаунта?", color = PixelMuted, fontSize = 13.sp)
            TextButton(onClick = onRegister) { Text("Создать") }
        }
    }
}

@Composable
private fun AuthCard(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(18.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("‹", color = PixelBlueBright, fontSize = 30.sp)
            }
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PixelSurface,
                shape = RoundedCornerShape(30.dp),
                tonalElevation = 4.dp
            ) {
                Column(Modifier.padding(20.dp)) {
                    PixelLogo(68.dp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        title,
                        color = PixelText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        subtitle,
                        color = PixelMuted,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun RegisterScreen(
    onBack: () -> Unit,
    onVerified: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var codeScreen by rememberSaveable { mutableStateOf(false) }

    if (codeScreen) {
        VerificationScreen(
            email = email,
            onBack = { codeScreen = false },
            onVerified = { onVerified(email) }
        )
        return
    }

    AuthCard(
        title = "Создать аккаунт",
        subtitle = "Подтвердим Gmail одноразовым кодом.",
        onBack = onBack
    ) {
        AuthField(
            label = "Gmail",
            value = email,
            onValueChange = { email = it.trimStart() },
            placeholder = "name@gmail.com"
        )
        Spacer(Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelBlueContainer,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(15.dp)) {
                Text(
                    "Подтверждение email",
                    color = PixelText,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Код будет отправлен на указанную почту сервером PIXEL CHAT.",
                    color = PixelMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { codeScreen = true },
            enabled = email.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Получить код", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun VerificationScreen(
    email: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var code by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {
        Column(
            modifier = Modifier.statusBarsPadding().padding(18.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("‹ Изменить Gmail", color = PixelBlueBright)
            }
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PixelSurface,
                shape = RoundedCornerShape(30.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(PixelBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✉", color = PixelBlueBright, fontSize = 27.sp)
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Введите код",
                        color = PixelText,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text("Код отправлен на", color = PixelMuted, fontSize = 13.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(email, color = PixelBlueBright, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            if (it.length <= 6 && it.all(Char::isDigit)) code = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("6‑значный код") },
                        placeholder = { Text("••••••") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(18.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = onVerified,
                        enabled = code.length == 6,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Подтвердить", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = {}) { Text("Отправить код повторно") }
                }
            }
        }
    }
}

@Composable
private fun ProfileSetupScreen(
    email: String,
    onComplete: suspend (String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Text("Последний шаг", color = PixelMuted, fontSize = 13.sp)
            Spacer(Modifier.height(5.dp))
            Text(
                "Настройте профиль",
                color = PixelText,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Имя и username будут видны контактам.",
                color = PixelMuted,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.size(94.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("P", color = PixelBlueBright, fontSize = 34.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(Modifier.height(22.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Имя") },
                placeholder = { Text("Pixel User") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it.replace(" ", "") },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                prefix = { Text("@") },
                placeholder = { Text("pixeluser") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(email, color = PixelMuted, fontSize = 12.sp)
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    scope.launch { onComplete(name.trim(), username.trim()) }
                },
                enabled = name.isNotBlank() && username.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Открыть PIXEL CHAT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomeScreen(onLogout: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf(0) }
    var openedChat by remember { mutableStateOf<ChatItem?>(null) }
    var showNewChat by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

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

    if (showSettings) {
        SettingsScreen(
            onBack = { showSettings = false },
            onLogout = onLogout
        )
        return
    }

    Scaffold(
        containerColor = PixelBackground,
        floatingActionButton = {
            AnimatedVisibility(
                visible = tab < 2,
                enter = fadeIn(tween(200)) + slideInVertically(
                    tween(200), initialOffsetY = { it / 2 }
                ),
                exit = fadeOut(tween(140))
            ) {
                FloatingActionButton(
                    onClick = { showNewChat = true },
                    containerColor = PixelBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text("+", fontSize = 28.sp)
                }
            }
        },
        bottomBar = {
            PixelTelegramNavigation(
                selected = tab,
                onSelected = { tab = it }
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = tab,
            modifier = Modifier.padding(padding),
            transitionSpec = {
                fadeIn(tween(180)) togetherWith fadeOut(tween(120))
            },
            label = "main_tab_transition"
        ) { current ->
            when (current) {
                0 -> ChatsTab(chats = chats, onOpen = { openedChat = it })
                1 -> ContactsTab(onOpen = { openedChat = it })
                2 -> CallsTab()
                else -> ProfileTab(onSettings = { showSettings = true })
            }
        }
    }
}

@Composable
private fun PixelTelegramNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        "Чаты" to "◉",
        "Контакты" to "◎",
        "Звонки" to "☎",
        "Профиль" to "●"
    )

    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = PixelSurface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val active = selected == index

            NavigationBarItem(
                selected = active,
                onClick = { onSelected(index) },
                icon = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (active) PixelBlueContainer else Color.Transparent
                    ) {
                        Text(
                            item.second,
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 5.dp),
                            color = if (active) PixelBlueBright else PixelMuted,
                            fontSize = 21.sp
                        )
                    }
                },
                label = {
                    Text(
                        item.first,
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = PixelBlueBright,
                    selectedTextColor = PixelBlueBright,
                    unselectedIconColor = PixelMuted,
                    unselectedTextColor = PixelMuted
                )
            )
        }
    }
}

@Composable
private fun ChatsTab(
    chats: List<ChatItem>,
    onOpen: (ChatItem) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }

    val filtered = chats.filter {
        it.name.contains(query, true) || it.message.contains(query, true)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Чаты",
                    color = PixelText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "${filtered.size} диалога",
                    color = PixelMuted,
                    fontSize = 12.sp
                )
            }
            Surface(shape = CircleShape, color = PixelBlueContainer) {
                Text(
                    "P",
                    modifier = Modifier.padding(12.dp),
                    color = PixelBlueBright,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск чатов") },
            leadingIcon = { Text("⌕", color = PixelMuted, fontSize = 22.sp) },
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(filtered) { chat ->
                ChatRow(chat, onClick = { onOpen(chat) })
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatItem, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .background(if (pressed) PixelSurfaceHigh else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AppAvatar(chat.initials, 58.dp)
            if (chat.online) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(PixelSuccess)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                chat.name,
                color = PixelText,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                chat.message,
                color = PixelMuted,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(chat.time, color = PixelMuted, fontSize = 11.sp)
            if (chat.unread > 0) {
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier.size(21.dp).clip(CircleShape).background(PixelBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(chat.unread.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ContactsTab(onOpen: (ChatItem) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val contacts = remember {
        listOf(
            ContactItem("Алекс", "@alex_dev", "A", true),
            ContactItem("Дима", "@dimas", "D"),
            ContactItem("Марк", "@markpixel", "M"),
            ContactItem("София", "@sofia_dev", "S", true),
            ContactItem("Иван", "@ivan_game", "I")
        )
    }
    val filtered = contacts.filter {
        it.name.contains(query, true) || it.username.contains(query, true)
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text(
            "Контакты",
            color = PixelText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск людей") },
            leadingIcon = { Text("⌕", color = PixelMuted, fontSize = 22.sp) },
            shape = RoundedCornerShape(20.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(filtered) { contact ->
                ContactRow(
                    contact = contact,
                    onClick = {
                        onOpen(
                            ChatItem(
                                contact.name,
                                "Новый чат",
                                "сейчас",
                                contact.initials
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ContactRow(contact: ContactItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppAvatar(contact.initials, 54.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(contact.name, color = PixelText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(
                if (contact.online) "в сети • ${contact.username}" else contact.username,
                color = if (contact.online) PixelSuccess else PixelMuted,
                fontSize = 12.sp
            )
        }
        Text("Чат", color = PixelBlueBright, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CallsTab() {
    val calls = remember {
        listOf(
            CallItem("Алекс", "Сегодня, 18:41", "A"),
            CallItem("Дима", "Сегодня, 16:02", "D"),
            CallItem("Сергей", "Вчера, 22:11", "S", missed = true),
            CallItem("София", "Вчера, 19:43", "S")
        )
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text(
            "Звонки",
            color = PixelText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = PixelSurface
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CALL", color = PixelBlueBright, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Голосовые и видеозвонки будут подключены к серверу позже.",
                    color = PixelMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(calls) { call ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PixelSurface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppAvatar(call.initials, 52.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(call.name, color = PixelText, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                call.time,
                                color = if (call.missed) PixelDanger else PixelMuted,
                                fontSize = 12.sp
                            )
                        }
                        Text("☎", color = PixelBlueBright, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTab(onSettings: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Профиль",
                color = PixelText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSettings) { Text("Настройки") }
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelSurface,
            shape = RoundedCornerShape(30.dp),
            tonalElevation = 4.dp
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppAvatar("P", 88.dp)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Pixel User",
                            color = PixelText,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("@pixeluser", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text("в сети", color = PixelSuccess, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                ProfileInfoRow("Gmail", "Подтверждённый аккаунт")
                ProfileInfoRow("Bio", "Я в PIXEL CHAT")
            }
        }

        Spacer(Modifier.height(10.dp))
        ProfileAction("Изменить профиль", "Имя, username и аватар")
        ProfileAction("QR-профиль", "Поделиться профилем")
        ProfileAction("Активные сессии", "Устройства, где открыт аккаунт")

        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = PixelBlueContainer
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("GIFTS", color = PixelBlueBright, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Подарки", color = PixelText, fontWeight = FontWeight.Bold)
                    Text("Будущий раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(title, color = PixelMuted, fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, color = PixelText, fontSize = 14.sp)
    }
}

@Composable
private fun ProfileAction(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(PixelBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("•", color = PixelBlueBright, fontSize = 24.sp)
            }
            Spacer(Modifier.width(13.dp))
            Column {
                Text(title, color = PixelText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = PixelMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var notifications by rememberSaveable { mutableStateOf(true) }
    var compact by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("‹", color = PixelBlueBright, fontSize = 30.sp)
            }
            Text(
                "Настройки",
                color = PixelText,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(Modifier.height(8.dp))
        SettingSwitch("Уведомления", "Новые сообщения и ответы", notifications) { notifications = it }
        SettingSwitch("Компактный режим", "Меньше отступов в списках", compact) { compact = it }
        SettingAction("Тема", "Тёмная • PIXEL NIGHT")
        SettingAction("Конфиденциальность", "Приватность, блокировки и безопасность")
        SettingAction("Безопасность", "Сессии, пароль и шифрование")
        SettingAction("О PIXEL CHAT", "UI • Material 3 Expressive • Server 10.0")
        Spacer(Modifier.height(14.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PixelText)
        ) {
            Text("Выйти из аккаунта")
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = PixelText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingAction(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(15.dp)) {
            Text(title, color = PixelText, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = PixelMuted, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(chat: ChatItem, onBack: () -> Unit) {
    var input by rememberSaveable { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            "Привет! Это новый PIXEL CHAT.",
            "Дизайн уже на Material 3 Expressive.",
            "Нижняя навигация сделана в стиле Telegram."
        )
    }

    Scaffold(
        containerColor = PixelBackground,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("‹", fontSize = 31.sp, color = PixelBlueBright)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppAvatar(chat.initials, 38.dp)
                        Spacer(Modifier.width(9.dp))
                        Column {
                            Text(chat.name, color = PixelText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                if (chat.online) "в сети" else "был недавно",
                                color = if (chat.online) PixelSuccess else PixelMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PixelBackground
                )
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(messages) { message ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            color = PixelBlueContainer,
                            shape = RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = 18.dp,
                                bottomEnd = 6.dp
                            )
                        ) {
                            Text(
                                message,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                color = PixelText,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Сообщение") },
                    singleLine = true,
                    shape = RoundedCornerShape(19.dp)
                )
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            messages.add(input.trim())
                            input = ""
                        }
                    },
                    modifier = Modifier.size(52.dp),
                    containerColor = PixelBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Text("➤", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun NewChatScreen(
    onBack: () -> Unit,
    onSelect: (ChatItem) -> Unit
) {
    val contacts = remember {
        listOf(
            ContactItem("Алекс", "@alex_dev", "A", true),
            ContactItem("Дима", "@dimas", "D"),
            ContactItem("Марк", "@markpixel", "M"),
            ContactItem("София", "@sofia_dev", "S", true)
        )
    }

    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("‹", color = PixelBlueBright, fontSize = 30.sp)
            }
            Text(
                "Новый чат",
                color = PixelText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            placeholder = { Text("Поиск контакта") },
            leadingIcon = { Text("⌕", color = PixelMuted, fontSize = 22.sp) },
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(contacts) { contact ->
                ContactRow(
                    contact = contact,
                    onClick = {
                        onSelect(
                            ChatItem(
                                contact.name,
                                "Новый чат",
                                "сейчас",
                                contact.initials,
                                online = contact.online
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AppAvatar(initials: String, size: Dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = PixelBlueContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                initials.take(2).uppercase(),
                color = PixelBlueBright,
                fontSize = (size.value * 0.29f).sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ============================================================================
// PIXEL CHAT 3000 — EXPANDED SINGLE-FILE UI
// Reusable Material 3 Expressive components for future PIXEL CHAT features.
// Network, TLS and cryptography remain outside the Compose UI layer.
// ============================================================================

private data class PixelFullItem(val title: String, val subtitle: String)
private data class PixelFullStat(val title: String, val value: String)
private data class PixelFullGift(val title: String, val subtitle: String, val icon: String)
private data class PixelFullSession(val device: String, val location: String, val active: Boolean)

@Composable
private fun PixelFullAccountCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("АК", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Аккаунт", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Gmail, username и профиль", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullPrivacyCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ПР", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Приватность", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Контроль видимости профиля", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullSecurityCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("БЕ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Безопасность", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Пароль, сессии и защита", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullNotificationsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("УВ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Уведомления", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Звуки и уведомления", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullCallsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ЗВ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Звонки", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Голосовые и видеозвонки", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullMediaCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("МЕ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Медиа", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Фото, видео и файлы", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullGiftsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "PIXEL GIFTS",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Пиксель-подарки для профиля и чатов.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PixelGiftTile("♥", "Heart")
            PixelGiftTile("★", "Star")
            PixelGiftTile("◆", "Gem")
            PixelGiftTile("●", "Coin")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Следующим этапом можно подключить покупку, отправку и анимации.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun PixelGiftTile(symbol: String, name: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = symbol, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = name, style = MaterialTheme.typography.labelSmall)
    }
}



@Composable
private fun PixelFullSessionsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("СЕ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Сессии", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Подключённые устройства", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullThemeCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ОФ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Оформление", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Material 3 Expressive", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullLanguageCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ЯЗ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Язык", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Язык приложения", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullArchiveCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("АР", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Архив", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Архивированные чаты", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullFavoritesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ИЗ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Избранное", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Сохранённые сообщения", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullDraftsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ЧЕ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Черновики", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Незавершённые сообщения", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullStoriesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ИС", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Истории", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Профильные истории", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullGroupsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ГР", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Группы", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Общие чаты", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullChannelsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("КА", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Каналы", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Публикации", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullBotsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("БО", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Боты", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Автоматические помощники", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullSearchCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ПО", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Поиск", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Поиск по чатам и людям", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullContactsCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("КО", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Контакты", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Список пользователей", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullProfileCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ПР", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Профиль", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Имя, username и статус", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullUpdatesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ОБ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Обновления", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Версия и изменения", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullHelpCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ПО", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Помощь", color = PixelText, fontWeight = FontWeight.Bold)
                Text("FAQ и поддержка", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullAboutCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("О ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("О приложении", color = PixelText, fontWeight = FontWeight.Bold)
                Text("PIXEL CHAT UI", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullDataCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ДА", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Данные", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Память и автозагрузка", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullDevicesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("УС", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Устройства", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Активные устройства", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullQRCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("QR", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("QR-профиль", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Быстрый обмен профилем", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullDeveloperCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("РА", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Разработчик", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Server 10.0 и API", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullFeedbackCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = PixelBlueContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("ОБ", color = PixelBlueBright, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Обратная связь", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Ошибки и предложения", color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 24.sp)
        }
    }
}

@Composable
private fun PixelFullAccountScreen() {
    val rows = listOf(
        PixelFullItem("Gmail", "PIXEL CHAT"),
        PixelFullItem("Имя", "PIXEL CHAT"),
        PixelFullItem("Username", "PIXEL CHAT"),
        PixelFullItem("ID", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Аккаунт", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullPrivacyScreen() {
    val rows = listOf(
        PixelFullItem("Номер телефона", "PIXEL CHAT"),
        PixelFullItem("Последний визит", "PIXEL CHAT"),
        PixelFullItem("Фото профиля", "PIXEL CHAT"),
        PixelFullItem("Блокировки", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Приватность", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullSecurityScreen() {
    val rows = listOf(
        PixelFullItem("Пароль", "PIXEL CHAT"),
        PixelFullItem("Активные сессии", "PIXEL CHAT"),
        PixelFullItem("Шифрование", "PIXEL CHAT"),
        PixelFullItem("Код блокировки", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Безопасность", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullNotificationsScreen() {
    val rows = listOf(
        PixelFullItem("Личные чаты", "PIXEL CHAT"),
        PixelFullItem("Группы", "PIXEL CHAT"),
        PixelFullItem("Звонки", "PIXEL CHAT"),
        PixelFullItem("Системные", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Уведомления", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullMediaScreen() {
    val rows = listOf(
        PixelFullItem("Фотографии", "PIXEL CHAT"),
        PixelFullItem("Видео", "PIXEL CHAT"),
        PixelFullItem("Файлы", "PIXEL CHAT"),
        PixelFullItem("Ссылки", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Медиа", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullGiftsScreen() {
    val rows = listOf(
        PixelFullItem("Звезда", "PIXEL CHAT"),
        PixelFullItem("Кристалл", "PIXEL CHAT"),
        PixelFullItem("Корона", "PIXEL CHAT"),
        PixelFullItem("Ракета", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Подарки", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullSessionsScreen() {
    val rows = listOf(
        PixelFullItem("Samsung Galaxy", "PIXEL CHAT"),
        PixelFullItem("Chrome", "PIXEL CHAT"),
        PixelFullItem("Desktop", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Активные сессии", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullThemeScreen() {
    val rows = listOf(
        PixelFullItem("PIXEL NIGHT", "PIXEL CHAT"),
        PixelFullItem("System", "PIXEL CHAT"),
        PixelFullItem("Высокий контраст", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Оформление", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullLanguageScreen() {
    val rows = listOf(
        PixelFullItem("Русский", "PIXEL CHAT"),
        PixelFullItem("English", "PIXEL CHAT"),
        PixelFullItem("Deutsch", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Язык", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullArchiveScreen() {
    val rows = listOf(
        PixelFullItem("Старые чаты", "PIXEL CHAT"),
        PixelFullItem("Проекты", "PIXEL CHAT"),
        PixelFullItem("Каналы", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Архив", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullFavoritesScreen() {
    val rows = listOf(
        PixelFullItem("Идеи", "PIXEL CHAT"),
        PixelFullItem("Код", "PIXEL CHAT"),
        PixelFullItem("Ссылки", "PIXEL CHAT"),
        PixelFullItem("Заметки", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Избранное", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullDevicesScreen() {
    val rows = listOf(
        PixelFullItem("Основной телефон", "PIXEL CHAT"),
        PixelFullItem("Браузер", "PIXEL CHAT"),
        PixelFullItem("Будущий ПК", "PIXEL CHAT")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("Устройства", color = PixelText, fontSize = 30.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text("Дополнительный раздел PIXEL CHAT", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(rows) { row ->
                PixelFullRow(row)
            }
        }
    }
}

@Composable
private fun PixelFullRow(item: PixelFullItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PixelSurface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppAvatar(item.title.take(1), 46.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.title, color = PixelText, fontWeight = FontWeight.Bold)
                Text(item.subtitle, color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 22.sp)
        }
    }
}

@Composable
private fun PixelFullDashboard() {
    val stats = listOf(
        PixelFullStat("Чаты", "5"),
        PixelFullStat("Контакты", "5"),
        PixelFullStat("Звонки", "4"),
        PixelFullStat("Сессии", "3")
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(10.dp))
        Text("PIXEL CHAT", color = PixelText, fontSize = 32.sp, fontWeight = FontWeight.Black)
        Text("Расширенный интерфейс", color = PixelMuted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            PixelFullStatBox(stats[0], Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            PixelFullStatBox(stats[1], Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            PixelFullStatBox(stats[2], Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            PixelFullStatBox(stats[3], Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        PixelFullProfileCard()
        Spacer(Modifier.height(7.dp))
        PixelFullSecurityCard()
        Spacer(Modifier.height(7.dp))
        PixelFullGiftsCard()
        Spacer(Modifier.height(7.dp))
        PixelFullCallsCard()
        Spacer(Modifier.height(7.dp))
        PixelFullAboutCard()
    }
}

@Composable
private fun PixelFullStatBox(item: PixelFullStat, modifier: Modifier) {
    Surface(modifier = modifier, color = PixelSurface, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(15.dp)) {
            Text(item.value, color = PixelBlueBright, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(2.dp))
            Text(item.title, color = PixelMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PixelFullGiftGrid() {
    val gifts = listOf(
        PixelFullGift("Звезда", "Обычный", "★"),
        PixelFullGift("Кристалл", "Редкий", "◆"),
        PixelFullGift("Корона", "Премиум", "♛"),
        PixelFullGift("Ракета", "Эпический", "➤")
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(gifts) { gift ->
            Surface(modifier = Modifier.fillMaxWidth(), color = PixelSurface, shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(gift.icon, color = PixelBlueBright, fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(gift.title, color = PixelText, fontWeight = FontWeight.Bold)
                        Text(gift.subtitle, color = PixelMuted, fontSize = 12.sp)
                    }
                    TextButton(onClick = {}) { Text("Открыть") }
                }
            }
        }
    }
}

@Composable
private fun PixelFullToggle1() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Онлайн-статус", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Показывать статус в сети", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle2() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Анимации", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Плавные переходы интерфейса", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle3() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Звук сообщений", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Воспроизводить звук сообщений", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle4() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Вибрация", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Вибрация при событиях", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle5() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Предпросмотр", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Показывать текст уведомлений", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle6() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Автозагрузка", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Автоматически загружать медиа", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle7() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Экономия", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Снижать фоновые эффекты", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle8() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Двойной тап", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Быстрые действия в чате", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle9() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Автовоспроизведение", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Воспроизводить медиа автоматически", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

@Composable
private fun PixelFullToggle10() {
    var enabled by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Синхронизация", color = PixelText, fontWeight = FontWeight.Bold)
                Text("Синхронизировать состояние UI", color = PixelMuted, fontSize = 12.sp)
            }
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
    }
}

// Integration note: connect chat state to the C++ client without moving network code into Compose.
// Integration note: map authenticated user data into immutable state before rendering ProfileTab.
// Integration note: route call state from the backend into CallsTab when signaling is ready.
// Integration note: keep Gmail verification in the auth/backend layer.
// Integration note: keep TLS configuration outside the UI file.
// Integration note: keep E2EE key operations outside Compose UI callbacks.
