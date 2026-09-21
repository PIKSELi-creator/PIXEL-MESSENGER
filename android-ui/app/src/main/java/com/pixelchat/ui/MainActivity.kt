package com.pixelchat.ui
import androidx.compose.ui.platform.LocalContext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val PixelBlue = Color(0xFF35A9FF)
private val PixelBlueDark = Color(0xFF16334A)

private val PixelBackground = Color(0xFF081017)
private val PixelSurface = Color(0xFF101B24)
private val PixelSurface2 = Color(0xFF14222D)

private val PixelText = Color(0xFFF4F8FB)
private val PixelMuted = Color(0xFF93A2AF)
private val PixelDivider = Color(0xFF21313C)

private data class ChatItem(
    val name: String,
    val message: String,
    val time: String,
    val initials: String,
    val unread: Int = 0
)

private data class ContactItem(
    val name: String,
    val username: String,
    val initials: String
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PixelChatTheme()
        }
    }
}

@Composable
fun PixelChatTheme() {

    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = PixelBlue,
            onPrimary = Color.White,
            background = PixelBackground,
            onBackground = PixelText,
            surface = PixelSurface,
            onSurface = PixelText,
            surfaceVariant = PixelSurface2,
            onSurfaceVariant = PixelMuted,
            outline = PixelDivider
        )
    ) {
        PixelChatApp()
    }
}

@Composable
fun PixelChatApp() {

    val context = LocalContext.current
    val sessionStore = remember { SessionStore(context) }

    var screen by remember { mutableStateOf("splash") }
    var sessionLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1100)

        val session = sessionStore.getSession()

        screen = if (session != null) {
            "home"
        } else {
            "welcome"
        }

        sessionLoaded = true
    }

    if (!sessionLoaded) {
        SplashScreen()
        return
    }

    when (screen) {

        "welcome" -> {
            WelcomeScreen(
                onLogin = {
                    screen = "login"
                },
                onRegister = {
                    screen = "register"
                }
            )
        }

        "login" -> {
            LoginScreen(
                onBack = {
                    screen = "welcome"
                },
                onRegister = {
                    screen = "register"
                },
                onLogin = {
                    screen = "home"
                }
            )
        }

        "register" -> {
            RegisterScreen(
                onBack = {
                    screen = "welcome"
                },
                onVerified = {
                    screen = "profile"
                }
            )
        }

        "profile" -> {
            ProfileSetupScreen(
                onComplete = {
                    screen = "home"
                }
            )
        }

        "home" -> {
            HomeScreen()
        }
    }
}

@Composable
fun SplashScreen() {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            PixelLogo(124.dp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PIXEL CHAT",
                color = PixelText,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Общайся. Быстро.",
                color = PixelMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun PixelLogo(
    size: Dp = 96.dp
) {

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(size * 0.72f)
                .clip(RoundedCornerShape(size * 0.22f))
                .border(
                    width = 2.dp,
                    color = PixelBlue,
                    shape = RoundedCornerShape(size * 0.22f)
                )
        )

        PixelBlock(
            blockSize = 15.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-1).dp)
        )

        PixelBlock(
            blockSize = 13.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = (-8).dp,
                    y = 11.dp
                )
        )

        PixelBlock(
            blockSize = 13.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(
                    x = (-8).dp,
                    y = (-10).dp
                )
        )

        PixelBlock(
            blockSize = 13.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-1).dp)
        )

        PixelBlock(
            blockSize = 13.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(
                    x = 8.dp,
                    y = (-10).dp
                )
        )

        PixelBlock(
            blockSize = 13.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = 8.dp,
                    y = 11.dp
                )
        )

        Text(
            text = "P",
            color = PixelText,
            fontSize = (size.value * 0.42f).sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun PixelBlock(
    blockSize: Dp,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .size(blockSize)
            .clip(RoundedCornerShape(4.dp))
            .background(PixelBlue)
    )
}

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 28.dp,
                    vertical = 34.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            PixelLogo(118.dp)

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "PIXEL CHAT",
                color = PixelText,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Современный мессенджер\nдля общения без лишнего.",
                color = PixelMuted,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "Войти",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "Создать аккаунт",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthShell(
        title = "С возвращением",
        subtitle = "Войдите в свой PIXEL CHAT",
        onBack = onBack
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Gmail")
            },
            placeholder = {
                Text("name@gmail.com")
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Пароль")
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text = "Войти",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {}
        ) {
            Text("Забыли пароль?")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Нет аккаунта?",
            color = PixelMuted
        )

        Spacer(modifier = Modifier.height(3.dp))

        TextButton(
            onClick = onRegister
        ) {
            Text("Создать аккаунт")
        }
    }
}

