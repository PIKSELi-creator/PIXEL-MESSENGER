package com.pixelchat.ui.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/** PIXEL CHAT's dark Material 3 design tokens. */
private val PixelBackground = Color(0xFF090B12)
private val PixelSurface = Color(0xFF121622)
private val PixelSurfaceVariant = Color(0xFF202638)
private val PixelPrimary = Color(0xFF9DB2FF)
private val PixelSecondary = Color(0xFF7DE3D1)
private val PixelOnBackground = Color(0xFFF1F3FA)
private val PixelOnSurfaceVariant = Color(0xFFB9C0D3)
private val PixelError = Color(0xFFFFB4AB)

private val PixelChatDarkColorScheme = darkColorScheme(
    primary = PixelPrimary,
    onPrimary = Color(0xFF17234A),
    secondary = PixelSecondary,
    onSecondary = Color(0xFF00382F),
    background = PixelBackground,
    onBackground = PixelOnBackground,
    surface = PixelSurface,
    onSurface = PixelOnBackground,
    surfaceVariant = PixelSurfaceVariant,
    onSurfaceVariant = PixelOnSurfaceVariant,
    error = PixelError,
    outline = Color(0xFF454C60)
)

private val PixelChatShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

/** Use around a screen's content. Existing MainActivity is deliberately not replaced. */
@Composable
fun PixelChatDesignTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PixelChatDarkColorScheme,
        typography = Typography(),
        shapes = PixelChatShapes,
        content = content
    )
}
