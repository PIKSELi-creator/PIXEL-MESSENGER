package com.pixelchat.ui
import androidx.compose.material.icons.outlined.Close

import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.IconButton

import android.net.Uri
import android.media.MediaPlayer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
private val PixelBlue = Color(0xFF000000)
private val PixelBlueBright = Color(0xFFFFFFFF)
private val PixelBlueContainer = Color(0xFF2A2A2A)

private val PixelBackground = Color(0xFF000000)
private val PixelSurface = Color(0xFF151515)
private val PixelSurfaceHigh = Color(0xFF202020)
private val PixelOutline = Color(0xFF666666)

private val PixelText = Color(0xFFFFFFFF)
private val PixelMuted = Color(0xFFAAAAAA)
private val PixelSuccess = Color(0xFFFFFFFF)
private val PixelDanger = Color(0xFFFFFFFF)

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
    val darkScheme = darkColorScheme(
        primary = Color.White,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF2A2A2A),
        onPrimaryContainer = Color.White,
        secondary = Color(0xFFD0D0D0),
        onSecondary = Color.Black,
        secondaryContainer = Color(0xFF303030),
        onSecondaryContainer = Color.White,
        tertiary = Color(0xFFBDBDBD),
        onTertiary = Color.Black,
        tertiaryContainer = Color(0xFF383838),
        onTertiaryContainer = Color.White,
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF202020),
        onSurfaceVariant = Color(0xFFBDBDBD),
        surfaceContainer = Color(0xFF151515),
        surfaceContainerHigh = Color(0xFF202020),
        surfaceContainerHighest = Color(0xFF292929),
        outline = Color(0xFF666666)
    )

    val lightScheme = lightColorScheme(
        primary = Color.Black,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE5E5E5),
        onPrimaryContainer = Color.Black,
        secondary = Color(0xFF444444),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE8E8E8),
        onSecondaryContainer = Color.Black,
        tertiary = Color(0xFF555555),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE0E0E0),
        onTertiaryContainer = Color.Black,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFE8E8E8),
        onSurfaceVariant = Color(0xFF555555),
        surfaceContainer = Color(0xFFF4F4F4),
        surfaceContainerHigh = Color(0xFFECECEC),
        surfaceContainerHighest = Color(0xFFE2E2E2),
        outline = Color(0xFF777777)
    )

    val isDark = isSystemInDarkTheme()

    val scheme = if (isDark) darkScheme else lightScheme

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(18.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(32.dp)
    )

    MaterialTheme(
        colorScheme = scheme,
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

    var showEditProfile by remember { mutableStateOf(false) }
    var profileName by rememberSaveable { mutableStateOf("Pixel User") }
    var profileUsername by rememberSaveable { mutableStateOf("pixeluser") }
    var profileBio by rememberSaveable { mutableStateOf("Я в PIXEL CHAT") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }

    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profilePhotoUri = uri
        }
    }

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

    if (showEditProfile) {
        EditProfileScreen(
            name = profileName,
            username = profileUsername,
            bio = profileBio,
            birthDate = "",
            photoUri = profilePhotoUri,
            onPickPhoto = { profilePhotoPicker.launch("image/*") },
            onBack = { showEditProfile = false },
            onSave = { newName, newUsername, newBio ->
                profileName = newName
                profileUsername = newUsername
                profileBio = newBio
                showEditProfile = false
            }
        )
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            AnimatedVisibility(
                visible = tab == 0,
                enter = fadeIn() + scaleIn(animationSpec = tween(300)),
                exit = fadeOut() + scaleOut(animationSpec = tween(300))
            ) {
                SmallFloatingActionButton(
                    onClick = { showNewChat = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Создать"
                    )
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
                fadeIn(animationSpec = tween(250)) +
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { it / 10 }
                    ) togetherWith
                    fadeOut(animationSpec = tween(180)) +
                    slideOutHorizontally(
                        animationSpec = tween(180),
                        targetOffsetX = { -it / 14 }
                    )
            },
            label = "pixel_main_tab"
        ) { current ->
            when (current) {
                0 -> ChatsTab(chats = chats, onOpen = { openedChat = it })
                1 -> ContactsTab(onOpen = { openedChat = it })
                2 -> SettingsTab(onLogout = onLogout)
                else -> ProfileTab(
                    name = profileName,
                    username = profileUsername,
                    bio = profileBio,
                    photoUri = profilePhotoUri,
                    onPickPhoto = { profilePhotoPicker.launch("image/*") },
                    onEdit = { showEditProfile = true }
                )
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val active = selected == index

                Surface(
                    onClick = {
                        if (selected != index) {
                            onSelected(index)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedContent(
                            targetState = active,
                            transitionSpec = {
                                (fadeIn(tween(160)) + scaleIn(tween(160))) togetherWith
                                    (fadeOut(tween(100)) + scaleOut(tween(100)))
                            },
                            label = "nav_icon_$index"
                        ) { isActive ->
                            Icon(
                                imageVector = item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(
                                    if (isActive) 25.dp else 23.dp
                                ),
                                tint = if (isActive) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }

                        Spacer(Modifier.height(3.dp))

                        Text(
                            text = item.first,
                            color = if (active) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (active) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            }
                        )
                    }
                }
            }
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
    var showAccountMenu by rememberSaveable { mutableStateOf(false) }

    val filtered = chats.filter { chat ->
        val matchesSearch =
            chat.name.contains(query, true) ||
            chat.message.contains(query, true)

        val matchesFilter = when (filter) {
            0 -> true
            1 -> chat.online
            2 -> chat.unread > 0
            else -> true
        }

        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 18.dp,
            bottom = 112.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Чаты",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        "${filtered.size} ${if (filtered.size == 1) "диалог" else "диалога"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    Surface(
                        onClick = { showAccountMenu = true },
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 2.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Добавить аккаунт",
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showAccountMenu,
                        onDismissRequest = { showAccountMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Добавить аккаунт") },
                            onClick = {
                                showAccountMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Переключить аккаунт") },
                            onClick = {
                                showAccountMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Мои аккаунты") },
                            onClick = {
                                showAccountMenu = false
                            }
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                singleLine = true,
                label = { Text("Поиск") },
                placeholder = { Text("Люди, @username, группы, каналы и боты") },
                leadingIcon = {
                    Text(
                        "⌕",
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        TextButton(
                            onClick = { query = "" }
                        ) {
                            Text("Очистить")
                        }
                    }
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor =
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor =
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedBorderColor =
                        MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor =
                        MaterialTheme.colorScheme.outline,
                    focusedTextColor =
                        MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor =
                        MaterialTheme.colorScheme.onSurface,
                    cursorColor =
                        MaterialTheme.colorScheme.primary
                )
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PixelFilterChip(
                    text = "Все",
                    selected = filter == 0
                ) { filter = 0 }

                PixelFilterChip(
                    text = "В сети",
                    selected = filter == 1
                ) { filter = 1 }

                PixelFilterChip(
                    text = "Непрочитанные",
                    selected = filter == 2
                ) { filter = 2 }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp,
                        bottom = 2.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Последние чаты",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Text(
                    "${filtered.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(
            items = filtered,
            key = { "${it.name}_${it.time}" }
        ) { chat ->
            PixelExpressiveChatItem(
                chat = chat,
                onClick = { onOpen(chat) }
            )
        }

        if (filtered.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Ничего не найдено",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "Попробуй изменить запрос или фильтр",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
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
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        },
        shape = if (selected) {
            MaterialTheme.shapes.largeIncreased
        } else {
            MaterialTheme.shapes.large
        },
        modifier = Modifier.animateContentSize()
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
        animationSpec = tween(250),
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
private fun EditProfileScreen(
    name: String,
    username: String,
    bio: String,
    birthDate: String,
    photoUri: Uri?,
    onPickPhoto: () -> Unit,
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var editedName by rememberSaveable { mutableStateOf(name) }
    var editedUsername by rememberSaveable { mutableStateOf(username) }
    var editedBio by rememberSaveable { mutableStateOf(bio) }
    var editedBirthDate by rememberSaveable { mutableStateOf(birthDate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Отмена")
            }

            Text(
                text = "Редактировать профиль",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = PixelText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {
                    onSave(
                        editedName.trim(),
                        editedUsername.trim(),
                        editedBio.trim()
                    )
                },
                enabled = editedName.isNotBlank() && editedUsername.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }

        Spacer(Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PixelSurface,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AppAvatar(
                        initials = editedName
                            .trim()
                            .firstOrNull()
                            ?.uppercase()
                            ?: "P",
                        size = 104.dp,
                        photoUri = photoUri
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onPickPhoto) {
                    Text("Изменить фото")
                }

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Имя") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = editedUsername,
                    onValueChange = {
                        editedUsername = it
                            .removePrefix("@")
                            .replace(" ", "")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    prefix = { Text("@") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = editedBirthDate,
                    onValueChange = { editedBirthDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Дата рождения") },
                    placeholder = { Text("ДД.ММ.ГГГГ") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = editedBio,
                    onValueChange = { editedBio = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("О себе") },
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTab(
    name: String,
    username: String,
    bio: String,
    photoUri: Uri?,
    onPickPhoto: () -> Unit,
    onEdit: () -> Unit
) {
    var musicPanelVisible by rememberSaveable { mutableStateOf(false) }
    var selectedMusicUri by rememberSaveable { mutableStateOf<String?>(null) }
    var musicPlaying by rememberSaveable { mutableStateOf(false) }

    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedMusicUri = uri.toString()
            musicPlaying = false
        }
    }

    val context = LocalContext.current

    val mediaPlayer = remember(selectedMusicUri) {
        selectedMusicUri?.let { uriString ->
            MediaPlayer.create(
                context,
                Uri.parse(uriString)
            )
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    var profileMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFF111111))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                "PIXEL CHAT",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Text(
                                "PROFILE",
                                color = Color(0xFF999999),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.5.sp
                            )
                        }

                        Box {
                            Surface(
                                onClick = {
                                    profileMenuExpanded = true
                                },
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    "•••",
                                    color = Color.White,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            DropdownMenu(
                                expanded = profileMenuExpanded,
                                onDismissRequest = {
                                    profileMenuExpanded = false
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Изменить фото") },
                                    onClick = {
                                        profileMenuExpanded = false
                                        onPickPhoto()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Изменить имя") },
                                    onClick = {
                                        profileMenuExpanded = false
                                        onEdit()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Изменить username") },
                                    onClick = {
                                        profileMenuExpanded = false
                                        onEdit()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Дата рождения") },
                                    onClick = {
                                        profileMenuExpanded = false
                                        onEdit()
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Редактировать профиль") },
                                    onClick = {
                                        profileMenuExpanded = false
                                        onEdit()
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "PIXEL",
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 7.sp
                            )

                            Text(
                                "CHAT PROFILE",
                                color = Color(0xFF8E8E8E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.08f))
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-62).dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    AppAvatar(
                        initials = name
                            .trim()
                            .firstOrNull()
                            ?.uppercase()
                            ?: "P",
                        size = 118.dp,
                        photoUri = photoUri
                    )
                }

                Spacer(Modifier.height((-42).dp))

                Text(
                    text = name.ifBlank { "Pixel User" },
                    color = PixelText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "@${username.ifBlank { "pixeluser" }}",
                    color = PixelMuted,
                    fontSize = 15.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(9.dp),
                        shape = CircleShape,
                        color = PixelText
                    ) {}

                    Spacer(Modifier.width(7.dp))

                    Text(
                        "В сети",
                        color = PixelMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPickPhoto,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(
                            horizontal = 10.dp,
                            vertical = 10.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Фото",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(
                            horizontal = 10.dp,
                            vertical = 10.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Изменить",
                            fontWeight = FontWeight.Bold
                        )
                    }

                }

                Spacer(Modifier.height(14.dp))

                if (bio.isNotBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PixelSurface,
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(17.dp)
                        ) {
                            Text(
                                "О себе",
                                color = PixelMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                bio,
                                color = PixelText,
                                fontSize = 15.sp,
                                lineHeight = 21.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PixelSurface,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 17.dp,
                            vertical = 10.dp
                        )
                    ) {
                        ProfileInfoRow(
                            title = "Телефон",
                            subtitle = "Не указан"
                        )

                        ProfileInfoRow(
                            title = "Имя пользователя",
                            subtitle = "@${username.ifBlank { "pixeluser" }}"
                        )

                        ProfileInfoRow(
                            title = "Дата рождения",
                            subtitle = "Не указана"
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Surface(
                    onClick = { musicPanelVisible = true },
                    modifier = Modifier.fillMaxWidth(),
                    color = PixelSurface,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(PixelBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MusicNote,
                                contentDescription = null,
                                tint = PixelBlueBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(Modifier.width(13.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (selectedMusicUri == null) "Музыка профиля" else "Музыка добавлена",
                                color = PixelText,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(3.dp))

                            Text(
                                if (selectedMusicUri == null) {
                                    "Музыка пока не добавлена"
                                } else {
                                    "Нажми, чтобы открыть плеер"
                                },
                                color = PixelMuted,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            "›",
                            color = PixelBlueBright,
                            fontSize = 22.sp
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
            }
        }


    }

    if (musicPanelVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                musicPanelVisible = false
                musicPlaying = false
                mediaPlayer?.pause()
            },
            containerColor = PixelSurface,
            contentColor = PixelText
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    "Музыка профиля",
                    color = PixelText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF111111)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        contentDescription = null,
                        tint = PixelBlueBright,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    if (selectedMusicUri == null) {
                        "Музыка пока не добавлена"
                    } else {
                        "Выбранный трек"
                    },
                    color = PixelText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            musicPicker.launch("audio/*")
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("＋ Добавить", fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = {
                            mediaPlayer?.let { player ->
                                if (musicPlaying) {
                                    player.pause()
                                    musicPlaying = false
                                } else {
                                    player.start()
                                    musicPlaying = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (musicPlaying) {
                                Icons.Outlined.Pause
                            } else {
                                Icons.Outlined.PlayArrow
                            },
                            contentDescription = null
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))
            }
        }
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
private fun ProfileAction(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
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
            item { SettingAction("О PIXEL CHAT", "Material 3 • Server 10.0") }

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
private fun AppAvatar(
    initials: String,
    size: Dp,
    photoUri: Uri? = null
) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = PixelBlueContainer,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Фото профиля",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    initials.take(2).uppercase(),
                    color = PixelBlueBright,
                    fontSize = (size.value * 0.29f).sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// PIXEL CHAT MASTER UI: design layer updated by PIXEL_CHAT_MASTER_UI.sh
