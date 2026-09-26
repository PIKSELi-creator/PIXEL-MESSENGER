@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package com.pixelchat.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
                onStart = { screen = "register" }
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
private fun WelcomeScreen(onStart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PixelLogo(132.dp)

            Spacer(Modifier.height(22.dp))

            Text(
                "PIXEL CHAT",
                color = PixelText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Общайся. Звони. Отправляй подарки.",
                color = PixelMuted,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PixelSurface,
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 5.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Твой новый чат",
                        color = PixelText,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "PIXEL CHAT объединяет сообщения, контакты, звонки и профиль в одном приложении.",
                        color = PixelMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Text(
                        """• Чаты и контакты
• Голосовые звонки
• Профиль и подарки
• Защита сообщений""",
                        color = PixelText,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(19.dp)
            ) {
                Text(
                    "Начать",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "Для создания аккаунта понадобится только email",
                color = PixelMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
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
            PixelChatNavigation(selected = tab, onSelected = { tab = it })
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
}

@Composable
private fun SettingsTab(
    onLogout: () -> Unit
) {
    SettingsScreen(
        onBack = { },
        onLogout = onLogout
    )
}

@Composable
private fun PixelChatNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf(
        "Чаты" to Icons.Outlined.Chat,
        "Контакты" to Icons.Outlined.Contacts,
        "Настройки" to Icons.Outlined.Settings,
        "Профиль" to Icons.Outlined.Person
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
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
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
}



@Composable
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
}

@Composable
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
}

@Composable
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
    val onlineCount = contacts.count { it.online }

    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Spacer(Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelSurface,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 2.dp
        ) {
            Column(Modifier.padding(17.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Контакты", color = PixelText, fontSize = 31.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(3.dp))
                        Text("${contacts.size} контактов • $onlineCount в сети", color = PixelMuted, fontSize = 12.sp)
                    }
                    Surface(shape = CircleShape, color = PixelBlueContainer) {
                        Icon(
    imageVector = Icons.Outlined.Person,
    contentDescription = "Контакт",
    modifier = Modifier.padding(12.dp),
    tint = PixelBlueBright
)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск по имени или username") },
            leadingIcon = { Text("⌕", color = PixelMuted, fontSize = 23.sp) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    TextButton(onClick = { query = "" }) { Text("×", fontSize = 20.sp) }
                }
            },
            shape = RoundedCornerShape(21.dp)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(filtered, key = { it.username }) { contact ->
                ContactRow(
                    contact = contact,
                    onClick = {
                        onOpen(
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
private fun ContactRow(contact: ContactItem, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = if (pressed) PixelSurfaceHigh else PixelSurface,
        shape = RoundedCornerShape(21.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AppAvatar(contact.initials, 55.dp)
                if (contact.online) {
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
                Text(contact.name, color = PixelText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    if (contact.online) "в сети • ${contact.username}" else contact.username,
                    color = if (contact.online) PixelSuccess else PixelMuted,
                    fontSize = 12.sp
                )
            }

            Surface(shape = RoundedCornerShape(14.dp), color = PixelBlueContainer) {
                Text(
                    "Чат",
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    color = PixelBlueBright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
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
        Spacer(Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelSurface,
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Звонки", color = PixelText, fontSize = 31.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(3.dp))
                    Text("История голосовых и видеозвонков", color = PixelMuted, fontSize = 12.sp)
                }
                Surface(shape = CircleShape, color = PixelBlueContainer) {
                    Text("☎", modifier = Modifier.padding(12.dp), color = PixelBlueBright, fontSize = 19.sp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelBlueContainer,
            shape = RoundedCornerShape(22.dp)
        ) {
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("CALL", color = PixelBlueBright, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Сигналинг и WebRTC подключим к серверу позже.",
                    color = PixelText,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(calls, key = { it.name + it.time }) { call ->
                val interaction = remember { MutableInteractionSource() }
                val pressed by interaction.collectIsPressedAsState()

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (pressed) PixelSurfaceHigh else PixelSurface,
                    shape = RoundedCornerShape(21.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                onClick = { }
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppAvatar(call.initials, 54.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(call.name, color = PixelText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                if (call.missed) "Пропущенный звонок" else "Исходящий звонок",
                                color = if (call.missed) PixelDanger else PixelMuted,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(call.time, color = PixelMuted, fontSize = 11.sp)
                        }
                        Text("☎", color = PixelBlueBright, fontSize = 21.sp)
                    }
                }
            }
        }
    }
}
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



@Composable
private fun ProfileInfoRow(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(title, color = PixelMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, color = PixelText, fontSize = 14.sp)
    }
}

@Composable
private fun ProfileAction(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        color = PixelSurface,
        shape = RoundedCornerShape(21.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(43.dp).clip(RoundedCornerShape(15.dp)).background(PixelBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("•", color = PixelBlueBright, fontSize = 24.sp)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = PixelText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 22.sp)
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
    var animations by rememberSaveable { mutableStateOf(true) }
    var previews by rememberSaveable { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("‹", color = PixelBlueBright, fontSize = 30.sp)
            }
            Column {
                Text("Настройки", color = PixelText, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                Text("PIXEL NIGHT", color = PixelMuted, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(7.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            item {
                Text("Интерфейс", color = PixelBlueBright, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
            }
            item { SettingSwitch("Анимации", "Плавные переходы экранов", animations) { animations = it } }
            item { SettingSwitch("Компактный режим", "Меньше отступов в списках", compact) { compact = it } }

            item {
                Text("Уведомления", color = PixelBlueBright, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
            }
            item { SettingSwitch("Новые сообщения", "Показывать уведомления чатов", notifications) { notifications = it } }
            item { SettingSwitch("Предпросмотр", "Показывать текст сообщения", previews) { previews = it } }

            item {
                Text("Аккаунт и безопасность", color = PixelBlueBright, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
            }
            item { SettingAction("Конфиденциальность", "Блокировки, онлайн-статус и права") }
            item { SettingAction("Безопасность", "Сессии, ключи и шифрование") }
            item { SettingAction("Устройства", "Активные подключения PIXEL CHAT") }
            item { SettingAction("О PIXEL CHAT", "Material 3 Expressive • Server 10.0") }

            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PixelDanger)
                ) {
                    Text("Выйти из аккаунта", fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(Modifier.height(18.dp)) }
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
        modifier = Modifier.fillMaxWidth(),
        color = PixelSurface,
        shape = RoundedCornerShape(19.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
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
        modifier = Modifier.fillMaxWidth(),
        color = PixelSurface,
        shape = RoundedCornerShape(19.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(13.dp), color = PixelBlueContainer) {
                Text("•", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = PixelBlueBright, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = PixelText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = PixelMuted, fontSize = 12.sp)
            }
            Text("›", color = PixelBlueBright, fontSize = 22.sp)
        }
    }
}
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
            "Как дела?"
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



@Composable
private fun NewChatScreen(
    onBack: () -> Unit,
    onSelect: (ChatItem) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val contacts = remember {
        listOf(
            ContactItem("Алекс", "@alex_dev", "A", true),
            ContactItem("Дима", "@dimas", "D"),
            ContactItem("Марк", "@markpixel", "M"),
            ContactItem("София", "@sofia_dev", "S", true)
        )
    }
    val filtered = contacts.filter { it.name.contains(query, true) || it.username.contains(query, true) }

    Column(Modifier.fillMaxSize().statusBarsPadding().padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹", color = PixelBlueBright, fontSize = 30.sp) }
            Column {
                Text("Новый чат", color = PixelText, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Text("Выбери контакт", color = PixelMuted, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(7.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск контакта") },
            leadingIcon = { Text("⌕", color = PixelMuted, fontSize = 23.sp) },
            shape = RoundedCornerShape(21.dp)
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(filtered, key = { it.username }) { contact ->
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
        color = PixelBlueContainer,
        tonalElevation = 2.dp
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

// PIXEL CHAT MASTER UI: design layer updated by PIXEL_CHAT_MASTER_UI.sh
