package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.local.StageEntity
import com.example.data.local.WorldEntity
import com.example.data.model.MiniGameType
import com.example.data.model.StageRating
import com.example.ui.components.BottomNavTab
import com.example.ui.components.FriendlyMonsterArtwork
import com.example.ui.components.ParsmindaBottomNavBar
import com.example.ui.components.PersianActionPillButton
import com.example.ui.components.PersianOrnateBluePanel
import com.example.ui.components.PersianParchmentCard
import com.example.ui.theme.AntiqueBrass
import com.example.ui.theme.CreamText
import com.example.ui.theme.DeepPersianBlue
import com.example.ui.theme.InkBrown
import com.example.ui.theme.JadeEmerald
import com.example.ui.theme.LapisBlue
import com.example.ui.theme.MutedInk
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseLight
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import com.example.util.PersianUtils

@Composable
fun WorldMapScreen(
    worlds: List<WorldEntity>,
    stages: List<StageEntity>,
    onSelectWorld: (WorldEntity) -> Unit,
    onOpenFutureWorlds: () -> Unit,
    onSelectBottomTab: (BottomNavTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "نقشه شش قلمرو سرزمین صفر",
                        color = WarmGoldLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp
                    )
                    Text(
                        text = "برای کشف مراحل، قلمرو مورد نظر را لمس کنید",
                        color = CreamText.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
                PersianActionPillButton(
                    text = "آینده بازی",
                    onClick = onOpenFutureWorlds,
                    containerColor = RoyalViolet
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.5.dp, WarmGoldLight),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = R.drawable.img_world_map_persia),
                                contentDescription = "نقشه مصور شش قلمرو سرزمین صفر",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = DeepPersianBlue.copy(alpha = 0.82f),
                                shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "کاروان‌سراهای دانش و خزانه",
                                    color = WarmGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                items(worlds, key = { it.id }) { world ->
                    val worldStages = stages.filter { it.worldId == world.id }
                    val completedCount = worldStages.count { it.timesCompleted > 0 }
                    val totalCount = worldStages.size.coerceAtLeast(5)
                    val isLocked = !world.isUnlocked

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                if (world.isFutureContent) onOpenFutureWorlds() else onSelectWorld(world)
                            }
                            .testTag("world_card_${world.id}"),
                        shape = RoundedCornerShape(18.dp),
                        color = if (isLocked) DeepPersianBlue.copy(alpha = 0.75f) else ParchmentCream,
                        border = BorderStroke(2.dp, if (isLocked) AntiqueBrass else SaffronGold),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            world.isFutureContent -> RoyalViolet.copy(alpha = 0.7f)
                                            world.isUnlocked -> PersianTurquoise
                                            else -> Color(0xFF4A5568)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isLocked) Icons.Filled.Lock else Icons.Filled.Castle,
                                    contentDescription = null,
                                    tint = WarmGoldLight,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = world.title,
                                        color = if (isLocked) WarmGoldLight else InkBrown,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = when {
                                            world.isFutureContent -> RoyalViolet
                                            world.isUnlocked -> JadeEmerald
                                            else -> RubyCrimson
                                        }
                                    ) {
                                        Text(
                                            text = when {
                                                world.isFutureContent -> "به‌زودی"
                                                world.isUnlocked -> "باز است"
                                                else -> "نیازمند مرحله ۵"
                                            },
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = world.subtitle,
                                    color = if (isLocked) PersianTurquoiseLight else PersianTurquoise,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = world.description,
                                    color = if (isLocked) CreamText.copy(alpha = 0.8f) else MutedInk,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    maxLines = 2
                                )
                                if (!world.isFutureContent) {
                                    Spacer(Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { completedCount.toFloat() / totalCount.toFloat() },
                                        color = SaffronGold,
                                        trackColor = Color.Gray.copy(alpha = 0.3f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(7.dp)
                                            .clip(RoundedCornerShape(50))
                                    )
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        text = "تکمیل‌شده: ${PersianUtils.toPersianDigits(completedCount)} از ${PersianUtils.toPersianDigits(totalCount)} مرحله • رئیس قلمرو: ${world.bossName}",
                                        color = if (isLocked) CreamText.copy(alpha = 0.75f) else MutedInk,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ParsmindaBottomNavBar(
                currentRoute = BottomNavTab.MAP.route,
                onSelectTab = onSelectBottomTab
            )
        }
    }
}

