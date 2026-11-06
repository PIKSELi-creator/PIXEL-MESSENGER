package com.pixelchat.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen(
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        "←",
                        color = Color.White,
                        fontSize = 27.sp
                    )
                }

                Text(
                    "Регистрация",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trimStart() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Gmail") },
                placeholder = { Text("name@gmail.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF777777),
                    unfocusedBorderColor = Color(0xFF444444),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color(0xFFAAAAAA),
                    cursorColor = Color.White
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { codeScreen = true },
                enabled = email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFF555555)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF171717),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF111111),
                    disabledContentColor = Color(0xFF777777)
                )
            ) {
                Text(
                    "Получить код",
                    fontWeight = FontWeight.Bold
                )
            }
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
                        shape = CircleShape
                    ) {
                        Text("Подтвердить", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = {}) { Text("Отправить код повторно") }
                }
            }
        }
    }
}
