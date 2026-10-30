package com.pixelchat.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.pixelchat.ui.PixelLogo

@Composable
fun AuthCard(
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
fun AuthField(
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
