package com.pixelchat.ui.screens.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelchat.ui.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ───────── Палитра (сняты с референса) ─────────
private val BgTop = Color(0xFFB3B5BA)
private val BgMid = Color(0xFFABADB2)
private val BgBottom = Color(0xFFA6A8AE)
private val Turquoise = Color(0xFF19E6E6)
private val TurquoiseLight = Color(0xFF8DFBF6)
private val ParticleDark = Color(0xFF5E6168)
private val SloganColor = Color(0xFFF4F6F8)
private val FooterColor = Color(0xFF5F6269)
private val FooterLine = Color(0xFF7C7F86)

/**
 * true  — pixel_bubble.png уже содержит бирюзовые кубики вокруг пузыря (как на референсе);
 * false — PNG содержит только пузырь, кубики дорисуются в Canvas.
 */
private const val LOGO_HAS_CUBES = true
private const val BUBBLE_SHARE = 0.70f // доля ширины PNG, занятая пузырём (только при LOGO_HAS_CUBES = false)

// ───────── Частицы: координаты нормализованы по референсу 941×1671 ─────────
private class Seed(val x: Float, val y: Float, val r: Float, val light: Boolean, val a: Float = 1f)

private class Particle(
    val x: Float, val y: Float, val r: Float, val light: Boolean, val a: Float,
    val phase: Float, val speed: Int, val drift: Float,
)

private val Particles: List<Particle> = listOf(
    Seed(0.850f, 0.127f, 7f, true),
    Seed(0.168f, 0.142f, 4f, true, 0.6f),
    Seed(0.184f, 0.165f, 8f, false),
    Seed(0.815f, 0.163f, 11f, false),
    Seed(0.081f, 0.285f, 9f, true, 0.7f),
    Seed(0.932f, 0.257f, 10f, true, 0.8f),
    Seed(0.047f, 0.355f, 7f, false),
    Seed(0.209f, 0.415f, 5f, true, 0.6f),
    Seed(0.936f, 0.454f, 9f, true, 0.85f),
    Seed(0.038f, 0.516f, 6f, true),
    Seed(0.120f, 0.660f, 5f, false),
    Seed(0.163f, 0.677f, 7f, true),
    Seed(0.791f, 0.690f, 6f, true),
    Seed(0.351f, 0.716f, 5f, false),
    Seed(0.865f, 0.654f, 11f, false),
    Seed(0.912f, 0.768f, 8f, false),
    Seed(0.053f, 0.892f, 7f, true),
    Seed(0.102f, 0.918f, 8f, false),
    Seed(0.892f, 0.943f, 4f, false),
    Seed(0.916f, 0.959f, 6f, true),
).mapIndexed { i, s ->
    Particle(
        x = s.x, y = s.y, r = s.r / 941f, light = s.light, a = s.a,
        phase = (i * 0.173f) % 1f,
        speed = 1 + i % 2,
        drift = 0.004f + (i % 4) * 0.0015f,
    )
}

@Composable
private fun Dp.asSp(): TextUnit = with(LocalDensity.current) { this@asSp.toSp() }

// ───────── Экран ─────────
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom)))
            .drawBehind {
                // мягкий светлый «блик» за логотипом
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height * 0.33f),
                        radius = size.width * 0.85f,
                    )
                )
            }
    ) {
        val w = maxWidth
        val h = maxHeight
        val logoSize = w * 0.52f

        ParticleField(Modifier.fillMaxSize())

        GlowingLogo(
            logoSize = logoSize,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = h * 0.311f - logoSize / 2),
        )

        PixelChatTitle(
            w = w,
            modifier = Modifier.align(Alignment.TopCenter).offset(y = h * 0.464f),
        )

        Text(
            text = "Общайся. Звони. Отправляй подарки.",
            color = SloganColor,
            style = TextStyle(
                fontSize = (w * 0.038f).asSp(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                shadow = Shadow(Color(0x33000000), Offset(0f, 3f), 6f),
            ),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.align(Alignment.TopCenter).offset(y = h * 0.549f),
        )

        GlassStartButton(
            w = w,
            onClick = onStart,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = h * 0.775f)
                .width(w * 0.807f)
                .height(w * 0.17f),
        )

        FooterCaption(
            w = w,
            modifier = Modifier.align(Alignment.TopCenter).offset(y = h * 0.9f),
        )
    }
}

// ───────── Частицы с лёгким дрейфом и мерцанием ─────────
@Composable
private fun ParticleField(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "particles")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14_000, easing = LinearEasing)),
        label = "time",
    )
    Canvas(modifier) {
        val tau = (2 * PI).toFloat()
        Particles.forEach { p ->
            // time читается только в фазе рисования → без рекомпозиций
            val angle = (time * p.speed + p.phase) * tau
            val cx = p.x * size.width + sin(angle) * 3.dp.toPx()
            val cy = p.y * size.height + cos(angle) * p.drift * size.height
            val radius = p.r * size.width
            val twinkle = 0.78f + 0.22f * sin(angle * 2f)
            if (p.light) {
                drawCircle(Color.White.copy(alpha = 0.22f * p.a * twinkle), radius * 2.4f, Offset(cx, cy))
                drawCircle(Color.White.copy(alpha = 0.95f * p.a * twinkle), radius, Offset(cx, cy))
            } else {
                drawCircle(ParticleDark.copy(alpha = 0.85f * p.a), radius, Offset(cx, cy))
            }
        }
    }
}

