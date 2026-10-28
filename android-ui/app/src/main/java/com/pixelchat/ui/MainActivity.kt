package com.pixelchat.ui

import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.Close

import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.IconButton

import android.net.Uri
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import android.widget.VideoView
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
internal data class ChatItem(
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
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val colorScheme = if (android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.S
    ) {
        if (isDark) {
            androidx.compose.material3.dynamicDarkColorScheme(context)
        } else {
            androidx.compose.material3.dynamicLightColorScheme(context)
        }
    } else {
        if (isDark) darkColorScheme() else lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes()
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
    var savedSession by remember { mutableStateOf<PixelSession?>(null) }

    LaunchedEffect(Unit) {
        delay(700)

        savedSession = sessionStore.getSession()
        sessionLoaded = true
        screen = if (savedSession != null) "home" else "welcome"
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
            "welcome" -> PixelWelcomeScreen(
                onRegister = { screen = "register" },
                onLogin = { screen = "login" }
            )

            "login" -> PixelLoginScreen(
                onBack = { screen = "welcome" },
                onRegister = { screen = "register" }
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
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pixel_bubble),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.24f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
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
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )
        Box(
            modifier = Modifier
                .size(size * 0.44f)
                .clip(RoundedCornerShape(size * 0.16f))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "P",
                color = MaterialTheme.colorScheme.onSurface,
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
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
        )
        Box(
            modifier = Modifier
                .size(size * 0.12f)
                .align(Alignment.BottomEnd)
                .offset(-size * 0.17f, -size * 0.13f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Общайся. Звони. Отправляй подарки.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 5.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Твой новый чат",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "PIXEL CHAT объединяет сообщения, контакты, звонки и профиль в одном приложении.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Text(
                        """• Чаты и контакты
• Голосовые звонки
• Профиль и подарки
• Защита сообщений""",
                        color = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(18.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("‹", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 30.sp)
            }
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(30.dp),
                tonalElevation = 4.dp
            ) {
                Column(Modifier.padding(20.dp)) {
                    PixelLogo(68.dp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(15.dp)) {
                Text(
                    "Подтверждение email",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Код будет отправлен на указанную почту сервером PIXEL CHAT.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.statusBarsPadding().padding(18.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("‹ Изменить Gmail", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(30.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✉", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 27.sp)
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Введите код",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text("Код отправлен на", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(email, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
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
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Text("Последний шаг", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.height(5.dp))
            Text(
                "Настройте профиль",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Имя и username будут видны контактам.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.size(94.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("P", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 34.sp, fontWeight = FontWeight.Black)
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
            Text(email, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
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
    val context = LocalContext.current
    val sessionStore = remember { SessionStore(context) }
    val scope = rememberCoroutineScope()

    var tab by rememberSaveable { mutableStateOf(0) }
    var openedChat by remember { mutableStateOf<ChatItem?>(null) }
    var showNewChat by remember { mutableStateOf(false) }

    var showEditProfile by remember { mutableStateOf(false) }
    var profileName by rememberSaveable { mutableStateOf("Pixel User") }
    var profileUsername by rememberSaveable { mutableStateOf("pixeluser") }
    var profileBio by rememberSaveable { mutableStateOf("Я в PIXEL CHAT") }
    var profileBirthDate by rememberSaveable { mutableStateOf("") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var profileBackgroundUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        sessionStore.getSession()?.let { session ->
            profileName = session.displayName
            profileUsername = session.username
            profileBio = session.bio
            profileBirthDate = session.birthDate
            profilePhotoUri = session.photoUri
                .takeIf { it.isNotBlank() }
                ?.let(Uri::parse)

            profileBackgroundUri = session.backgroundUri
                .takeIf { it.isNotBlank() }
                ?.let(Uri::parse)
        }
    }

    fun saveProfileData(
        photoUri: String = profilePhotoUri?.toString().orEmpty(),
        backgroundUri: String = profileBackgroundUri?.toString().orEmpty()
    ) {
        scope.launch {
            sessionStore.getSession()?.let { session ->
                sessionStore.saveSession(
                    email = session.email,
                    displayName = profileName,
                    username = profileUsername,
                    bio = profileBio,
                    birthDate = profileBirthDate,
                    photoUri = photoUri,
                    backgroundUri = backgroundUri
                )
            }
        }
    }

    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profilePhotoUri = uri
            saveProfileData(photoUri = uri.toString())
        }
    }

    val profileBackgroundPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }

            profileBackgroundUri = uri
            saveProfileData(backgroundUri = uri.toString())
        }
    }

    val profileBackgroundVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }

            profileBackgroundUri = uri
            saveProfileData(backgroundUri = uri.toString())
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
            birthDate = profileBirthDate,
            photoUri = profilePhotoUri,
            onPickPhoto = { profilePhotoPicker.launch("image/*") },
            onBack = { showEditProfile = false },
            onSave = { newName, newUsername, newBio, newBirthDate ->
                profileName = newName
                profileUsername = newUsername
                profileBio = newBio
                profileBirthDate = newBirthDate
                showEditProfile = false
                saveProfileData()
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
                    modifier = Modifier.padding(bottom = 84.dp),
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
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = tab,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = 100.dp
                ),
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
                    birthDate = profileBirthDate,
                    photoUri = profilePhotoUri,
                    backgroundUri = profileBackgroundUri,
                    onPickPhoto = { profilePhotoPicker.launch("image/*") },
                    onPickBackgroundPhoto = {
                        profileBackgroundPhotoPicker.launch(arrayOf("image/*"))
                    },
                    onPickBackgroundVideo = {
                        profileBackgroundVideoPicker.launch(arrayOf("video/*"))
                    },
                    onBack = { tab = 0 },
                    onEdit = { showEditProfile = true }
                )
            }
        }

        PixelChatNavigation(
            selected = tab,
            onSelected = { tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
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
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        "Чаты" to Icons.Outlined.Chat,
        "Контакты" to Icons.Outlined.Contacts,
        "Настройки" to Icons.Outlined.Settings,
        "Профиль" to Icons.Outlined.Person
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 4.dp,
        shadowElevation = 5.dp
    ) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selected == index,
                onClick = { if (selected != index) onSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first
                    )
                },
                label = { Text(item.first, maxLines = 1) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
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

    ListItem(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
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
        colors = ListItemDefaults.colors(
            containerColor = if (pressed) {
                MaterialTheme.colorScheme.surfaceContainerHigh
            } else {
                Color.Transparent
            },
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
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
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 2.dp
        ) {
            Column(Modifier.padding(17.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Контакты", color = MaterialTheme.colorScheme.onSurface, fontSize = 31.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(3.dp))
                        Text("${contacts.size} контактов • $onlineCount в сети", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                        Icon(
    imageVector = Icons.Outlined.Person,
    contentDescription = "Контакт",
    modifier = Modifier.padding(12.dp),
    tint = MaterialTheme.colorScheme.onSecondaryContainer
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
            leadingIcon = { Text("⌕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 23.sp) },
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
        color = if (pressed) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
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
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(contact.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    if (contact.online) "в сети • ${contact.username}" else contact.username,
                    color = if (contact.online) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                Text(
                    "Чат",
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
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
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Звонки", color = MaterialTheme.colorScheme.onSurface, fontSize = 31.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(3.dp))
                    Text("История голосовых и видеозвонков", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                    Image(
                        painter = painterResource(id = R.drawable.nav_calls),
                        contentDescription = "Звонки",
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(22.dp)
        ) {
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("CALL", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Сигналинг и WebRTC подключим к серверу позже.",
                    color = MaterialTheme.colorScheme.onSurface,
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
                    color = if (pressed) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
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
                            Text(call.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                if (call.missed) "Пропущенный звонок" else "Исходящий звонок",
                                color = if (call.missed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(call.time, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                        Image(
                            painter = painterResource(id = R.drawable.nav_calls),
                            contentDescription = "Позвонить",
                            modifier = Modifier.size(24.dp)
                        )
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
    onSave: (String, String, String, String) -> Unit
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
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {
                    onSave(
                        editedName.trim(),
                        editedUsername.trim(),
                        editedBio.trim(),
                        editedBirthDate.trim()
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
            color = MaterialTheme.colorScheme.surfaceContainer,
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
    birthDate: String,
    photoUri: Uri?,
    backgroundUri: Uri?,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickBackgroundVideo: () -> Unit,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    var selectedMusicUri by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedMusicName by rememberSaveable { mutableStateOf<String?>(null) }
    var musicPlaying by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var infoDialogTitle by rememberSaveable { mutableStateOf<String?>(null) }

    val musicStore = remember(context) { SessionStore(context) }
    val musicScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val session = musicStore.getSession()
        if (session != null) {
            if (selectedMusicUri == null && session.musicUri.isNotBlank()) {
                selectedMusicUri = session.musicUri
            }
            if (selectedMusicName == null && session.musicName.isNotBlank()) {
                selectedMusicName = session.musicName
            }
        }
    }

    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }

            val name = context.contentResolver.query(
                uri,
                arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            } ?: "Музыка профиля"

            selectedMusicUri = uri.toString()
            selectedMusicName = name
            musicPlaying = false

            musicScope.launch {
                musicStore.saveMusic(uri.toString(), name)
            }
        }
    }

    val mediaPlayer = remember(selectedMusicUri) {
        selectedMusicUri?.let { MediaPlayer.create(context, Uri.parse(it)) }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            try {
                mediaPlayer?.release()
            } catch (_: Exception) {
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111521)),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Column {
                // Cover photo/video, with the controls kept above the media layer.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(286.dp)
                ) {
                    if (backgroundUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF303B67),
                                            Color(0xFF202638),
                                            Color(0xFF111521)
                                        )
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1.35f)
                                .height(150.dp)
                                .align(Alignment.Center)
                                .rotate(-18f)
                                .background(
                                    Color(0xFF829CFF).copy(alpha = 0.14f),
                                    RoundedCornerShape(44.dp)
                                )
                        )
                    } else {
                        val mime = remember(backgroundUri) {
                            context.contentResolver.getType(backgroundUri)
                        }
                        if (mime?.startsWith("video/") == true) {
                            AndroidView(
                                factory = { ctx ->
                                    VideoView(ctx).apply {
                                        setVideoURI(backgroundUri)
                                        setOnPreparedListener { player ->
                                            player.isLooping = true
                                            player.setVolume(0f, 0f)
                                            start()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AsyncImage(
                                model = backgroundUri,
                                contentDescription = "Обложка профиля",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.40f),
                                        androidx.compose.ui.graphics.Color.Transparent,
                                        Color(0xFF111521).copy(alpha = 0.94f)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF252C40).copy(alpha = 0.72f))
                        ) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад")
                        }

                        Spacer(Modifier.weight(1f))

                        IconButton(
                            onClick = { infoDialogTitle = "Видеозвонок" },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF252C40).copy(alpha = 0.72f))
                        ) {
                            Icon(Icons.Outlined.Videocam, contentDescription = "Видеозвонок")
                        }

                        Spacer(Modifier.width(8.dp))

                        Box {
                            IconButton(
                                onClick = { menuExpanded = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF252C40).copy(alpha = 0.72f))
                            ) {
                                Icon(Icons.Outlined.MoreVert, contentDescription = "Меню профиля")
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Изменить аватар") },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Person, contentDescription = null)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onPickPhoto()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Обложка — фото") },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onPickBackgroundPhoto()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Обложка — видео") },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Movie, contentDescription = null)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onPickBackgroundVideo()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Редактировать профиль") },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Edit, contentDescription = null)
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onEdit()
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(y = 52.dp)
                            .padding(end = 24.dp)
                            .size(122.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF252C40))
                                .border(
                                    width = 3.dp,
                                    color = Color(0xFF829CFF).copy(alpha = 0.75f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AppAvatar(
                                initials = name.trim().firstOrNull()?.uppercase() ?: "P",
                                size = 116.dp,
                                photoUri = photoUri
                            )
                        }

                        IconButton(
                            onClick = onPickPhoto,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF829CFF))
                        ) {
                            Icon(
                                Icons.Outlined.PhotoCamera,
                                contentDescription = "Изменить аватар",
                                tint = Color(0xFFFFFFFF)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // The name stays to the left of the avatar, as in the approved mockup.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 38.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name.ifBlank { "PIXEL" },
                            modifier = Modifier.weight(1f, fill = false),
                            color = Color(0xFFF0F2FA),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF829CFF),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                color = Color(0xFF829CFF),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }

                    Text(
                        text = "@${username.removePrefix("@").ifBlank { "pixelchat" }}",
                        color = Color(0xFFA5AEC7),
                        fontSize = 17.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
                    )

                    // Music card follows the profile name and username.
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF202638),
                        shape = RoundedCornerShape(28.dp),
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                color = Color(0xFF829CFF)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.MusicNote,
                                        contentDescription = null,
                                        tint = Color(0xFFFFFFFF),
                                        modifier = Modifier.size(31.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { musicPicker.launch(arrayOf("audio/*")) }
                            ) {
                                Text(
                                    "Любимая музыка",
                                    color = Color(0xFFF0F2FA),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    selectedMusicName ?: "Добавить любимый трек",
                                    color = Color(0xFFA5AEC7),
                                    fontSize = 14.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (selectedMusicUri == null) {
                                        musicPicker.launch(arrayOf("audio/*"))
                                    } else if (musicPlaying) {
                                        mediaPlayer?.pause()
                                        musicPlaying = false
                                    } else {
                                        try {
                                            mediaPlayer?.start()
                                            musicPlaying = mediaPlayer != null
                                        } catch (_: Exception) {
                                            infoDialogTitle = "Не удалось воспроизвести трек"
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = when {
                                        selectedMusicUri == null -> Icons.Outlined.Add
                                        musicPlaying -> Icons.Outlined.Pause
                                        else -> Icons.Outlined.PlayArrow
                                    },
                                    contentDescription = if (musicPlaying) "Пауза" else "Воспроизвести",
                                    tint = Color(0xFF829CFF),
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Icon(
                                Icons.Outlined.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFFA5AEC7),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    PixelProfileDetail(
                        icon = Icons.Outlined.CalendarToday,
                        title = "Дата рождения",
                        value = birthDate.ifBlank { "Не указана" }
                    )

                    Spacer(Modifier.height(18.dp))

                    PixelProfileDetail(
                        icon = Icons.Outlined.Person,
                        title = "О себе",
                        value = bio.ifBlank { "О себе пока ничего не указано" }
                    )

                    Spacer(Modifier.height(28.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF1D2435),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                            PixelProfileOptionRow(
                                icon = Icons.Outlined.PhotoLibrary,
                                title = "Фотографии и видео",
                                trailing = "›",
                                onClick = { infoDialogTitle = "Фотографии и видео" }
                            )
                            HorizontalDivider(color = Color(0xFF343C50).copy(alpha = 0.55f))
                            PixelProfileOptionRow(
                                icon = Icons.Outlined.Link,
                                title = "Ссылки",
                                trailing = "›",
                                onClick = { infoDialogTitle = "Ссылки" }
                            )
                            HorizontalDivider(color = Color(0xFF343C50).copy(alpha = 0.55f))
                            PixelProfileOptionRow(
                                icon = Icons.Outlined.Lock,
                                title = "Конфиденциальность",
                                trailing = "›",
                                onClick = { infoDialogTitle = "Конфиденциальность" }
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick = onEdit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        shape = RoundedCornerShape(31.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Редактировать профиль",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    val dialogTitle = infoDialogTitle
    if (dialogTitle != null) {
        val dialogMessage = when (dialogTitle) {
            "Видеозвонок" -> "Интерфейс профиля готов. Видеозвонки подключим отдельным этапом."
            "Фотографии и видео" -> "Медиатека профиля будет подключена отдельно. Фото и видео обложки уже можно менять через меню сверху."
            "Ссылки" -> "Добавление и отображение ссылок подключим отдельным этапом."
            "Конфиденциальность" -> "Настройки видимости и приватности подключим отдельным этапом."
            else -> "Не удалось воспроизвести выбранный трек. Попробуй выбрать другой аудиофайл."
        }
        AlertDialog(
            onDismissRequest = { infoDialogTitle = null },
            title = { Text(dialogTitle) },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = { infoDialogTitle = null }) {
                    Text("Понятно")
                }
            }
        )
    }
}

@Composable
private fun PixelProfileDetail(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(23.dp)
                )
            }
        }

        Spacer(Modifier.width(18.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun PixelProfileOptionRow(
    icon: ImageVector,
    title: String,
    trailing: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(Modifier.width(15.dp))
        Text(
            title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
        Text(
            trailing,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 28.sp
        )
    }
}
@Composable
private fun PixelProfileAction(
    modifier: Modifier,
    icon: ImageVector,
    title: String
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 17.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(29.dp)
            )

            Spacer(Modifier.height(9.dp))

            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PixelProfileInfo(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(31.dp)
                .padding(top = 2.dp)
        )

        Spacer(Modifier.width(22.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(7.dp))

            Text(
                value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                lineHeight = 23.sp
            )
        }

        Text(
            "›",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            fontSize = 30.sp
        )
    }
}

@Composable
private fun PixelDivider() {
    Spacer(Modifier.height(17.dp))

    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )

    Spacer(Modifier.height(17.dp))
}

@Composable
private fun ProfileActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    title: String
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 6.dp,
                vertical = 13.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(23.dp)
                )
            } else {
                Text(
                    "☎",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(7.dp))

            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}


@Composable
private fun ProfileInfoRow(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
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
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(21.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(43.dp).clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("•", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 24.sp)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text("›", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 22.sp)
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
                Text("‹", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 30.sp)
            }
            Column {
                Text("Настройки", color = MaterialTheme.colorScheme.onSurface, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                Text("PIXEL NIGHT", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(7.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            item {
                Text("Интерфейс", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
            }
            item { SettingSwitch("Анимации", "Плавные переходы экранов", animations) { animations = it } }
            item { SettingSwitch("Компактный режим", "Меньше отступов в списках", compact) { compact = it } }

            item {
                Text("Уведомления", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
            }
            item { SettingSwitch("Новые сообщения", "Показывать уведомления чатов", notifications) { notifications = it } }
            item { SettingSwitch("Предпросмотр", "Показывать текст сообщения", previews) { previews = it } }

            item {
                Text("Аккаунт и безопасность", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
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
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(19.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingAction(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(19.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(13.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                Text("•", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
            Text("›", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 22.sp)
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
    var showChatMenu by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showNotice by remember { mutableStateOf<String?>(null) }

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }

                    AppAvatar(initials = chat.initials, size = 42.dp)
                    Spacer(Modifier.width(10.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = chat.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (chat.online) "в сети" else "был недавно",
                            color = if (chat.online) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { showNotice = "Звонки пока не подключены" }
                    ) {
                        Icon(Icons.Outlined.Call, contentDescription = "Позвонить")
                    }

                    Box {
                        IconButton(onClick = { showChatMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Меню чата")
                        }
                        DropdownMenu(
                            expanded = showChatMenu,
                            onDismissRequest = { showChatMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Поиск в чате") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                },
                                onClick = {
                                    showChatMenu = false
                                    showNotice = "Поиск по сообщениям пока не подключён"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Уведомления") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.NotificationsOff, contentDescription = null)
                                },
                                onClick = {
                                    showChatMenu = false
                                    showNotice = "Настройка уведомлений пока не подключена"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Очистить историю") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Delete, contentDescription = null)
                                },
                                onClick = {
                                    showChatMenu = false
                                    messages.clear()
                                    showNotice = "Локальная история очищена"
                                }
                            )
                        }
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
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(count = messages.size, key = { index -> index }) { index ->
                    PixelChatMessage(
                        text = messages[index],
                        outgoing = true
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(
                            onClick = { showAttachmentMenu = true },
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Вложения",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(
                            expanded = showAttachmentMenu,
                            onDismissRequest = { showAttachmentMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Фото") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Image, contentDescription = null)
                                },
                                onClick = {
                                    showAttachmentMenu = false
                                    showNotice = "Прикрепление фото пока не подключено"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Видео") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Videocam, contentDescription = null)
                                },
                                onClick = {
                                    showAttachmentMenu = false
                                    showNotice = "Прикрепление видео пока не подключено"
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Файл") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.AttachFile, contentDescription = null)
                                },
                                onClick = {
                                    showAttachmentMenu = false
                                    showNotice = "Прикрепление файлов пока не подключено"
                                }
                            )
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Сообщение",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            val message = input.trim()
                            if (message.isNotEmpty()) {
                                messages.add(message)
                                input = ""
                                scope.launch {
                                    if (messages.isNotEmpty()) {
                                        listState.animateScrollToItem(messages.lastIndex)
                                    }
                                }
                            }
                        },
                        enabled = input.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (input.isNotBlank()) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Отправить",
                            tint = if (input.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    showNotice?.let { notice ->
        AlertDialog(
            onDismissRequest = { showNotice = null },
            confirmButton = {
                TextButton(onClick = { showNotice = null }) {
                    Text("Понятно")
                }
            },
            text = { Text(notice) }
        )
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
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "сейчас",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            TextButton(onClick = onBack) { Text("‹", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 30.sp) }
            Column {
                Text("Новый чат", color = MaterialTheme.colorScheme.onSurface, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Text("Выбери контакт", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
        }

        Spacer(Modifier.height(7.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Поиск контакта") },
            leadingIcon = { Text("⌕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 23.sp) },
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
        color = MaterialTheme.colorScheme.secondaryContainer,
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
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = (size.value * 0.29f).sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// PIXEL CHAT MASTER UI: design layer updated by PIXEL_CHAT_MASTER_UI.sh
