package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncStatus
import com.example.data.remote.AuthManager
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
import com.example.ui.viewmodel.HomeUiState
import com.example.util.PersianUtils
import com.google.firebase.auth.FirebaseUser

@Composable
fun SettingsScreen(
    uiState: HomeUiState,
    onBack: () -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onEditIdentity: () -> Unit,
    onOpenSyncStatus: () -> Unit,
    onOpenAnnouncements: () -> Unit,
    onOpenFutureWorlds: () -> Unit
) {
    BackHandler(onBack = onBack)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text("تنظیمات بازی", color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }

            PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = null, tint = PersianTurquoise)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("افکت‌های صوتی ایرانی", color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("صدای بازخورد دکمه‌ها و پیروزی", color = MutedInk, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = uiState.preferences.soundEnabled,
                            onCheckedChange = onToggleSound,
                            colors = SwitchDefaults.colors(checkedThumbColor = CreamText, checkedTrackColor = JadeEmerald)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = PersianTurquoise)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("یادآور مأموریت روزانه", color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("اطلاع‌رسانی رویدادهای هفتگی", color = MutedInk, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = uiState.preferences.notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(checkedThumbColor = CreamText, checkedTrackColor = JadeEmerald)
                        )
                    }
                }
            }

            SettingsNavTile(
                title = "ویرایش نام مستعار و شخصیت",
                subtitle = "شخصیت فعلی: ${uiState.profile.nickname}",
                icon = Icons.Filled.Edit,
                onClick = onEditIdentity,
                testTag = "settings_edit_identity"
            )
            SettingsNavTile(
                title = "وضعیت همگام‌سازی و حساب گوگل",
                subtitle = "${PersianUtils.toPersianDigits(uiState.pendingSyncCount)} عملیات در انتظار همگام‌سازی",
                icon = Icons.Filled.Sync,
                onClick = onOpenSyncStatus,
                testTag = "settings_sync_status"
            )
            SettingsNavTile(
                title = "اطلاعیه‌ها و مأموریت‌های ویژه",
                subtitle = "اخبار سرزمین صفر و رویداد هفتگی",
                icon = Icons.Filled.Campaign,
                onClick = onOpenAnnouncements,
                testTag = "settings_announcements"
            )
            SettingsNavTile(
                title = "پیش‌نمایش قلمروهای آینده (۳ تا ۶)",
                subtitle = "جنگل بخشنامه‌ها، برج مکاتبات، آزمایشگاه و میدان قهرمانان",
                icon = Icons.Filled.Explore,
                onClick = onOpenFutureWorlds,
                testTag = "settings_future_worlds"
            )

            PersianOrnateBluePanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("درباره پارسمیندا و قهرمانان تأمین", color = WarmGoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "نسخه ۱.۰ (فاز اول) • ساخته شده با معماری آفلاین‌محور برای تمام خانواده بزرگ سازمان تأمین اجتماعی در شعب، بیمارستان‌ها، مراکز درمانی و ستاد. تمامی پرونده‌ها و داده‌ها تخیلی هستند.",
                        color = CreamText.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsNavTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = ParchmentCream,
        border = BorderStroke(1.5.dp, AntiqueBrass)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = RoyalViolet, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = MutedInk, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SyncStatusScreen(
    uiState: HomeUiState,
    currentUser: FirebaseUser?,
    syncInProgress: Boolean,
    onBack: () -> Unit,
    onSyncNow: () -> Unit,
    onAuthFeedback: (String) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var authLoading by remember { mutableStateOf(false) }

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
                Text("همگام‌سازی آفلاین و ابری", color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    PersianOrnateBluePanel(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (uiState.isOnline) Icons.Filled.CloudQueue else Icons.Filled.CloudOff,
                                    contentDescription = null,
                                    tint = if (uiState.isOnline) JadeEmerald else SaffronGold
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isOnline) "اتصال اینترنت برقرار است" else "حالت آفلاین فعال است",
                                    color = CreamText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = uiState.preferences.lastSyncMessage,
                                color = PersianTurquoiseLight,
                                fontSize = 12.sp
                            )
                            if (uiState.preferences.lastSyncEpochMillis > 0L) {
                                Text(
                                    text = "آخرین همگام‌سازی: ${PersianUtils.millisToJalaliDateTime(uiState.preferences.lastSyncEpochMillis)}",
                                    color = CreamText.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(Modifier.height(10.dp))

                            // Security rule notice
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DeepPersianBlue.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, PersianTurquoise)
                            ) {
                                Text(
                                    text = "نتایج آفلاین با شناسه یکتا ذخیره می‌شوند و پس از اتصال، برای راستی‌آزمایی به سرور ارسال می‌گردند تا امتیازهای تقلبی وارد تالار قهرمانان نشوند.",
                                    color = CreamText.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                // Google Sign-In via Jetpack Credential Manager
                item {
                    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = RoyalViolet)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (currentUser != null) "حساب ابری متصل است" else "اتصال به حساب ابری گوگل",
                                    color = InkBrown,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            if (currentUser != null) {
                                Text(
                                    text = "نشانی متصل: ${currentUser.email ?: "حساب گوگل تأییدشده"}",
                                    color = JadeEmerald,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PersianActionPillButton(
                                        text = if (syncInProgress) "در حال همگام‌سازی..." else "همگام‌سازی فوری",
                                        onClick = onSyncNow,
                                        enabled = !syncInProgress,
                                        containerColor = JadeEmerald,
                                        leadingContent = {
                                            if (syncInProgress) {
                                                CircularProgressIndicator(Modifier.size(16.dp), Color.White, strokeWidth = 2.dp)
                                            } else {
                                                Icon(Icons.Filled.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                            Spacer(Modifier.width(4.dp))
                                        },
                                        modifier = Modifier.weight(1f).testTag("sync_now_button")
                                    )
                                    PersianActionPillButton(
                                        text = "خروج از حساب",
                                        onClick = {
                                            AuthManager.signOut(context, scope) {
                                                onAuthFeedback("خروج از حساب ابری انجام شد. داده‌های آفلاین شما محفوظ است.")
                                            }
                                        },
                                        containerColor = RubyCrimson,
                                        modifier = Modifier.weight(0.8f).testTag("google_sign_out_button")
                                    )
                                }
                            } else {
                                Text(
                                    text = "برای ارسال نتایج در صف و به‌روزرسانی جدول قهرمانان آنلاین، با حساب گوگل وارد شوید. بازی بدون ورود هم به‌صورت آفلاین ادامه دارد.",
                                    color = MutedInk,
                                    fontSize = 12.sp,
                                    lineHeight = 19.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                PersianActionPillButton(
                                    text = if (authLoading) "در حال اتصال..." else "ورود با گوگل برای همگام‌سازی",
                                    onClick = {
                                        authLoading = true
                                        AuthManager.onGoogleSignInClicked(
                                            context = context,
                                            scope = scope,
                                            onAuthSuccess = {
                                                authLoading = false
                                                onAuthFeedback("ورود با موفقیت انجام شد! در حال همگام‌سازی...")
                                                onSyncNow()
                                            },
                                            onAuthError = { msg ->
                                                authLoading = false
                                                onAuthFeedback(msg)
                                            },
                                            onAuthCancelled = {
                                                authLoading = false
                                                onAuthFeedback("ورود لغو شد؛ می‌توانید بعداً دوباره تلاش کنید.")
                                            }
                                        )
                                    },
                                    enabled = !authLoading,
                                    containerColor = RoyalViolet,
                                    modifier = Modifier.fillMaxWidth().testTag("google_sign_in_button")
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "صف عملیات آفلاین (${PersianUtils.toPersianDigits(uiState.sessions.size)} مورد)",
                        color = WarmGoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                items(uiState.sessions, key = { it.operationId }) { session ->
                    val status = SyncStatus.entries.firstOrNull { it.name == session.syncStatus } ?: SyncStatus.PENDING_VERIFICATION
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ParchmentCream,
                        border = BorderStroke(1.2.dp, AntiqueBrass),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(session.stageTitle, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("شناسه یکتا: ${session.operationId.take(14)}...", color = MutedInk, fontSize = 10.sp)
                                Text(session.playedAtJalali, color = MutedInk, fontSize = 11.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = when (status) {
                                    SyncStatus.VERIFIED -> JadeEmerald
                                    SyncStatus.REJECTED -> RubyCrimson
                                    SyncStatus.SYNCED_PENDING_REVIEW -> PersianTurquoise
                                    else -> SaffronGold
                                }
                            ) {
                                Text(
                                    text = status.persianLabel,
                                    color = if (status == SyncStatus.PENDING_VERIFICATION || status == SyncStatus.SYNC_FAILED) InkBrown else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementsScreen(
    uiState: HomeUiState,
    onBack: () -> Unit
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
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = CreamText)
                }
                Text("اطلاعیه‌ها و رویدادهای سرزمین صفر", color = WarmGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }

            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.announcements, key = { it.id }) { ann ->
                    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (ann.isSpecialEvent) RoyalViolet else PersianTurquoise
                                ) {
                                    Text(
                                        text = ann.badgeText,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                                Text(ann.jalaliDate, color = MutedInk, fontSize = 11.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(ann.title, color = InkBrown, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(ann.body, color = MutedInk, fontSize = 13.sp, lineHeight = 21.sp)
                            if (ann.daysRemaining > 0) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "مهلت باقی‌مانده: ${PersianUtils.toPersianDigits(ann.daysRemaining)} روز",
                                    color = RubyCrimson,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
