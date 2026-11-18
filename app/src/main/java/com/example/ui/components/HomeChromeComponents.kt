package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.LevelProgression
import com.example.ui.theme.AntiqueBrass
import com.example.ui.theme.CreamText
import com.example.ui.theme.DeepPersianBlue
import com.example.ui.theme.JadeEmerald
import com.example.ui.theme.LapisBlue
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseDark
import com.example.ui.theme.PersianTurquoiseLight
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import com.example.util.PersianUtils

enum class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("home", "خانه", Icons.Filled.Home, "nav_home"),
    MAP("map", "نقشه", Icons.Filled.Map, "nav_map"),
    ACHIEVEMENTS("achievements", "افتخارات", Icons.Filled.EmojiEvents, "nav_achievements"),
    INVENTORY("inventory", "کوله‌پشتی", Icons.Filled.Backpack, "nav_inventory"),
    PROFILE("profile", "پروفایل", Icons.Filled.Person, "nav_profile")
}

@Composable
fun HomeTopHudBar(
    profile: PlayerProfileEntity,
    pendingSyncCount: Int,
    isCompactWidth: Boolean,
    equippedFrameId: String,
    onProfileClick: () -> Unit,
    onCoinsClick: () -> Unit,
    onMedalsClick: () -> Unit,
    onScoreClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val rank = LevelProgression.rankForXp(profile.xp)
    val span = (rank.nextLevelXp - rank.minXp).coerceAtLeast(1)
    val progressInLevel = (profile.xp - rank.minXp).coerceIn(0, span)
    val progressFraction = (progressInLevel.toFloat() / span.toFloat()).coerceIn(0.05f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (isCompactWidth) {
            // Row 1: Profile pill + Logo crest + Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerProfileHudPill(
                    profile = profile,
                    rankLevel = rank.level,
                    progressText = "${PersianUtils.toPersianDigits(profile.xp)} / ${PersianUtils.toPersianDigits(rank.nextLevelXp)}",
                    progressFraction = progressFraction,
                    equippedFrameId = equippedFrameId,
                    onClick = onProfileClick,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(Modifier.width(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ParsmindaLogoCrest(compact = true)
                    SettingsHudButton(pendingSyncCount = pendingSyncCount, onClick = onSettingsClick)
                }
            }
            // Row 2: Coins, Medals, Score pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResourceHudPill(
                    valueText = PersianUtils.formatPersianNumber(profile.coins),
                    icon = Icons.Filled.MonetizationOn,
                    iconTint = SaffronGold,
                    showPlus = true,
                    onClick = onCoinsClick,
                    testTag = "hud_coins_pill"
                )
                ResourceHudPill(
                    valueText = PersianUtils.formatPersianNumber(profile.medals),
                    icon = Icons.Filled.MilitaryTech,
                    iconTint = WarmGoldLight,
                    showPlus = true,
                    onClick = onMedalsClick,
                    testTag = "hud_medals_pill"
                )
                ResourceHudPill(
                    valueText = PersianUtils.formatPersianNumber(profile.score),
                    icon = Icons.Filled.EmojiEvents,
                    iconTint = SaffronGold,
                    pillColor = RoyalViolet,
                    showPlus = false,
                    onClick = onScoreClick,
                    testTag = "hud_score_pill"
                )
            }
        } else {
            // Single wide RPG HUD row matching reference layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerProfileHudPill(
                    profile = profile,
                    rankLevel = rank.level,
                    progressText = "${PersianUtils.toPersianDigits(profile.xp)} / ${PersianUtils.toPersianDigits(rank.nextLevelXp)}",
                    progressFraction = progressFraction,
                    equippedFrameId = equippedFrameId,
                    onClick = onProfileClick
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ResourceHudPill(
                        valueText = PersianUtils.formatPersianNumber(profile.coins),
                        icon = Icons.Filled.MonetizationOn,
                        iconTint = SaffronGold,
                        showPlus = true,
                        onClick = onCoinsClick,
                        testTag = "hud_coins_pill"
                    )
                    ResourceHudPill(
                        valueText = PersianUtils.formatPersianNumber(profile.medals),
                        icon = Icons.Filled.MilitaryTech,
                        iconTint = WarmGoldLight,
                        showPlus = true,
                        onClick = onMedalsClick,
                        testTag = "hud_medals_pill"
                    )
                    ResourceHudPill(
                        valueText = PersianUtils.formatPersianNumber(profile.score),
                        icon = Icons.Filled.EmojiEvents,
                        iconTint = SaffronGold,
                        pillColor = RoyalViolet,
                        showPlus = false,
                        onClick = onScoreClick,
                        testTag = "hud_score_pill"
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ParsmindaLogoCrest(compact = false)
                    SettingsHudButton(pendingSyncCount = pendingSyncCount, onClick = onSettingsClick)
                }
            }
        }
    }
}