// ───────── Логотип: PNG + мягкое пульсирующее свечение ─────────
@Composable
private fun GlowingLogo(logoSize: Dp, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "logo")
    val pulse by transition.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse",
    )
    val floatY by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float",
    )
    Box(
        modifier = modifier
            .size(logoSize)
            .graphicsLayer { translationY = floatY * 4.dp.toPx() }
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Turquoise.copy(alpha = pulse), Color.Transparent),
                        center = center,
                        radius = size.minDimension * 0.75f,
                    ),
                    radius = size.minDimension * 0.75f,
                    center = center,
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.pixel_bubble),
            contentDescription = "PIXEL CHAT",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
        if (!LOGO_HAS_CUBES) LogoCubes(Modifier.fillMaxSize())
    }
}

// dx, dy — смещение центра от центра пузыря в диаметрах пузыря; s — сторона; rot — наклон
private class Cube(val dx: Float, val dy: Float, val s: Float, val rot: Float)

private val Cubes = listOf(
    Cube(-0.46f, -0.46f, 0.25f, -12f),
    Cube(-0.66f, -0.24f, 0.16f, 14f),
    Cube(-0.60f, -0.04f, 0.09f, -8f),
    Cube(0.62f, -0.03f, 0.10f, 12f),
    Cube(0.575f, 0.30f, 0.22f, -14f),
    Cube(0.37f, 0.46f, 0.12f, 10f),
)

@Composable
private fun LogoCubes(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val d = size.minDimension * BUBBLE_SHARE
        Cubes.forEach { c ->
            val side = d * c.s
            val mid = Offset(center.x + c.dx * d, center.y + c.dy * d)
            val topLeft = Offset(mid.x - side / 2, mid.y - side / 2)
            val corner = CornerRadius(side * 0.24f)
            rotate(c.rot, pivot = mid) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        listOf(TurquoiseLight, Turquoise, Color(0xFF0FB5B9)),
                        start = topLeft,
                        end = topLeft + Offset(side, side),
                    ),
                    topLeft = topLeft, size = Size(side, side), cornerRadius = corner,
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.55f),
                    topLeft = topLeft, size = Size(side, side), cornerRadius = corner,
                    style = Stroke(width = side * 0.06f),
                )
            }
        }
    }
}

// ───────── PIXEL (белый) + CHAT (бирюзовый градиент) ─────────
@Composable
private fun PixelChatTitle(w: Dp, modifier: Modifier = Modifier) {
    val fontSize = (w * 0.108f).asSp()
    val base = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = fontSize,
        lineHeight = fontSize * 1.2f,
        shadow = Shadow(Color(0x40000000), Offset(0f, 6f), 14f),
    )
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "PIXEL", style = base.copy(color = Color.White), maxLines = 1, softWrap = false)
        Spacer(Modifier.width(w * 0.022f))
        Text(
            text = "CHAT",
            style = base.copy(brush = Brush.horizontalGradient(listOf(TurquoiseLight, Turquoise))),
            maxLines = 1,
            softWrap = false,
        )
    }
}

// ───────── Тёмная стеклянная кнопка ─────────
@Composable
private fun GlassStartButton(w: Dp, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "press")

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(14.dp, shape, ambientColor = Color(0x66000000), spotColor = Color(0x55000000))
            .drawBehind {
                // лёгкое светлое свечение вокруг: несколько расходящихся контуров
                val step = 3.dp.toPx()
                for (i in 1..4) {
                    val grow = step * i
                    drawRoundRect(
                        color = Color(0xFFCFFBFF).copy(alpha = 0.16f / i),
                        topLeft = Offset(-grow, -grow),
                        size = Size(size.width + grow * 2, size.height + grow * 2),
                        cornerRadius = CornerRadius(size.height / 2 + grow),
                        style = Stroke(width = step),
                    )
                }
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFF7B808A),
                    0.5f to Color(0xFF535863),
                    1f to Color(0xFF444955),
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.45f))
                ),
                shape = shape,
            )
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Начать",
            color = Color.White,
            style = TextStyle(fontSize = (w * 0.056f).asSp(), fontWeight = FontWeight.SemiBold),
        )
        Canvas(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = w * 0.085f)
                .size(w * 0.05f)
        ) {
            val sw = 2.5.dp.toPx()
            drawLine(Color.White, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), sw, StrokeCap.Round)
            drawLine(Color.White, Offset(size.width * 0.55f, size.height * 0.1f), Offset(size.width, size.height / 2), sw, StrokeCap.Round)
            drawLine(Color.White, Offset(size.width * 0.55f, size.height * 0.9f), Offset(size.width, size.height / 2), sw, StrokeCap.Round)
        }
    }
}

// ───────── Нижняя подпись с линиями ─────────
@Composable
private fun FooterCaption(w: Dp, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Box(Modifier.width(w * 0.053f).height(1.dp).background(FooterLine))
        Spacer(Modifier.width(w * 0.035f))
        Text(
            text = "Общение начинается здесь",
            color = FooterColor,
            style = TextStyle(fontSize = (w * 0.029f).asSp(), letterSpacing = 0.3.sp),
            maxLines = 1,
            softWrap = false,
        )
        Spacer(Modifier.width(w * 0.035f))
        Box(Modifier.width(w * 0.053f).height(1.dp).background(FooterLine))
    }
}