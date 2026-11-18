package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HeroCatalog
import com.example.ui.components.AnimatedGoldenSparkles
import com.example.ui.components.HeroPortraitAvatar
import com.example.ui.components.PersianActionPillButton
import com.example.ui.components.PersianGeometricRosette
import com.example.ui.components.PersianOrnateBluePanel
import com.example.ui.components.PersianParchmentCard
import com.example.ui.theme.CreamText
import com.example.ui.theme.DeepPersianBlue
import com.example.ui.theme.InkBrown
import com.example.ui.theme.JadeEmerald
import com.example.ui.theme.LapisBlue
import com.example.ui.theme.MutedInk
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseLight
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isReady: Boolean,
    onboardingCompleted: Boolean,
    onNavigateNext: (Boolean) -> Unit
) {
    val pulseTransition = rememberInfiniteTransition(label = "splashPulse")
    val scale by pulseTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(isReady) {
        if (isReady) {
            delay(1100L)
            onNavigateNext(onboardingCompleted)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepPersianBlue, LapisBlue, PersianTurquoise.copy(alpha = 0.85f))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        AnimatedGoldenSparkles()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(3.dp, WarmGoldLight),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .size(148.dp)
                    .scale(scale)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_parsminda_emblem),
                    contentDescription = "نشان پارسمیندا",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = "پارسمیندا و قهرمانان تأمین",
                color = SaffronGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "«اینجا هر پرونده یک ماجراست!»",
                color = CreamText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = DeepPersianBlue.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, PersianTurquoiseLight)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.OfflinePin, contentDescription = null, tint = WarmGoldLight, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "آماده اجرا به‌صورت آفلاین و آنلاین",
                        color = CreamText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_courtyard_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DeepPersianBlue.copy(alpha = 0.58f),
                            DeepPersianBlue.copy(alpha = 0.85f)
                        )
                    )
                )
        )
        AnimatedGoldenSparkles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.widthIn(max = 560.dp)) {
                PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PersianGeometricRosette(modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "به سرزمین صفر خوش آمدید!",
                                color = InkBrown,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 21.sp
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_hosabgar),
                            contentDescription = "قهرمان پارسمیندا",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter,
                            modifier = Modifier
                                .size(136.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "شخصیت شیطنت‌آمیز «سلطان کاغذباز» کلیدهای شش قلمرو سرزمین صفر را پنهان کرده، عددهای ترازنامه را جابه‌جا نموده و پرونده‌های تخیلی را به هم ریخته است!",
                            color = InkBrown,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 23.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "این ماجراجویی شاد برای تمام خانواده بزرگ سازمان تأمین اجتماعی در شعب، بیمارستان‌ها، مراکز درمانی، کلینیک‌ها، ادارات کل استان‌ها و ستاد ساخته شده است.",
                            color = MutedInk,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 21.sp
                        )
                        Spacer(Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PersianTurquoise.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, PersianTurquoise)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Shield, contentDescription = null, tint = PersianTurquoise)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "تمامی داستان‌ها و ارقام کاملاً تخیلی هستند و بازی بدون اینترنت هم کاملاً اجرا می‌شود.",
                                    color = InkBrown,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        PersianActionPillButton(
                            text = "آغاز ساخت قهرمان",
                            onClick = onContinue,
                            containerColor = RoyalViolet,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("welcome_start_button")
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NicknameSetupScreen(
    initialNickname: String,
    initialDepartment: String,
    onBack: () -> Unit,
    onNext: (nickname: String, departmentType: String) -> Unit
) {
    BackHandler(onBack = onBack)
    var nickname by rememberSaveable { mutableStateOf(if (initialNickname == "قهرمان تأمین") "" else initialNickname) }
    var department by rememberSaveable { mutableStateOf(initialDepartment.ifBlank { "شعبه" }) }

    val presets = listOf("قهرمان فیروزه", "استاد تراز", "کاوشگر اسناد", "همیار دانا")
    val departments = listOf("شعبه", "بیمارستان", "کلینیک و مرکز درمانی", "اداره کل استان", "ستاد مرکزی")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        AnimatedGoldenSparkles()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text(
                    text = "گام ۱ از ۲ • نام مستعار قهرمان",
                    color = WarmGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.widthIn(max = 560.dp)) {
                PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "نام مستعار خود را برگزینید",
                            color = InkBrown,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "نیازی به درج نام واقعی خود نیست؛ یک لقب فانتزی و دلخواه انتخاب کنید.",
                            color = MutedInk,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(14.dp))
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { if (it.length <= 28) nickname = it },
                            label = { Text("نام مستعار قهرمان") },
                            placeholder = { Text("مثلاً: شکارچی مغایرت") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PersianTurquoise,
                                unfocusedBorderColor = ParchmentBorder,
                                focusedTextColor = InkBrown,
                                unfocusedTextColor = InkBrown
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("nickname_input")
                        )
                        Spacer(Modifier.height(10.dp))
                        Text("پیشنهادهای سریع:", color = MutedInk, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presets.forEach { sample ->
                                AssistChip(
                                    onClick = { nickname = sample },
                                    label = { Text(sample, color = InkBrown, fontSize = 12.sp) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = SaffronGold.copy(alpha = 0.22f)
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "بخش همکار در خانواده تأمین (اختیاری):",
                            color = InkBrown,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            departments.forEach { dept ->
                                val selected = department == dept
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { department = dept },
                                    shape = RoundedCornerShape(50),
                                    color = if (selected) PersianTurquoise else Color.White.copy(alpha = 0.65f),
                                    border = BorderStroke(1.5.dp, if (selected) WarmGoldLight else ParchmentBorder)
                                ) {
                                    Text(
                                        text = dept,
                                        color = if (selected) CreamText else InkBrown,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(22.dp))
                        PersianActionPillButton(
                            text = "مرحله بعد: انتخاب قهرمان",
                            onClick = {
                                val finalName = nickname.trim().ifEmpty { "قهرمان تأمین" }
                                onNext(finalName, department)
                            },
                            containerColor = JadeEmerald,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("nickname_next_button")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterSelectionScreen(
    initialCharacterId: String,
    isEditMode: Boolean,
    onBack: () -> Unit,
    onConfirmCharacter: (String) -> Unit
) {
    BackHandler(onBack = onBack)
    var selectedId by rememberSaveable { mutableStateOf(initialCharacterId) }
    val selectedHero = HeroCatalog.byId(selectedId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        AnimatedGoldenSparkles()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text(
                    text = if (isEditMode) "تغییر شخصیت قهرمان" else "گام ۲ از ۲ • انتخاب قهرمان",
                    color = WarmGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            // Highlighted selected character showcase
            PersianOrnateBluePanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroPortraitAvatar(
                        characterId = selectedHero.id,
                        size = 82.dp,
                        showCrown = true
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedHero.name,
                            color = SaffronGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${selectedHero.title} • ${selectedHero.personalityTag}",
                            color = PersianTurquoiseLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = selectedHero.bio,
                            color = CreamText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = PersianTurquoise.copy(alpha = 0.25f),
                border = BorderStroke(1.dp, PersianTurquoiseLight)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = WarmGoldLight, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "انتخاب شخصیت کاملاً ظاهری است و همه قهرمانان امتیاز برابر دارند",
                        color = CreamText,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 720.dp)
            ) {
                items(HeroCatalog.heroes, key = { it.id }) { hero ->
                    val isSelected = hero.id == selectedId
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedId = hero.id }
                            .testTag("hero_card_${hero.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) ParchmentCream else DeepPersianBlue.copy(alpha = 0.78f),
                        border = BorderStroke(
                            width = if (isSelected) 2.5.dp else 1.2.dp,
                            color = if (isSelected) SaffronGold else hero.primaryColor
                        ),
                        shadowElevation = if (isSelected) 8.dp else 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box {
                                HeroPortraitAvatar(
                                    characterId = hero.id,
                                    size = 64.dp,
                                    showCrown = isSelected
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "انتخاب‌شده",
                                        tint = JadeEmerald,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(20.dp)
                                            .background(Color.White, CircleShape)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = hero.name,
                                color = if (isSelected) InkBrown else CreamText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = hero.motifName,
                                color = if (isSelected) MutedInk else WarmGoldLight,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            PersianActionPillButton(
                text = if (isEditMode) "ذخیره شخصیت" else "ورود به سرزمین صفر",
                onClick = { onConfirmCharacter(selectedId) },
                containerColor = RoyalViolet,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .height(50.dp)
                    .testTag("confirm_character_button")
            )
        }
    }
}
