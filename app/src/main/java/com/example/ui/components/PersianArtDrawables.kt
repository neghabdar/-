package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HeroCatalog
import com.example.ui.theme.AntiqueBrass
import com.example.ui.theme.CreamText
import com.example.ui.theme.DeepPersianBlue
import com.example.ui.theme.InkBrown
import com.example.ui.theme.JadeEmerald
import com.example.ui.theme.LapisBlue
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.ParchmentDark
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseDark
import com.example.ui.theme.PersianTurquoiseLight
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedGoldenSparkles(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sparkles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparklePhase"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val seeds = listOf(
            0.14f to 0.22f,
            0.35f to 0.18f,
            0.64f to 0.28f,
            0.85f to 0.16f,
            0.22f to 0.56f,
            0.78f to 0.62f,
            0.48f to 0.42f,
            0.91f to 0.45f
        )
        seeds.forEachIndexed { idx, (rx, ry) ->
            val driftY = sin(phase + idx * 0.9f) * 14f
            val alpha = 0.25f + 0.35f * (0.5f + 0.5f * cos(phase + idx * 1.1f))
            val radius = (3.5f + (idx % 3) * 1.8f)
            val center = Offset(size.width * rx, size.height * ry + driftY)
            drawCircle(
                color = WarmGoldLight.copy(alpha = alpha),
                radius = radius,
                center = center
            )
        }
    }
}

@Composable
fun PersianGeometricRosette(
    modifier: Modifier = Modifier,
    primaryColor: Color = PersianTurquoise,
    accentColor: Color = SaffronGold
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = minOf(size.width, size.height) * 0.46f

        // Outer 8-pointed Persian star (Girih motif)
        for (layer in 0..1) {
            val rotationOffset = if (layer == 0) 0f else (PI / 4f).toFloat()
            val path = Path()
            for (i in 0 until 4) {
                val angle = rotationOffset + i * (PI / 2f).toFloat()
                val x = cx + r * cos(angle)
                val y = cy + r * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(
                path = path,
                color = if (layer == 0) primaryColor else PersianTurquoiseLight,
                style = Stroke(width = size.minDimension * 0.1f)
            )
            drawPath(
                path = path,
                color = accentColor.copy(alpha = 0.85f),
                style = Stroke(width = size.minDimension * 0.03f)
            )
        }
        drawCircle(
            color = accentColor,
            radius = r * 0.22f,
            center = Offset(cx, cy)
        )
    }
}

@Composable
fun HeroPortraitAvatar(
    characterId: String,
    size: Dp = 56.dp,
    showCrown: Boolean = false,
    frameId: String = "item_frame_turquoise",
    modifier: Modifier = Modifier
) {
    val hero = HeroCatalog.byId(characterId)
    val frameColor = when (frameId) {
        "item_frame_saffron" -> SaffronGold
        "item_frame_lapis" -> LapisBlue
        else -> PersianTurquoiseLight
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Surface(
            shape = CircleShape,
            color = hero.primaryColor.copy(alpha = 0.25f),
            border = BorderStroke(3.dp, Brush.linearGradient(listOf(WarmGoldLight, frameColor, AntiqueBrass))),
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            if (characterId == "hesabgar") {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_hosabgar),
                    contentDescription = hero.name,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                IllustratedHeroCanvas(
                    characterId = characterId,
                    modifier = Modifier.fillMaxSize().padding(3.dp)
                )
            }
        }
        if (showCrown) {
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(size * 0.36f)
            ) {
                val w = this.size.width
                val h = this.size.height
                val crown = Path().apply {
                    moveTo(w * 0.1f, h * 0.78f)
                    lineTo(w * 0.15f, h * 0.22f)
                    lineTo(w * 0.36f, h * 0.5f)
                    lineTo(w * 0.5f, h * 0.12f)
                    lineTo(w * 0.64f, h * 0.5f)
                    lineTo(w * 0.85f, h * 0.22f)
                    lineTo(w * 0.9f, h * 0.78f)
                    close()
                }
                drawPath(crown, color = SaffronGold)
                drawPath(crown, color = InkBrown, style = Stroke(width = 1.6f))
            }
        }
    }
}

