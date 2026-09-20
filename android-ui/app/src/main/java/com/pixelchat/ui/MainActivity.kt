package com.pixelchat.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.sp

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
            primary = Color(0xFF35A9FF),
            background = Color(0xFF0B1117),
            surface = Color(0xFF121A22)
        )
    ) {
        PixelChatApp()
    }
}

@Composable
fun PixelChatApp() {

    var screen by remember { mutableStateOf("welcome") }

    when (screen) {

        "welcome" -> {
            WelcomeScreen(
                onStart = {
                    screen = "login"
                }
            )
        }

        "login" -> {
            LoginScreen(
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
                    screen = "login"
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
fun WelcomeScreen(
    onStart: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B1117)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Surface(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(30.dp),
                color = Color(0xFF35A9FF)
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "P",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PIXEL CHAT",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Общайся. Создавай. Будь на связи.",
                color = Color(0xFF9BA8B5),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(38.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Начать",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B1117)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "С возвращением",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Войдите в свой PIXEL CHAT",
                color = Color(0xFF9BA8B5),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(35.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Email")
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

            Spacer(modifier = Modifier.height(15.dp))

            HorizontalDivider(
                color = Color(0xFF26313C)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Нет аккаунта?",
                color = Color(0xFF9BA8B5)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Создать аккаунт")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onVerified: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var codeScreen by remember { mutableStateOf(false) }

    if (codeScreen) {

        VerificationScreen(
            email = email,
            onBack = {
                codeScreen = false
            },
            onVerified = onVerified
        )

        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B1117)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            Spacer(modifier = Modifier.height(35.dp))

            TextButton(
                onClick = onBack
            ) {
                Text("← Назад")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Создать аккаунт",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Начнём с вашего Gmail",
                color = Color(0xFF9BA8B5),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        codeScreen = true
                    }
                },
                enabled = email.isNotBlank(),
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
        color = Color(0xFF0B1117)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(55.dp))

            Text(
                text = "Подтверждение",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Код подтверждения отправлен на",
                color = Color(0xFF9BA8B5),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = email,
                color = Color(0xFF35A9FF),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(35.dp))

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
                    Text("Код из письма")
                },
                placeholder = {
                    Text("123456")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onVerified()
                },
                enabled = code.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Подтвердить",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {}
            ) {
                Text("Отправить код повторно")
            }

            TextButton(
                onClick = onBack
            ) {
                Text("Изменить Gmail")
            }
        }
    }
}

@Composable
fun ProfileSetupScreen(
    onComplete: () -> Unit
) {

    var username by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B1117)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(55.dp))

            Text(
                text = "Ваш профиль",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Настройте профиль PIXEL CHAT",
                color = Color(0xFF9BA8B5)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color(0xFF35A9FF)
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "P",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Имя пользователя")
                },
                placeholder = {
                    Text("Например, PixelUser")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onComplete,
                enabled = username.isNotBlank(),
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

    var selectedTab by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B1117)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = when (selectedTab) {
                        0 -> "Чаты"
                        1 -> "Контакты"
                        2 -> "Профиль"
                        else -> "Настройки"
                    },
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 0) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF121A22)
                    ) {

                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier = Modifier.size(52.dp),
                                shape = CircleShape,
                                color = Color(0xFF35A9FF)
                            ) {

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "P",
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {

                                Text(
                                    text = "PIXEL CHAT",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Добро пожаловать!",
                                    color = Color(0xFF9BA8B5),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = Color(0xFF26313C)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                NavigationButton(
                    text = "Чаты",
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                    }
                )

                NavigationButton(
                    text = "Контакты",
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                    }
                )

                NavigationButton(
                    text = "Профиль",
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                    }
                )

                NavigationButton(
                    text = "Настройки",
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                    }
                )
            }
        }
    }
}

@Composable
fun NavigationButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    TextButton(
        onClick = onClick
    ) {

        Text(
            text = text,
            color = if (selected) {
                Color(0xFF35A9FF)
            } else {
                Color(0xFF9BA8B5)
            },
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )
    }
}
