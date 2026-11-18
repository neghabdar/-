package com.example.ui.screens

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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.LeaderboardCacheEntity
import com.example.data.local.StageEntity
import com.example.data.model.HeroCatalog
import com.example.ui.components.AnimatedGoldenSparkles
import com.example.ui.components.BottomNavTab
import com.example.ui.components.HomeTopHudBar
import com.example.ui.components.IllustratedHeroCanvas
import com.example.ui.components.ParsmindaBottomNavBar
import com.example.ui.components.PersianActionPillButton
import com.example.ui.components.PersianGeometricRosette
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
import com.example.ui.theme.RoyalVioletDark
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import com.example.ui.viewmodel.HomeUiState
import com.example.util.PersianUtils

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSelectBottomTab: (BottomNavTab) -> Unit,
    onContinueStory: (Int) -> Unit,
    onOpenDailyMission: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenAnnouncements: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenInventory: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val activeStage = uiState.stages.firstOrNull { it.stageNumber == uiState.profile.currentStageNumber }
        ?: uiState.stages.firstOrNull()
    val equippedFrame = uiState.inventory.firstOrNull { it.category == "FRAME" && it.isEquipped }?.id
        ?: "item_frame_turquoise"
    val specialAnnouncement = uiState.announcements.firstOrNull { it.isSpecialEvent }
        ?: uiState.announcements.firstOrNull()
    val shortWelcomeText = uiState.announcements.firstOrNull { !it.isSpecialEvent }?.title
        ?: "به دنیای شگفت‌انگیز پارسمیندا خوش آمدید!"

    Box(modifier = Modifier.fillMaxSize()) {
        // Sunlit Iranian Palace Courtyard background
        Image(
            painter = painterResource(id = R.drawable.img_courtyard_bg),
            contentDescription = "حیاط کاخ پارسمیندا",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Gentle atmosphere gradient so HUD & cards pop crisply
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DeepPersianBlue.copy(alpha = 0.28f),
                            Color.Transparent,
                            DeepPersianBlue.copy(alpha = 0.42f)
                        )
                    )
                )
        )
        AnimatedGoldenSparkles()

        Column(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val isWide = maxWidth >= 680.dp

                Column(modifier = Modifier.fillMaxSize()) {
                    HomeTopHudBar(
                        profile = uiState.profile,
                        pendingSyncCount = uiState.pendingSyncCount,
                        isCompactWidth = !isWide,
                        equippedFrameId = equippedFrame,
                        onProfileClick = onOpenProfile,
                        onCoinsClick = onOpenInventory,
                        onMedalsClick = onOpenAchievements,
                        onScoreClick = onOpenLeaderboard,
                        onSettingsClick = onOpenSettings
                    )

                    if (isWide) {
                        // Wide / Landscape composition directly faithful to reference image
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Right/Start side in RTL: Hall of Champions + Special Missions + Weekly Prize
                            Column(
                                modifier = Modifier
                                    .weight(0.29f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HallOfChampionsCard(
                                    entries = uiState.leaderboard.sortedByDescending { it.score }.take(3),
                                    onViewTable = onOpenLeaderboard
                                )
                                SpecialMissionsCard(
                                    daysRemaining = specialAnnouncement?.daysRemaining ?: 3,
                                    onClick = onOpenAnnouncements
                                )
                                WeeklyPrizeCard(onClick = onOpenInventory)
                            }

                            // Center Hero Courtyard + Continue Story Scroll
                            Row(
                                modifier = Modifier
                                    .weight(0.44f)
                                    .fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ContinueStoryScrollCard(
                                    stage = activeStage,
                                    lastCompletedTitle = uiState.profile.lastCompletedStageTitle,
                                    onContinue = { onContinueStory(activeStage?.id ?: 1) },
                                    modifier = Modifier.weight(0.54f)
                                )
                                Spacer(Modifier.width(8.dp))
                                HeroCourtyardShowcase(
                                    characterId = uiState.profile.selectedCharacterId,
                                    modifier = Modifier.weight(0.46f)
                                )
                            }

                            // Left/End side in RTL: Daily Mission + Welcome Scroll
                            Column(
                                modifier = Modifier
                                    .weight(0.27f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DailyMissionCard(
                                    progress = uiState.profile.dailyMissionProgress,
                                    target = uiState.profile.dailyMissionTarget,
                                    completed = uiState.profile.dailyMissionCompleted,
                                    onClick = onOpenDailyMission
                                )
                                WelcomeRibbonCard(
                                    message = shortWelcomeText,
                                    onClick = onOpenAnnouncements
                                )
                            }
                        }
                    } else {
                        // Portrait Phone responsive courtyard layout
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Hero Showcase + Welcome Ribbon
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HeroCourtyardShowcase(
                                    characterId = uiState.profile.selectedCharacterId,
                                    modifier = Modifier.weight(0.45f)
                                )
                                Column(
                                    modifier = Modifier.weight(0.55f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ContinueStoryScrollCard(
                                        stage = activeStage,
                                        lastCompletedTitle = uiState.profile.lastCompletedStageTitle,
                                        onContinue = { onContinueStory(activeStage?.id ?: 1) }
                                    )
                                }
                            }

                            WelcomeRibbonCard(
                                message = shortWelcomeText,
                                onClick = onOpenAnnouncements
                            )

                            DailyMissionCard(
                                progress = uiState.profile.dailyMissionProgress,
                                target = uiState.profile.dailyMissionTarget,
                                completed = uiState.profile.dailyMissionCompleted,
                                onClick = onOpenDailyMission
                            )

                            HallOfChampionsCard(
                                entries = uiState.leaderboard.sortedByDescending { it.score }.take(3),
                                onViewTable = onOpenLeaderboard
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SpecialMissionsCard(
                                    daysRemaining = specialAnnouncement?.daysRemaining ?: 3,
                                    onClick = onOpenAnnouncements,
                                    modifier = Modifier.weight(1f)
                                )
                                WeeklyPrizeCard(
                                    onClick = onOpenInventory,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            ParsmindaBottomNavBar(
                currentRoute = BottomNavTab.HOME.route,
                onSelectTab = onSelectBottomTab
            )
        }
    }
}

@Composable
private fun HeroCourtyardShowcase(
    characterId: String,
    modifier: Modifier = Modifier
) {
    val hero = HeroCatalog.byId(characterId)
    val transition = rememberInfiniteTransition(label = "heroFloat")
    val offsetY by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroFloatY"
    )

    Column(
        modifier = modifier.offset(y = offsetY.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(2.5.dp, WarmGoldLight),
            color = DeepPersianBlue.copy(alpha = 0.35f),
            shadowElevation = 10.dp,
            modifier = Modifier.size(width = 164.dp, height = 205.dp)
        ) {
            if (characterId == "hesabgar") {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_hosabgar),
                    contentDescription = "${hero.name} و گربه فندق",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(LapisBlue, DeepPersianBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    IllustratedHeroCanvas(
                        characterId = characterId,
                        modifier = Modifier.size(145.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyMissionCard(
    progress: Int,
    target: Int,
    completed: Boolean,
    onClick: () -> Unit
) {
    PersianParchmentCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_daily_mission_card")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF9C4221),
                border = BorderStroke(1.2.dp, WarmGoldLight)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.GpsFixed, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "مأموریت روزانه",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (completed) "مأموریت امروز تکمیل شد!" else "سه پاسخ صحیح بده",
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "پیشرفت: ${PersianUtils.toPersianDigits(progress)} از ${PersianUtils.toPersianDigits(target)}",
                color = MutedInk,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("${PersianUtils.toPersianDigits(40)} XP", color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(PersianUtils.toPersianDigits(150), color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            PersianActionPillButton(
                text = if (completed) "آماده چالش بیشتر" else "مشاهده مأموریت",
                onClick = onClick,
                containerColor = JadeEmerald,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_daily_mission_button")
            )
        }
    }
}

@Composable
private fun WelcomeRibbonCard(
    message: String,
    onClick: () -> Unit
) {
    PersianParchmentCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("home_welcome_announcement")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersianGeometricRosette(modifier = Modifier.size(34.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = message,
                color = InkBrown,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ContinueStoryScrollCard(
    stage: StageEntity?,
    lastCompletedTitle: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stageNumText = PersianUtils.toPersianDigits(stage?.stageNumber ?: 1)
    val stageTitle = stage?.title ?: "دروازه اسرارآمیز"
    val storyHint = stage?.objective ?: "برای کشف راز قلعه خزانه وارد دروازه شوید..."

    PersianParchmentCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_continue_story_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ادامه داستان",
                color = InkBrown,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "مرحله $stageNumText : $stageTitle",
                color = DeepPersianBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = PersianTurquoise,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = storyHint,
                        color = MutedInk,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "آخرین دستاورد: $lastCompletedTitle",
                color = MutedInk,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            PersianActionPillButton(
                text = "ادامه بازی",
                onClick = onContinue,
                containerColor = RoyalViolet,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_continue_play_button")
            )
        }
    }
}

@Composable
private fun HallOfChampionsCard(
    entries: List<LeaderboardCacheEntity>,
    onViewTable: () -> Unit
) {
    PersianOrnateBluePanel(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_leaderboard_card")
    ) {
        Column(
            modifier = Modifier.padding(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "تالار قهرمانان",
                    color = WarmGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(6.dp))
            entries.forEachIndexed { idx, entry ->
                val medalColor = when (idx) {
                    0 -> SaffronGold
                    1 -> Color(0xFFD6DCE5)
                    else -> Color(0xFFD98E54)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(medalColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = PersianUtils.toPersianDigits(idx + 1),
                            color = InkBrown,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = entry.displayName,
                        color = CreamText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = PersianUtils.formatPersianNumber(entry.score),
                        color = WarmGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            PersianActionPillButton(
                text = "مشاهده جدول",
                onClick = onViewTable,
                containerColor = SaffronGold,
                contentColor = InkBrown,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_view_leaderboard_button")
            )
        }
    }
}

@Composable
private fun SpecialMissionsCard(
    daysRemaining: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PersianOrnateBluePanel(
        modifier = modifier.testTag("home_special_missions_card"),
        topColor = RoyalViolet,
        bottomColor = RoyalVioletDark,
        borderColor = WarmGoldLight
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CardGiftcard, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "مأموریت‌های ویژه",
                    color = CreamText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "تا پایان هفته ${PersianUtils.toPersianDigits(daysRemaining)} روز باقی مانده",
                color = WarmGoldLight,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            PersianActionPillButton(
                text = "مشاهده",
                onClick = onClick,
                containerColor = SaffronGold,
                contentColor = InkBrown,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WeeklyPrizeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("home_weekly_prize_card"),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, WarmGoldLight),
        color = PersianTurquoise,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(DeepPersianBlue, PersianTurquoise)))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_treasure_chest),
                contentDescription = "صندوقچه جایزه ویژه هفتگی",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = "جایزه ویژه هفتگی",
                    color = WarmGoldLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "گنجینه سکه‌های تزئینی و نشان‌های افتخار",
                    color = CreamText,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
