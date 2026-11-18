package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HeroCatalog
import com.example.data.model.LevelProgression
import com.example.data.model.SyncStatus
import com.example.ui.components.BottomNavTab
import com.example.ui.components.HeroPortraitAvatar
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
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import com.example.ui.viewmodel.HomeUiState
import com.example.util.PersianUtils

@Composable
fun ProfileScreen(
    uiState: HomeUiState,
    isSignedIn: Boolean,
    onEditCharacter: () -> Unit,
    onOpenSyncStatus: () -> Unit,
    onSelectBottomTab: (BottomNavTab) -> Unit
) {
    val profile = uiState.profile
    val hero = HeroCatalog.byId(profile.selectedCharacterId)
    val rank = LevelProgression.rankForXp(profile.xp)
    val equippedTitle = uiState.inventory.firstOrNull { it.category == "TITLE" && it.isEquipped }
    val equippedFrame = uiState.inventory.firstOrNull { it.category == "FRAME" && it.isEquipped }?.id ?: "item_frame_turquoise"
    val span = (rank.nextLevelXp - rank.minXp).coerceAtLeast(1)
    val levelProgress = ((profile.xp - rank.minXp).toFloat() / span.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PersianOrnateBluePanel(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HeroPortraitAvatar(
                                characterId = hero.id,
                                size = 86.dp,
                                showCrown = true,
                                frameId = equippedFrame
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(profile.nickname, color = SaffronGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                            Text(
                                text = equippedTitle?.title ?: "${hero.name} • ${profile.departmentType}",
                                color = PersianTurquoiseLight,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Surface(shape = RoundedCornerShape(50), color = RoyalViolet) {
                                Text(
                                    text = "سطح ${PersianUtils.toPersianDigits(rank.level)}: ${rank.title}",
                                    color = CreamText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { levelProgress },
                                color = SaffronGold,
                                trackColor = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(9.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "تجربه: ${PersianUtils.toPersianDigits(profile.xp)} از ${PersianUtils.toPersianDigits(rank.nextLevelXp)} XP",
                                color = WarmGoldLight,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatMiniTile("سکه", PersianUtils.formatPersianNumber(profile.coins))
                                StatMiniTile("نشان", PersianUtils.toPersianDigits(profile.medals))
                                StatMiniTile("امتیاز", PersianUtils.formatPersianNumber(profile.score))
                                StatMiniTile("مراحل کامل", "${PersianUtils.toPersianDigits(uiState.completedPlayableStages)} از ۱۰")
                            }

                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PersianActionPillButton(
                                    text = "تغییر قهرمان",
                                    onClick = onEditCharacter,
                                    containerColor = PersianTurquoise,
                                    leadingContent = {
                                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                    },
                                    modifier = Modifier.weight(1f).testTag("profile_edit_hero_button")
                                )
                                PersianActionPillButton(
                                    text = if (isSignedIn) "وضعیت همگام‌سازی" else "اتصال ابری",
                                    onClick = onOpenSyncStatus,
                                    containerColor = RoyalViolet,
                                    leadingContent = {
                                        Icon(Icons.Filled.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                    },
                                    modifier = Modifier.weight(1f).testTag("profile_sync_button")
                                )
                            }
                        }
                    }
                }

                // 8-Level Rank Ladder
                item {
                    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "نردبان ۸ سطح قهرمانان سرزمین صفر",
                                color = InkBrown,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            LevelProgression.ranks.forEach { lvl ->
                                val reached = rank.level >= lvl.level
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(if (reached) JadeEmerald else Color.Gray.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = PersianUtils.toPersianDigits(lvl.level),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = lvl.title,
                                        color = if (reached) InkBrown else MutedInk,
                                        fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${PersianUtils.formatPersianNumber(lvl.minXp)} XP",
                                        color = MutedInk,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Recent Session History
                item {
                    Text(
                        text = "تاریخچه مأموریت‌های اخیر",
                        color = WarmGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                if (uiState.sessions.isEmpty()) {
                    item {
                        PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "هنوز مأموریتی ثبت نشده است. اولین مرحله را از نقشه یا خانه آغاز کنید!",
                                color = MutedInk,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(uiState.sessions.take(8), key = { it.operationId }) { session ->
                        val st = SyncStatus.entries.firstOrNull { it.name == session.syncStatus } ?: SyncStatus.PENDING_VERIFICATION
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ParchmentCream,
                            border = BorderStroke(1.dp, AntiqueBrass),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(session.stageTitle, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(session.playedAtJalali, color = MutedInk, fontSize = 11.sp)
                                    Text(st.persianLabel, color = PersianTurquoise, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("+${PersianUtils.formatPersianNumber(session.rawScore)} امتیاز", color = RoyalViolet, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("+${PersianUtils.toPersianDigits(session.xpAwarded)} XP", color = JadeEmerald, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            ParsmindaBottomNavBar(
                currentRoute = BottomNavTab.PROFILE.route,
                onSelectTab = onSelectBottomTab
            )
        }
    }
}

@Composable
private fun StatMiniTile(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        Text(label, color = CreamText.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
fun AchievementsScreen(
    uiState: HomeUiState,
    onSelectBottomTab: (BottomNavTab) -> Unit
) {
    val unlockedCount = uiState.achievements.count { it.isUnlocked }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("افتخارات و نشان‌های قهرمانی", color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text(
                        text = "${PersianUtils.toPersianDigits(unlockedCount)} از ${PersianUtils.toPersianDigits(uiState.achievements.size)} نشان دریافت شده",
                        color = CreamText.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
                Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(34.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.achievements, key = { it.id }) { ach ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (ach.isUnlocked) ParchmentCream else DeepPersianBlue.copy(alpha = 0.72f),
                        border = BorderStroke(2.dp, if (ach.isUnlocked) SaffronGold else AntiqueBrass.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (ach.isUnlocked) SaffronGold else Color.White.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (ach.isUnlocked) Icons.Filled.EmojiEvents else Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (ach.isUnlocked) InkBrown else WarmGoldLight
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ach.title,
                                    color = if (ach.isUnlocked) InkBrown else WarmGoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = ach.description,
                                    color = if (ach.isUnlocked) MutedInk else CreamText.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { ach.progress.toFloat() / ach.target.coerceAtLeast(1).toFloat() },
                                    color = if (ach.isUnlocked) JadeEmerald else SaffronGold,
                                    trackColor = Color.Gray.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50))
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "پاداش: ${PersianUtils.toPersianDigits(ach.medalReward)} نشان + ${PersianUtils.toPersianDigits(ach.coinReward)} سکه" +
                                        if (ach.unlockedAtJalali.isNotBlank()) " • دریافت: ${ach.unlockedAtJalali}" else "",
                                    color = if (ach.isUnlocked) JadeEmerald else PersianTurquoiseLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            ParsmindaBottomNavBar(
                currentRoute = BottomNavTab.ACHIEVEMENTS.route,
                onSelectTab = onSelectBottomTab
            )
        }
    }
}

@Composable
fun InventoryScreen(
    uiState: HomeUiState,
    onPurchaseOrEquip: (String) -> Unit,
    onSelectBottomTab: (BottomNavTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("کوله‌پشتی و بازارچه تزئینی", color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text("بدون پرداخت واقعی • تمام آیتم‌ها با سکه بازی", color = CreamText.copy(alpha = 0.85f), fontSize = 12.sp)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = DeepPersianBlue,
                    border = BorderStroke(1.5.dp, SaffronGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(PersianUtils.formatPersianNumber(uiState.profile.coins), color = CreamText, fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.inventory, key = { it.id }) { item ->
                    val categoryLabel = when (item.category) {
                        "FRAME" -> "قاب پروفایل"
                        "COMPANION" -> "همراه فانتزی"
                        "TITLE" -> "عنوان قهرمانی"
                        else -> "نشان افتخار"
                    }
                    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (item.isEquipped) JadeEmerald else PersianTurquoise),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.isEquipped) Icons.Filled.CheckCircle else Icons.Filled.Backpack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(categoryLabel, color = PersianTurquoise, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(item.description, color = MutedInk, fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            PersianActionPillButton(
                                text = when {
                                    item.isEquipped -> "فعال"
                                    item.isOwned -> "پوشیدن"
                                    else -> "${PersianUtils.toPersianDigits(item.priceCoins)} سکه"
                                },
                                onClick = { onPurchaseOrEquip(item.id) },
                                enabled = !item.isEquipped,
                                containerColor = when {
                                    item.isEquipped -> JadeEmerald
                                    item.isOwned -> PersianTurquoise
                                    else -> RoyalViolet
                                },
                                modifier = Modifier.testTag("inventory_action_${item.id}")
                            )
                        }
                    }
                }
            }

            ParsmindaBottomNavBar(
                currentRoute = BottomNavTab.INVENTORY.route,
                onSelectTab = onSelectBottomTab
            )
        }
    }
}

@Composable
fun LeaderboardScreen(
    uiState: HomeUiState,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val sorted = uiState.leaderboard.sortedByDescending { it.score }

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
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Column {
                    Text("تالار قهرمانان", color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text("فقط امتیازهای تأییدشده وارد رتبه‌بندی رسمی ابری می‌شوند", color = CreamText.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sorted.indices.toList()) { idx ->
                    val entry = sorted[idx]
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (entry.isCurrentUser) ParchmentCream else DeepPersianBlue.copy(alpha = 0.85f),
                        border = BorderStroke(
                            width = if (entry.isCurrentUser) 2.2.dp else 1.2.dp,
                            color = if (idx == 0) SaffronGold else if (entry.isCurrentUser) PersianTurquoise else AntiqueBrass
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (idx) {
                                            0 -> SaffronGold
                                            1 -> Color(0xFFD6DCE5)
                                            2 -> Color(0xFFD98E54)
                                            else -> PersianTurquoise
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = PersianUtils.toPersianDigits(idx + 1),
                                    color = InkBrown,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            HeroPortraitAvatar(characterId = entry.characterId, size = 40.dp)
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (entry.isCurrentUser) "${entry.displayName} (شما)" else entry.displayName,
                                        color = if (entry.isCurrentUser) InkBrown else CreamText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (entry.isVerifiedOnline) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Filled.Verified, contentDescription = "تأیید ابری", tint = JadeEmerald, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    text = "سطح ${PersianUtils.toPersianDigits(entry.level)}",
                                    color = if (entry.isCurrentUser) MutedInk else PersianTurquoiseLight,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = PersianUtils.formatPersianNumber(entry.score),
                                color = if (entry.isCurrentUser) RoyalViolet else WarmGoldLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