@Composable
private fun AuthShell(
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
                .fillMaxSize()
                .padding(24.dp)
        ) {

            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 8.dp)
            ) {

                Text(
                    text = "← Назад",
                    color = PixelBlue
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            PixelLogo(72.dp)

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = title,
                color = PixelText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = subtitle,
                color = PixelMuted,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            content()
        }
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onVerified: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var codeVisible by remember { mutableStateOf(false) }

    if (codeVisible) {

        VerificationScreen(
            email = email,
            onBack = {
                codeVisible = false
            },
            onVerified = onVerified
        )

        return
    }

    AuthShell(
        title = "Создать аккаунт",
        subtitle = "Начните с вашего Gmail",
        onBack = onBack
    ) {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Gmail")
            },
            placeholder = {
                Text("name@gmail.com")
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PixelSurface
            ),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Подтверждение почты",
                    color = PixelText,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "На Gmail будет отправлен 6-значный код подтверждения.",
                    color = PixelMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (email.isNotBlank()) {
                    codeVisible = true
                }
            },
            enabled = email.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {

            Text(
                text = "Получить код",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun VerificationScreen(
    email: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {

    var code by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "‹ Изменить Gmail",
                    color = PixelBlue
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PixelBlueDark),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✉",
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Введите код",
                color = PixelText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Код подтверждения будет отправлен на",
                color = PixelMuted
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                color = PixelBlue,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = code,
                onValueChange = {
                    if (
                        it.length <= 6 &&
                        it.all { char -> char.isDigit() }
                    ) {
                        code = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("6-значный код")
                },
                placeholder = {
                    Text("123456")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onVerified,
                enabled = code.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {

                Text(
                    text = "Продолжить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            TextButton(
                onClick = {}
            ) {
                Text("Отправить код повторно")
            }
        }
    }
}

@Composable
fun ProfileSetupScreen(
    onComplete: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(45.dp))

            PixelLogo(102.dp)

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Настройте профиль",
                color = PixelText,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Эти данные будут видны вашим контактам.",
                color = PixelMuted,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Имя")
                },
                placeholder = {
                    Text("Ваше имя")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it.replace(" ", "")
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Username")
                },
                placeholder = {
                    Text("@pixeluser")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onComplete,
                enabled = name.isNotBlank() && username.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {

                Text(
                    text = "Готово",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HomeScreen() {

    var tab by remember { mutableStateOf(0) }
    var openedChat by remember { mutableStateOf<ChatItem?>(null) }
    var showNewChat by remember { mutableStateOf(false) }

    val chats = remember {

        listOf(
            ChatItem(
                name = "PIXEL CHAT",
                message = "Добро пожаловать в мессенджер",
                time = "19:42",
                initials = "P"
            ),
            ChatItem(
                name = "Алекс",
                message = "Увидимся позже",
                time = "18:27",
                initials = "A",
                unread = 2
            ),
            ChatItem(
                name = "Дима",
                message = "Смотри, что я нашёл",
                time = "17:51",
                initials = "D"
            ),
            ChatItem(
                name = "Game Dev",
                message = "Новый билд уже готов",
                time = "16:05",
                initials = "G",
                unread = 4
            )
        )
    }

    if (openedChat != null) {

        ChatScreen(
            chat = openedChat!!,
            onBack = {
                openedChat = null
            }
        )

        return
    }

    if (showNewChat) {

        NewChatScreen(
            onBack = {
                showNewChat = false
            },
            onSelect = {
                openedChat = it
                showNewChat = false
            }
        )

        return
    }

    Scaffold(
        containerColor = PixelBackground,

        floatingActionButton = {

            if (tab == 0 || tab == 1) {

                FloatingActionButton(
                    onClick = {
                        showNewChat = true
                    },
                    containerColor = PixelBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {

                    Text(
                        text = "+",
                        fontSize = 28.sp
                    )
                }
            }
        },

        bottomBar = {

            PixelBottomNavigation(
                selected = tab,
                onSelected = {
                    tab = it
                }
            )
        }

    ) { innerPadding ->

        when (tab) {

            0 -> ChatsTab(
                modifier = Modifier.padding(innerPadding),
                chats = chats,
                onOpen = {
                    openedChat = it
                }
            )

            1 -> ContactsTab(
                modifier = Modifier.padding(innerPadding),
                onOpen = {
                    openedChat = it
                }
            )

            2 -> ProfileTab(
                modifier = Modifier.padding(innerPadding)
            )

            else -> SettingsTab(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun ChatsTab(
    modifier: Modifier,
    chats: List<ChatItem>,
    onOpen: (ChatItem) -> Unit
) {

    var query by remember { mutableStateOf("") }

    val filtered = chats.filter {

        it.name.contains(
            query,
            ignoreCase = true
        ) || it.message.contains(
            query,
            ignoreCase = true
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Чаты",
            color = PixelText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        SearchField(
            query = query,
            onQueryChange = {
                query = it
            },
            placeholder = "Поиск чатов"
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {

            EmptyState(
                title = "Ничего не найдено",
                subtitle = "Попробуйте другой запрос"
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                items(filtered) { chat ->

                    ChatRow(
                        chat = chat,
                        onClick = {
                            onOpen(chat)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactsTab(
    modifier: Modifier,
    onOpen: (ChatItem) -> Unit
) {

    var query by remember { mutableStateOf("") }

    val contacts = remember {

        listOf(
            ContactItem(
                "Алекс",
                "@alex_dev",
                "A"
            ),
            ContactItem(
                "Дима",
                "@dimas",
                "D"
            ),
            ContactItem(
                "Марк",
                "@markpixel",
                "M"
            ),
            ContactItem(
                "София",
                "@sofia_dev",
                "S"
            )
        )
    }

    val filtered = contacts.filter {

        it.name.contains(
            query,
            ignoreCase = true
        ) || it.username.contains(
            query,
            ignoreCase = true
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Контакты",
            color = PixelText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        SearchField(
            query = query,
            onQueryChange = {
                query = it
            },
            placeholder = "Поиск людей"
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                                name = contact.name,
                                message = "Новый чат",
                                time = "сейчас",
                                initials = contact.initials
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileTab(
    modifier: Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Профиль",
            color = PixelText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PixelSurface
            ),
            shape = RoundedCornerShape(22.dp)
        ) {

            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AppAvatar(
                    initials = "P",
                    size = 70.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {

                    Text(
                        text = "Pixel User",
                        color = PixelText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "@pixeluser",
                        color = PixelBlue,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Я в PIXEL CHAT",
                        color = PixelMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileAction(
            title = "Изменить профиль",
            subtitle = "Имя, username и аватар"
        )

        ProfileAction(
            title = "QR-профиль",
            subtitle = "Быстрый обмен профилями"
        )

        ProfileAction(
            title = "Активные сессии",
            subtitle = "Устройства, где открыт аккаунт"
        )
    }
}

@Composable
private fun ProfileAction(
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PixelSurface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PixelBlueDark),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "•",
                    color = PixelBlue,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {

                Text(
                    text = title,
                    color = PixelText,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = PixelMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsTab(
    modifier: Modifier
) {

    var notifications by remember {
        mutableStateOf(true)
    }

    var compactMode by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Настройки",
            color = PixelText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        SettingsSwitch(
            title = "Уведомления",
            subtitle = "Новые сообщения и ответы",
            checked = notifications,
            onCheckedChange = {
                notifications = it
            }
        )

        SettingsSwitch(
            title = "Компактный режим",
            subtitle = "Меньше отступов в списках",
            checked = compactMode,
            onCheckedChange = {
                compactMode = it
            }
        )

        SettingsAction(
            title = "Тема",
            subtitle = "Тёмная"
        )

        SettingsAction(
            title = "Конфиденциальность",
            subtitle = "Настройки аккаунта и блокировок"
        )

        SettingsAction(
            title = "О PIXEL CHAT",
            subtitle = "Версия 0.1.0"
        )
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PixelSurface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = PixelText,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = PixelMuted,
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingsAction(
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PixelSurface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                color = PixelText,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                color = PixelMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Text(
                text = "⌕",
                color = PixelMuted,
                fontSize = 24.sp
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = PixelMuted
            )
        },
        shape = RoundedCornerShape(17.dp)
    )
}

@Composable
private fun ChatRow(
    chat: ChatItem,
    onClick: () -> Unit
) {

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 10.dp,
                    vertical = 11.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AppAvatar(
                initials = chat.initials,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(13.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = chat.name,
                    color = PixelText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chat.message,
                    color = PixelMuted,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = chat.time,
                    color = PixelMuted,
                    fontSize = 11.sp
                )

                if (chat.unread > 0) {

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(PixelBlue),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = chat.unread.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 80.dp)
                .background(PixelDivider)
        )
    }
}

@Composable
private fun ContactRow(
    contact: ContactItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = 10.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AppAvatar(
            initials = contact.initials,
            size = 54.dp
        )

        Spacer(modifier = Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = contact.name,
                color = PixelText,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = contact.username,
                color = PixelMuted,
                fontSize = 13.sp
            )
        }

        Text(
            text = "Чат",
            color = PixelBlue,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AppAvatar(
    initials: String,
    size: Dp
) {

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(PixelBlueDark),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = initials.take(2),
            color = PixelBlue,
            fontSize = (size.value * 0.30f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyState(
    title: String,
    subtitle: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PixelLogo(72.dp)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            color = PixelText,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            color = PixelMuted,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun PixelBottomNavigation(
    selected: Int,
    onSelected: (Int) -> Unit
) {

    NavigationBar(
        containerColor = PixelSurface
    ) {

        val items = listOf(
            "Чаты" to "✉",
            "Контакты" to "●",
            "Профиль" to "P",
            "Настройки" to "⚙"
        )

        items.forEachIndexed { index, item ->

            NavigationBarItem(
                selected = selected == index,

                onClick = {
                    onSelected(index)
                },

                icon = {

                    Text(
                        text = item.second,
                        color = if (
                            selected == index
                        ) {
                            PixelBlue
                        } else {
                            PixelMuted
                        }
                    )
                },

                label = {
                    Text(item.first)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(
    chat: ChatItem,
    onBack: () -> Unit
) {

    var input by remember {
        mutableStateOf("")
    }

    var messages by remember {

        mutableStateOf(
            listOf(
                "Добро пожаловать в PIXEL CHAT",
                "Этот чат пока работает локально."
            )
        )
    }

    Scaffold(
        containerColor = PixelBackground,

        topBar = {

            CenterAlignedTopAppBar(

                title = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AppAvatar(
                            initials = chat.initials,
                            size = 38.dp
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Column {

                            Text(
                                text = chat.name,
                                color = PixelText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "был недавно",
                                color = PixelMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                },

                navigationIcon = {

                    TextButton(
                        onClick = onBack
                    ) {

                        Text(
                            text = "‹",
                            fontSize = 34.sp,
                            color = PixelBlue
                        )
                    }
                },

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PixelBackground
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(messages) { message ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Surface(
                            color = PixelBlueDark,
                            shape = RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = 18.dp,
                                bottomEnd = 5.dp
                            )
                        ) {

                            Text(
                                text = message,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 10.dp
                                ),
                                color = PixelText,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Сообщение")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                FloatingActionButton(
                    onClick = {

                        if (input.isNotBlank()) {

                            messages =
                                messages + input

                            input = ""
                        }
                    },

                    modifier = Modifier.size(52.dp),

                    containerColor = PixelBlue,
                    contentColor = Color.White
                ) {

                    Text(
                        text = "➤",
                        fontSize = 20.sp
                    )
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
            ContactItem(
                "Алекс",
                "@alex_dev",
                "A"
            ),
            ContactItem(
                "Дима",
                "@dimas",
                "D"
            ),
            ContactItem(
                "Марк",
                "@markpixel",
                "M"
            ),
            ContactItem(
                "София",
                "@sofia_dev",
                "S"
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PixelBackground
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = onBack
                ) {

                    Text(
                        text = "‹",
                        fontSize = 34.sp,
                        color = PixelBlue
                    )
                }

                Text(
                    text = "Новый чат",
                    color = PixelText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            SearchField(
                query = "",
                onQueryChange = {},
                placeholder = "Поиск контакта"
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                items(contacts) { contact ->

                    ContactRow(
                        contact = contact,
                        onClick = {

                            onSelect(
                                ChatItem(
                                    name = contact.name,
                                    message = "Новый чат",
                                    time = "сейчас",
                                    initials = contact.initials
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
