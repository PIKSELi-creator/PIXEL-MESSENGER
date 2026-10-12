
package com.pixelchat.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PixelNeon = Color(0xFF16F5D0)
private val PixelWhite = Color(0xFFF5FFFF)

@Composable
fun PixelChatSplashLogo(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pixel_logo")

    val pulse = transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )

    val glow = transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubble_glow"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .scale(pulse.value),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(54.dp)) {
                val bubble = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.18f)
                    quadraticTo(
                        size.width * 0.08f, size.height * 0.18f,
                        size.width * 0.08f, size.height * 0.34f
                    )
                    lineTo(size.width * 0.08f, size.height * 0.62f)
                    quadraticTo(
                        size.width * 0.08f, size.height * 0.78f,
                        size.width * 0.25f, size.height * 0.78f
                    )
                    lineTo(size.width * 0.25f, size.height * 0.92f)
                    lineTo(size.width * 0.43f, size.height * 0.78f)
                    lineTo(size.width * 0.74f, size.height * 0.78f)
                    quadraticTo(
                        size.width * 0.92f, size.height * 0.78f,
                        size.width * 0.92f, size.height * 0.60f
                    )
                    lineTo(size.width * 0.92f, size.height * 0.34f)
                    quadraticTo(
                        size.width * 0.92f, size.height * 0.18f,
                        size.width * 0.74f, size.height * 0.18f
                    )
                    close()
                }

                // Мягкий внешний ореол
                drawPath(
                    path = bubble,
                    color = PixelNeon.copy(alpha = glow.value * 0.22f),
                    style = Stroke(
                        width = 12.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Основной контур чат-баббла
                drawPath(
                    path = bubble,
                    color = PixelNeon,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Три точки внутри пузыря
                for (x in listOf(0.32f, 0.50f, 0.68f)) {
                    drawCircle(
                        color = PixelWhite,
                        radius = 2.3.dp.toPx(),
                        center = Offset(
                            size.width * x,
                            size.height * 0.48f
                        )
                    )
                }
            }
        }

        Text(
            text = "PIXEL",
            fontSize = 39.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1.5).sp,
            color = PixelWhite,
            maxLines = 1
        )
    }
}