@Composable
fun WorldDetailScreen(
    world: WorldEntity,
    stages: List<StageEntity>,
    onBack: () -> Unit,
    onOpenStages: () -> Unit,
    onSelectStage: (StageEntity) -> Unit
) {
    BackHandler(onBack = onBack)
    val completed = stages.count { it.timesCompleted > 0 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text(
                    text = world.title,
                    color = WarmGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.height(10.dp))
            PersianOrnateBluePanel(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FriendlyMonsterArtwork(
                        isBoss = true,
                        stageNumber = world.stageEnd,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = world.subtitle,
                        color = PersianTurquoiseLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = world.description,
                        color = CreamText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepPersianBlue.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, SaffronGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("نگهبان نهایی: ${world.bossName}", color = WarmGoldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                "${PersianUtils.toPersianDigits(completed)} از ${PersianUtils.toPersianDigits(stages.size)} کامل",
                                color = CreamText,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    if (world.isUnlocked) {
                        PersianActionPillButton(
                            text = "ورود به فهرست مراحل",
                            onClick = onOpenStages,
                            containerColor = RoyalViolet,
                            modifier = Modifier.fillMaxWidth().testTag("world_detail_open_stages")
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RubyCrimson.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, RubyCrimson)
                        ) {
                            Text(
                                text = "این قلمرو با شکست دادن «اژدهای مغایرت» در مرحله ۵ باز می‌شود.",
                                color = CreamText,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "نگاهی به مراحل این قلمرو",
                color = WarmGoldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(8.dp))
            stages.forEach { st ->
                StageRowCard(
                    stage = st,
                    onClick = { if (st.isUnlocked && world.isUnlocked) onSelectStage(st) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StageSelectionScreen(
    world: WorldEntity,
    stages: List<StageEntity>,
    onBack: () -> Unit,
    onSelectStage: (StageEntity) -> Unit
) {
    BackHandler(onBack = onBack)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Column {
                    Text(
                        text = "مراحل ${world.title}",
                        color = WarmGoldLight,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "با تکمیل هر مرحله، دروازه مرحله بعدی باز می‌شود",
                        color = CreamText.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(stages, key = { it.id }) { stage ->
                    StageRowCard(
                        stage = stage,
                        onClick = { if (stage.isUnlocked) onSelectStage(stage) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StageRowCard(
    stage: StageEntity,
    onClick: () -> Unit
) {
    val locked = !stage.isUnlocked
    val rating = StageRating.entries.firstOrNull { it.name == stage.bestRating } ?: StageRating.NONE
    val miniGame = MiniGameType.entries.firstOrNull { it.name == stage.miniGameType } ?: MiniGameType.VAULT_PUZZLE

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !locked, onClick = onClick)
            .testTag("stage_card_${stage.stageNumber}"),
        shape = RoundedCornerShape(16.dp),
        color = if (locked) DeepPersianBlue.copy(alpha = 0.6f) else ParchmentCream,
        border = BorderStroke(
            width = if (stage.isBossStage) 2.2.dp else 1.5.dp,
            color = if (stage.isBossStage) RubyCrimson else if (locked) Color.Gray else SaffronGold
        ),
        shadowElevation = if (locked) 1.dp else 6.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            locked -> Color(0xFF475569)
                            stage.isBossStage -> RubyCrimson
                            else -> PersianTurquoise
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (locked) {
                    Icon(Icons.Filled.Lock, contentDescription = "قفل", tint = Color.White)
                } else {
                    Text(
                        text = PersianUtils.toPersianDigits(stage.stageNumber),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stage.title,
                        color = if (locked) CreamText.copy(alpha = 0.7f) else InkBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (stage.isBossStage) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(50), color = RubyCrimson) {
                            Text(
                                text = "نبرد رئیس",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "${miniGame.persianTitle} • سختی ${PersianUtils.toPersianDigits(stage.difficulty)} از ۳",
                    color = if (locked) CreamText.copy(alpha = 0.55f) else MutedInk,
                    fontSize = 12.sp
                )
                if (stage.timesCompleted > 0) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) { idx ->
                            Icon(
                                imageVector = if (idx < rating.stars) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = SaffronGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${rating.persianTitle} • قابل تکرار تمرینی",
                            color = JadeEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (!locked) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "شروع", tint = RoyalViolet, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun MissionIntroScreen(
    stage: StageEntity,
    onBack: () -> Unit,
    onStartMission: () -> Unit
) {
    BackHandler(onBack = onBack)
    val miniGame = MiniGameType.entries.firstOrNull { it.name == stage.miniGameType } ?: MiniGameType.VAULT_PUZZLE
    val isReplay = stage.timesCompleted > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text(
                    text = "معرفی مأموریت • مرحله ${PersianUtils.toPersianDigits(stage.stageNumber)}",
                    color = WarmGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (stage.isBossStage || miniGame == MiniGameType.MONSTER_BATTLE) {
                        FriendlyMonsterArtwork(
                            isBoss = stage.isBossStage,
                            stageNumber = stage.stageNumber,
                            modifier = Modifier.size(110.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = PersianTurquoise,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stage.title,
                        color = InkBrown,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(50), color = PersianTurquoise) {
                        Text(
                            text = miniGame.persianTitle,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = stage.storyIntro,
                        color = InkBrown,
                        fontSize = 14.sp,
                        lineHeight = 23.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PersianTurquoise.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, PersianTurquoise),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("هدف مأموریت:", color = PersianTurquoise, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(stage.objective, color = InkBrown, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = SaffronGold)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isReplay) "${PersianUtils.toPersianDigits(15)} XP تمرینی"
                                else "${PersianUtils.toPersianDigits(stage.xpReward)} XP",
                                color = InkBrown,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = SaffronGold)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isReplay) "پاداش اصلی قبلاً دریافت شده"
                                else "${PersianUtils.toPersianDigits(stage.coinReward)} سکه",
                                color = InkBrown,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    PersianActionPillButton(
                        text = if (stage.isBossStage) "شروع نبرد نهایی!" else "آغاز مأموریت",
                        onClick = onStartMission,
                        containerColor = if (stage.isBossStage) RubyCrimson else RoyalViolet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("mission_intro_start_button")
                    )
                }
            }
        }
    }
}

@Composable
fun FutureWorldsScreen(
    worlds: List<WorldEntity>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val futureWorlds = worlds.filter { it.isFutureContent }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, RoyalViolet.copy(alpha = 0.75f))))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text(
                    text = "قلمروهای آینده و محتوای در حال ساخت",
                    color = WarmGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = JadeEmerald)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "سفر قهرمانان ادامه دارد!",
                                    color = InkBrown,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "در فاز نخست بازی «پارسمیندا و قهرمانان تأمین»، قلمروهای قلعه خزانه و شهر کارگزینی (۱۰ مرحله کامل) آماده بازی هستند. چهار قلمرو دیگر همراه با ۴ مینی‌گیم جدید در به‌روزرسانی‌های بعدی با معماری ماژولار اضافه خواهند شد.",
                                color = MutedInk,
                                fontSize = 13.sp,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }

                items(futureWorlds, key = { it.id }) { world ->
                    PersianOrnateBluePanel(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(RoyalViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = WarmGoldLight)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(world.title, color = SaffronGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(world.subtitle, color = PersianTurquoiseLight, fontSize = 12.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(world.description, color = CreamText, fontSize = 12.sp, lineHeight = 18.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("نگهبان قلمرو: ${world.bossName}", color = WarmGoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
