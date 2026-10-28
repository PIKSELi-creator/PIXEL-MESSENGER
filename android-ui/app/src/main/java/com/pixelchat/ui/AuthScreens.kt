package com.pixelchat.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Material 3 onboarding screens: start, login, register, code, profile setup.
 *
 * Needs res/drawable-nodpi/pixel_logo.png (the neon bubble logo).
 * Names start with "Pixel" so they do not clash with the private screens
 * that still live in MainActivity.kt.
 */

@Composable
internal fun PixelBrandLogo(size: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.pixel_logo),
        contentDescription = "PIXEL CHAT",
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f)),
        contentScale = ContentScale.Crop
    )
}

/** Black Material 3 button used on all onboarding screens. */
@Composable
private fun PixelBlackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = { if (!loading) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/* ------------------------------------------------------------------ */
/*  Start                                                             */
/* ------------------------------------------------------------------ */

@Composable
internal fun PixelWelcomeScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            PixelBrandLogo(size = 168.dp)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "PIXEL CHAT",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Общайся. Звони. Отправляй подарки.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            PixelBlackButton(
                text = "Регистрация",
                onClick = onRegister,
                icon = Icons.Outlined.PersonAdd
            )

            Spacer(Modifier.height(12.dp))

            PixelBlackButton(
                text = "Вход",
                onClick = onLogin,
                icon = Icons.Outlined.Person
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Для создания аккаунта понадобится только email",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PixelAuthScaffold(
    onBack: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            content = content
        )
    }
}

/* ------------------------------------------------------------------ */
/*  Login (UI only: the server login is connected in a later stage)   */
/* ------------------------------------------------------------------ */

@Composable
internal fun PixelLoginScreen(
    onBack: () -> Unit,
    onRegister: () -> Unit
) {
    var identity by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showNotice by remember { mutableStateOf(false) }

    val canLogin = identity.isNotBlank() && password.isNotBlank()

    BackHandler(onBack = onBack)

    PixelAuthScaffold(onBack = onBack) {
        PixelBrandLogo(size = 64.dp)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Вход",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "С возвращением в PIXEL CHAT.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = identity,
            onValueChange = { identity = it.replace(" ", "") },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email или username") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Пароль") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = if (showPassword) "Скрыть пароль" else "Показать пароль"
                    )
                }
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (canLogin) showNotice = true }
            )
        )

        Spacer(Modifier.height(24.dp))

        PixelBlackButton(
            text = "Войти",
            onClick = { showNotice = true },
            enabled = canLogin
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Нет аккаунта? Регистрация")
        }
    }

    if (showNotice) {
        AlertDialog(
            onDismissRequest = { showNotice = false },
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            title = { Text("Вход пока недоступен") },
            text = {
                Text("Вход через сервер подключим на следующем этапе. Сейчас можно создать аккаунт.")
            },
            confirmButton = {
                TextButton(onClick = { showNotice = false }) { Text("Понятно") }
            }
        )
    }
}

/* ------------------------------------------------------------------ */
/*  Register + code                                                   */
/* ------------------------------------------------------------------ */

@Composable
internal fun PixelRegisterScreen(
    onBack: () -> Unit,
    onVerified: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var codeStep by rememberSaveable { mutableStateOf(false) }

    if (codeStep) {
        PixelVerificationScreen(
            email = email,
            onBack = { codeStep = false },
            onVerified = { onVerified(email) }
        )
        return
    }

    BackHandler(onBack = onBack)

    PixelAuthScaffold(onBack = onBack) {
        PixelBrandLogo(size = 72.dp)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Создать аккаунт",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Подтвердим Gmail одноразовым кодом.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.replace(" ", "") },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Gmail") },
            placeholder = { Text("name@gmail.com") },
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null)
            },
            supportingText = {
                Text("Код будет отправлен на указанную почту сервером PIXEL CHAT.")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (email.isNotBlank()) codeStep = true }
            )
        )

        Spacer(Modifier.height(20.dp))

        PixelBlackButton(
            text = "Получить код",
            onClick = { codeStep = true },
            enabled = email.isNotBlank()
        )
    }
}

@Composable
internal fun PixelVerificationScreen(
    email: String,
    onBack: () -> Unit,
    onVerified: () -> Unit,
    onResend: () -> Unit = {}
) {
    var code by rememberSaveable { mutableStateOf("") }
    var secondsLeft by rememberSaveable { mutableStateOf(30) }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
    }

    BackHandler(onBack = onBack)

    PixelAuthScaffold(onBack = onBack) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Введите код",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Код отправлен на",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = email,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 6 && it.all(Char::isDigit)) code = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("6-значный код") },
            textStyle = MaterialTheme.typography.headlineSmall.copy(letterSpacing = 6.sp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (code.length == 6) onVerified() }
            )
        )

        Spacer(Modifier.height(24.dp))

        PixelBlackButton(
            text = "Подтвердить",
            onClick = onVerified,
            enabled = code.length == 6
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                onResend()
                secondsLeft = 30
            },
            enabled = secondsLeft == 0,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                if (secondsLeft > 0) {
                    "Отправить повторно через $secondsLeft с"
                } else {
                    "Отправить код повторно"
                }
            )
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Изменить Gmail")
        }
    }
}

/* ------------------------------------------------------------------ */
/*  Profile setup                                                     */
/* ------------------------------------------------------------------ */

@Composable
internal fun PixelProfileSetupScreen(
    email: String,
    onComplete: suspend (String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    PixelAuthScaffold(onBack = null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Последний шаг",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Настройте профиль",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Имя и username будут видны контактам.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Surface(
                modifier = Modifier.size(104.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val initial = name.trim().firstOrNull()?.uppercase()
                    if (initial != null) {
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Имя") },
                placeholder = { Text("Pixel User") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it.removePrefix("@").replace(" ", "") },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                prefix = { Text("@") },
                placeholder = { Text("pixeluser") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))

            PixelBlackButton(
                text = "Открыть PIXEL CHAT",
                onClick = {
                    saving = true
                    scope.launch {
                        try {
                            onComplete(name.trim(), username.trim())
                        } finally {
                            saving = false
                        }
                    }
                },
                enabled = name.isNotBlank() && username.isNotBlank(),
                loading = saving
            )
        }
    }
}