@Composable
fun IllustratedHeroCanvas(
    characterId: String,
    modifier: Modifier = Modifier
) {
    val hero = HeroCatalog.byId(characterId)
    val transition = rememberInfiniteTransition(label = "heroBob")
    val bob by transition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f + bob

        // Radiant Persian medallion backdrop
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(hero.secondaryColor.copy(alpha = 0.45f), hero.primaryColor.copy(alpha = 0.9f)),
                center = Offset(cx, cy),
                radius = w * 0.55f
            )
        )

        // Robe / Shoulders
        val robe = Path().apply {
            moveTo(cx - w * 0.34f, h * 0.95f)
            quadraticTo(cx - w * 0.28f, h * 0.56f, cx, h * 0.55f)
            quadraticTo(cx + w * 0.28f, h * 0.56f, cx + w * 0.34f, h * 0.95f)
            close()
        }
        drawPath(robe, color = hero.primaryColor)
        drawPath(robe, color = WarmGoldLight, style = Stroke(width = w * 0.03f))

        // Gold sash
        drawRoundRect(
            color = hero.secondaryColor,
            topLeft = Offset(cx - w * 0.16f, h * 0.72f),
            size = Size(w * 0.32f, h * 0.08f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Head
        val headY = cy - h * 0.08f
        drawCircle(
            color = Color(0xFFF6C89F),
            radius = w * 0.18f,
            center = Offset(cx, headY)
        )

        // Hair / Hat unique to each of the 6 characters
        when (characterId) {
            "karagah" -> {
                // Traditional crimson cap + magnifying glass
                drawArc(
                    color = RubyCrimson,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(cx - w * 0.18f, headY - w * 0.24f),
                    size = Size(w * 0.36f, w * 0.22f)
                )
                drawCircle(
                    color = SaffronGold,
                    radius = w * 0.08f,
                    center = Offset(cx + w * 0.22f, h * 0.62f),
                    style = Stroke(width = w * 0.03f)
                )
            }
            "jadoogar" -> {
                // Tall Persian dervish/scholar turban with turquoise gem
                drawOval(
                    color = ParchmentCream,
                    topLeft = Offset(cx - w * 0.22f, headY - w * 0.28f),
                    size = Size(w * 0.44f, w * 0.2f)
                )
                drawCircle(color = PersianTurquoise, radius = w * 0.045f, center = Offset(cx, headY - w * 0.19f))
            }
            "farmandeh" -> {
                // Helmeted plume / royal Persian crest
                drawArc(
                    color = SaffronGold,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(cx - w * 0.19f, headY - w * 0.24f),
                    size = Size(w * 0.38f, w * 0.22f)
                )
                drawCircle(color = RubyCrimson, radius = w * 0.05f, center = Offset(cx, headY - w * 0.26f))
            }
            "excel" -> {
                // Emerald headband & golden stylus
                drawRoundRect(
                    color = JadeEmerald,
                    topLeft = Offset(cx - w * 0.19f, headY - w * 0.16f),
                    size = Size(w * 0.38f, w * 0.09f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
            "nashnas" -> {
                // Mysterious indigo domino mask & hood
                drawArc(
                    color = DeepPersianBlue,
                    startAngle = 170f,
                    sweepAngle = 200f,
                    useCenter = true,
                    topLeft = Offset(cx - w * 0.23f, headY - w * 0.26f),
                    size = Size(w * 0.46f, w * 0.32f)
                )
                drawRoundRect(
                    color = SaffronGold,
                    topLeft = Offset(cx - w * 0.14f, headY - w * 0.03f),
                    size = Size(w * 0.28f, w * 0.07f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
            else -> {
                // Hesabgar crimson embroidered cap
                drawRoundRect(
                    color = RubyCrimson,
                    topLeft = Offset(cx - w * 0.17f, headY - w * 0.24f),
                    size = Size(w * 0.34f, w * 0.15f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
        }

        // Friendly Eyes & Smile
        drawCircle(color = InkBrown, radius = w * 0.025f, center = Offset(cx - w * 0.06f, headY))
        drawCircle(color = InkBrown, radius = w * 0.025f, center = Offset(cx + w * 0.06f, headY))
        drawArc(
            color = InkBrown,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.07f, headY + w * 0.02f),
            size = Size(w * 0.14f, w * 0.08f),
            style = Stroke(width = w * 0.022f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun FriendlyMonsterArtwork(
    isBoss: Boolean,
    stageNumber: Int,
    isHit: Boolean = false,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "monsterFloat")
    val floatY by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val bodyColor = when {
        stageNumber == 5 -> JadeEmerald // اژدهای مغایرت
        stageNumber == 10 -> RoyalViolet // غول اضافه‌کاری
        isBoss -> RubyCrimson
        else -> PersianTurquoise
    }
    val tint = if (isHit) RubyCrimson.copy(alpha = 0.7f) else bodyColor

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f + floatY

        // Soft shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.22f),
            topLeft = Offset(cx - w * 0.28f, h * 0.85f),
            size = Size(w * 0.56f, h * 0.1f)
        )

        // Cute humorous body
        drawOval(
            brush = Brush.verticalGradient(listOf(tint.copy(alpha = 0.9f), tint)),
            topLeft = Offset(cx - w * 0.3f, cy - h * 0.24f),
            size = Size(w * 0.6f, h * 0.58f)
        )
        drawOval(
            color = SaffronGold,
            topLeft = Offset(cx - w * 0.3f, cy - h * 0.24f),
            size = Size(w * 0.6f, h * 0.58f),
            style = Stroke(width = 4f)
        )

        // Belly scroll/clock badge
        drawCircle(
            color = ParchmentCream,
            radius = w * 0.17f,
            center = Offset(cx, cy + h * 0.08f)
        )

        if (stageNumber == 10) {
            // Clock hands for غول اضافه‌کاری
            drawLine(InkBrown, Offset(cx, cy + h * 0.08f), Offset(cx, cy), strokeWidth = 4f)
            drawLine(RubyCrimson, Offset(cx, cy + h * 0.08f), Offset(cx + w * 0.09f, cy + h * 0.08f), strokeWidth = 3f)
        } else {
            // Horns / ears for dragon or paper guardian
            val leftHorn = Path().apply {
                moveTo(cx - w * 0.22f, cy - h * 0.18f)
                lineTo(cx - w * 0.32f, cy - h * 0.36f)
                lineTo(cx - w * 0.1f, cy - h * 0.22f)
                close()
            }
            val rightHorn = Path().apply {
                moveTo(cx + w * 0.22f, cy - h * 0.18f)
                lineTo(cx + w * 0.32f, cy - h * 0.36f)
                lineTo(cx + w * 0.1f, cy - h * 0.22f)
                close()
            }
            drawPath(leftHorn, color = SaffronGold)
            drawPath(rightHorn, color = SaffronGold)
        }

        // Big expressive cartoon eyes
        drawCircle(Color.White, radius = w * 0.075f, center = Offset(cx - w * 0.11f, cy - h * 0.08f))
        drawCircle(Color.White, radius = w * 0.075f, center = Offset(cx + w * 0.11f, cy - h * 0.08f))
        drawCircle(InkBrown, radius = w * 0.036f, center = Offset(cx - w * 0.1f, cy - h * 0.08f))
        drawCircle(InkBrown, radius = w * 0.036f, center = Offset(cx + w * 0.1f, cy - h * 0.08f))

        // Silly grin
        drawArc(
            color = InkBrown,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.12f, cy - h * 0.01f),
            size = Size(w * 0.24f, h * 0.1f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PersianParchmentCard(
    modifier: Modifier = Modifier,
    headerColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = ParchmentCream,
        border = BorderStroke(2.5.dp, Brush.linearGradient(listOf(WarmGoldLight, ParchmentBorder, AntiqueBrass)))
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(listOf(ParchmentCream, ParchmentDark.copy(alpha = 0.72f)))
            )
        ) {
            if (headerColor != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(width = 120.dp, height = 4.dp)
                        .background(headerColor, RoundedCornerShape(2.dp))
                )
            }
            content()
        }
    }
}

@Composable
fun PersianOrnateBluePanel(
    modifier: Modifier = Modifier,
    topColor: Color = LapisBlue,
    bottomColor: Color = DeepPersianBlue,
    borderColor: Color = SaffronGold,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.shadow(10.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = bottomColor,
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(WarmGoldLight, borderColor, AntiqueBrass)))
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(listOf(topColor.copy(alpha = 0.94f), bottomColor.copy(alpha = 0.96f)))
            ),
            content = content
        )
    }
}

@Composable
fun PersianActionPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = RoyalViolet,
    contentColor: Color = CreamText,
    enabled: Boolean = true,
    leadingContent: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.8.dp, WarmGoldLight),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        modifier = modifier
    ) {
        if (leadingContent != null) {
            leadingContent()
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