@Composable
private fun PlayerProfileHudPill(
    profile: PlayerProfileEntity,
    rankLevel: Int,
    progressText: String,
    progressFraction: Float,
    equippedFrameId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag("hud_profile_pill"),
        shape = RoundedCornerShape(50),
        color = DeepPersianBlue.copy(alpha = 0.9f),
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(WarmGoldLight, AntiqueBrass))),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(start = 6.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeroPortraitAvatar(
                characterId = profile.selectedCharacterId,
                size = 46.dp,
                showCrown = true,
                frameId = equippedFrameId
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = profile.nickname,
                    color = CreamText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "سطح ${PersianUtils.toPersianDigits(rankLevel)}",
                    color = WarmGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .width(106.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF281910))
                        .padding(1.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(11.dp)
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(50))
                            .background(Brush.horizontalGradient(listOf(SaffronGold, WarmGoldLight)))
                    )
                    Text(
                        text = progressText,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ResourceHudPill(
    valueText: String,
    icon: ImageVector,
    iconTint: Color,
    showPlus: Boolean,
    onClick: () -> Unit,
    testTag: String,
    pillColor: Color = DeepPersianBlue
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(50),
        color = pillColor.copy(alpha = 0.92f),
        border = BorderStroke(1.8.dp, WarmGoldLight),
        shadowElevation = 5.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = valueText,
                color = CreamText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            if (showPlus) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(JadeEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "افزودن",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ParsmindaLogoCrest(compact: Boolean) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DeepPersianBlue.copy(alpha = 0.94f),
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(WarmGoldLight, PersianTurquoiseLight, SaffronGold))),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = if (compact) 10.dp else 14.dp,
                vertical = if (compact) 4.dp else 6.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "پارسمیندا!",
                color = SaffronGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (compact) 14.sp else 17.sp
            )
            Text(
                text = "و قهرمانان تأمین",
                color = CreamText,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (compact) 10.sp else 11.sp
            )
            Spacer(Modifier.height(2.dp))
            // Subtle Iranian tricolour ribbon bar
            Row(
                modifier = Modifier
                    .width(if (compact) 44.dp else 56.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
            ) {
                Box(Modifier.weight(1f).height(3.dp).background(JadeEmerald))
                Box(Modifier.weight(1f).height(3.dp).background(Color.White))
                Box(Modifier.weight(1f).height(3.dp).background(RubyCrimson))
            }
        }
    }
}

@Composable
private fun SettingsHudButton(
    pendingSyncCount: Int,
    onClick: () -> Unit
) {
    Box {
        Surface(
            shape = CircleShape,
            color = DeepPersianBlue.copy(alpha = 0.92f),
            border = BorderStroke(2.dp, WarmGoldLight),
            shadowElevation = 6.dp,
            modifier = Modifier.size(48.dp)
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.testTag("hud_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "تنظیمات و همگام‌سازی",
                    tint = CreamText
                )
            }
        }
        if (pendingSyncCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(SaffronGold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = PersianUtils.toPersianDigits(pendingSyncCount.coerceAtMost(9)),
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ParsmindaBottomNavBar(
    currentRoute: String,
    onSelectTab: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.entries.forEach { tab ->
                val selected = currentRoute == tab.route
                val bgBrush = if (selected) {
                    Brush.verticalGradient(listOf(PersianTurquoiseLight, PersianTurquoise))
                } else {
                    Brush.verticalGradient(listOf(LapisBlue.copy(alpha = 0.95f), PersianTurquoiseDark.copy(alpha = 0.95f)))
                }
                val borderColor = if (selected) WarmGoldLight else AntiqueBrass.copy(alpha = 0.85f)

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (selected) 8.dp else 4.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectTab(tab) }
                        .testTag(tab.testTag),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Transparent,
                    border = BorderStroke(if (selected) 2.2.dp else 1.5.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .background(bgBrush)
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (selected) WarmGoldLight else CreamText,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            color = if (selected) WarmGoldLight else CreamText,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun XpRewardBadge(xp: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = SaffronGold,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = "${PersianUtils.toPersianDigits(xp)} امتیاز تجربه",
            color = Color(0xFF4A2E0E),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
