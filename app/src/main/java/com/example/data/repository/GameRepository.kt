package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.local.AchievementEntity
import com.example.data.local.AnnouncementEntity
import com.example.data.local.AppPreferences
import com.example.data.local.GameSessionEntity
import com.example.data.local.InventoryItemEntity
import com.example.data.local.LeaderboardCacheEntity
import com.example.data.local.ParsmindaDatabase
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.QuestionEntity
import com.example.data.local.StageEntity
import com.example.data.local.WorldEntity
import com.example.data.model.LevelProgression
import com.example.data.model.StageRating
import com.example.data.model.SyncStatus
import com.example.data.remote.AuthManager
import com.example.data.remote.EconomyConfig
import com.example.data.remote.FirestoreSyncRepository
import com.example.data.remote.SyncWorker
import com.example.util.PersianUtils
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class StageCompletionSummary(
    val stageId: Int,
    val stageTitle: String,
    val rating: StageRating,
    val baseScore: Int,
    val speedBonus: Int,
    val noMistakeBonus: Int,
    val specialMissionBonus: Int,
    val totalScoreAwarded: Int,
    val xpAwarded: Int,
    val coinsAwarded: Int,
    val isFirstCompletion: Boolean,
    val unlockedWorld2: Boolean,
    val unlockedWorld3Preview: Boolean,
    val operationId: String
)

class GameRepository private constructor(private val context: Context) {
    private val db = ParsmindaDatabase.getInstance(context)
    private val prefs = PreferencesManager(context)
    private val remote = FirestoreSyncRepository(context)

    private var economyConfig = EconomyConfig()

    val profileFlow: Flow<PlayerProfileEntity?> = db.playerProfileDao().observeProfile()
    val worldsFlow: Flow<List<WorldEntity>> = db.worldAndStageDao().observeWorlds()
    val stagesFlow: Flow<List<StageEntity>> = db.worldAndStageDao().observeAllStages()
    val achievementsFlow: Flow<List<AchievementEntity>> = db.achievementDao().observeAchievements()
    val inventoryFlow: Flow<List<InventoryItemEntity>> = db.inventoryDao().observeInventory()
    val sessionsFlow: Flow<List<GameSessionEntity>> = db.gameSessionDao().observeSessions()
    val pendingSyncCountFlow: Flow<Int> = db.gameSessionDao().observePendingSyncCount()
    val announcementsFlow: Flow<List<AnnouncementEntity>> = db.announcementAndLeaderboardDao().observeAnnouncements()
    val leaderboardFlow: Flow<List<LeaderboardCacheEntity>> = db.announcementAndLeaderboardDao().observeLeaderboard()
    val completedPlayableStagesFlow: Flow<Int> = db.worldAndStageDao().observeCompletedPlayableStageCount()
    val preferencesFlow: Flow<AppPreferences> = prefs.preferencesFlow

    suspend fun initializeOfflineData() {
        db.ensureSeeded()
        refreshDailyMissionIfNewDay()
    }

    private suspend fun refreshDailyMissionIfNewDay() {
        val profile = db.playerProfileDao().getProfileOnce() ?: return
        val today = PersianUtils.currentJalaliDate()
        if (profile.dailyMissionDateJalali != today) {
            db.playerProfileDao().upsertProfile(
                profile.copy(
                    dailyMissionProgress = 0,
                    dailyMissionCompleted = false,
                    dailyMissionDateJalali = today
                )
            )
        }
    }

    suspend fun completeOnboarding(nickname: String, characterId: String, departmentType: String) {
        db.ensureSeeded()
        val current = db.playerProfileDao().getProfileOnce() ?: PlayerProfileEntity()
        val cleanNick = nickname.trim().ifEmpty { "قهرمان تأمین" }.take(28)
        db.playerProfileDao().upsertProfile(
            current.copy(
                nickname = cleanNick,
                selectedCharacterId = characterId,
                departmentType = departmentType,
                dailyMissionDateJalali = PersianUtils.currentJalaliDate()
            )
        )
        prefs.setOnboardingCompleted(true)
        updateLocalPlayerInLeaderboard(cleanNick, characterId, current.level, current.score)
    }

    suspend fun updateProfileIdentity(nickname: String, characterId: String, departmentType: String) {
        val current = db.playerProfileDao().getProfileOnce() ?: return
        val cleanNick = nickname.trim().ifEmpty { current.nickname }.take(28)
        db.playerProfileDao().upsertProfile(
            current.copy(
                nickname = cleanNick,
                selectedCharacterId = characterId,
                departmentType = departmentType
            )
        )
        updateLocalPlayerInLeaderboard(cleanNick, characterId, current.level, current.score)
    }

    suspend fun setSoundEnabled(enabled: Boolean) = prefs.setSoundEnabled(enabled)
    suspend fun setNotificationsEnabled(enabled: Boolean) = prefs.setNotificationsEnabled(enabled)

    suspend fun getStageById(stageId: Int): StageEntity? = db.worldAndStageDao().getStageById(stageId)

    suspend fun getQuestionsForStage(worldId: Int, count: Int): List<QuestionEntity> {
        val pool = db.questionDao().getApprovedQuestionsForWorld(worldId)
        val entertainment = pool.filter { it.category == "ENTERTAINMENT" }.shuffled()
        val specialized = pool.filter { it.category == "SPECIALIZED" }.shuffled()
        val desiredEntertainment = (count * 6) / 10
        val picked = mutableListOf<QuestionEntity>()
        picked.addAll(entertainment.take(desiredEntertainment))
        picked.addAll(specialized.take(count - picked.size))
        if (picked.size < count) {
            picked.addAll((pool - picked.toSet()).shuffled().take(count - picked.size))
        }
        return picked.shuffled()
    }

    suspend fun recordStageResult(
        stageId: Int,
        correctCount: Int,
        totalCount: Int,
        mistakes: Int,
        durationSeconds: Int
    ): StageCompletionSummary {
        val stage = db.worldAndStageDao().getStageById(stageId)
            ?: throw IllegalStateException("Stage $stageId not found")
        val profile = db.playerProfileDao().getProfileOnce()
            ?: PlayerProfileEntity()

        val ratio = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f
        val rating = when {
            ratio >= 0.99f && mistakes == 0 -> StageRating.GOLD
            ratio >= 0.66f -> StageRating.SILVER
            else -> StageRating.BRONZE
        }

        val isFirstClear = stage.timesCompleted == 0
        val baseScore = correctCount * economyConfig.baseCorrectScore
        val speedBonus = if (durationSeconds <= 30) economyConfig.speedBonusMax else if (durationSeconds <= 60) 15 else 5
        val noMistakeBonus = if (mistakes == 0) economyConfig.noMistakeBonus else 0

        val prevDailyProgress = profile.dailyMissionProgress
        val newDailyProgress = (prevDailyProgress + correctCount).coerceAtMost(profile.dailyMissionTarget)
        val justFinishedDaily = !profile.dailyMissionCompleted && newDailyProgress >= profile.dailyMissionTarget
        val specialBonus = if (justFinishedDaily) economyConfig.specialMissionBonus else 0

        // Prevent unlimited repeatable reward farming on replayed stages
        val totalScoreAwarded = if (isFirstClear) {
            baseScore + speedBonus + noMistakeBonus + specialBonus
        } else {
            val improved = rating.stars > ratingStars(stage.bestRating)
            (if (improved) 40 else 10) + specialBonus
        }

        val xpAwarded = if (isFirstClear) {
            stage.xpReward + (if (justFinishedDaily) 40 else 0)
        } else {
            15 + (if (justFinishedDaily) 40 else 0)
        }

        val coinsAwarded = if (isFirstClear) {
            stage.coinReward + (if (justFinishedDaily) 150 else 0)
        } else {
            val improved = rating.stars > ratingStars(stage.bestRating)
            (if (improved) 10 else 0) + (if (justFinishedDaily) 150 else 0)
        }

        val newRating = if (rating.stars > ratingStars(stage.bestRating)) rating.name else stage.bestRating
        val newBestScore = maxOf(stage.bestScore, totalScoreAwarded)

        db.worldAndStageDao().updateStage(
            stage.copy(
                timesCompleted = stage.timesCompleted + 1,
                bestRating = newRating,
                bestScore = newBestScore
            )
        )

        // Unlock next stage
        var unlockedWorld2 = false
        var unlockedWorld3Preview = false
        val nextStage = db.worldAndStageDao().getStageByNumber(stage.stageNumber + 1)
        if (nextStage != null && nextStage.worldId <= 2 && !nextStage.isUnlocked) {
            db.worldAndStageDao().updateStage(nextStage.copy(isUnlocked = true))
        }
        if (stage.stageNumber == 5) {
            val world2 = db.worldAndStageDao().getWorldById(2)
            if (world2 != null && !world2.isUnlocked) {
                db.worldAndStageDao().updateWorld(world2.copy(isUnlocked = true))
                unlockedWorld2 = true
            }
        }
        if (stage.stageNumber == 10 && isFirstClear) {
            unlockedWorld3Preview = true
        }

        val newXp = profile.xp + xpAwarded
        val newRank = LevelProgression.rankForXp(newXp)
        val newScore = profile.score + totalScoreAwarded
        val newCoins = profile.coins + coinsAwarded
        val nextStageNumber = maxOf(profile.currentStageNumber, (stage.stageNumber + 1).coerceAtMost(10))

        val updatedProfile = profile.copy(
            xp = newXp,
            coins = newCoins,
            score = newScore,
            level = newRank.level,
            currentStageNumber = nextStageNumber,
            dailyMissionProgress = newDailyProgress,
            dailyMissionCompleted = profile.dailyMissionCompleted || justFinishedDaily,
            dailyMissionDateJalali = PersianUtils.currentJalaliDate(),
            lastCompletedStageTitle = "مرحله ${PersianUtils.toPersianDigits(stage.stageNumber)}: ${stage.title}"
        )
        db.playerProfileDao().upsertProfile(updatedProfile)

        val opId = "op_${UUID.randomUUID().toString().replace("-", "")}"
        val nowMillis = System.currentTimeMillis()
        val session = GameSessionEntity(
            operationId = opId,
            stageId = stage.id,
            stageTitle = stage.title,
            miniGameType = stage.miniGameType,
            rawScore = totalScoreAwarded,
            xpAwarded = xpAwarded,
            coinsAwarded = coinsAwarded,
            rating = rating.name,
            durationSeconds = durationSeconds,
            correctCount = correctCount,
            totalCount = totalCount,
            playedAtMillis = nowMillis,
            playedAtJalali = PersianUtils.millisToJalaliDateTime(nowMillis),
            syncStatus = SyncStatus.PENDING_VERIFICATION.name
        )
        db.gameSessionDao().insertSession(session)

        evaluateAchievements(stage, rating, justFinishedDaily, mistakes)
        updateLocalPlayerInLeaderboard(
            updatedProfile.nickname,
            updatedProfile.selectedCharacterId,
            updatedProfile.level,
            updatedProfile.score
        )

        // Enqueue reliable background WorkManager sync when online
        if (AuthManager.authOrNull(context)?.currentUser != null) {
            SyncWorker.enqueueImmediateSync(context)
        }

        return StageCompletionSummary(
            stageId = stage.id,
            stageTitle = stage.title,
            rating = rating,
            baseScore = baseScore,
            speedBonus = speedBonus,
            noMistakeBonus = noMistakeBonus,
            specialMissionBonus = specialBonus,
            totalScoreAwarded = totalScoreAwarded,
            xpAwarded = xpAwarded,
            coinsAwarded = coinsAwarded,
            isFirstCompletion = isFirstClear,
            unlockedWorld2 = unlockedWorld2,
            unlockedWorld3Preview = unlockedWorld3Preview,
            operationId = opId
        )
    }

    private fun ratingStars(ratingName: String): Int {
        return StageRating.entries.firstOrNull { it.name == ratingName }?.stars ?: 0
    }

    private suspend fun evaluateAchievements(
        stage: StageEntity,
        rating: StageRating,
        dailyDone: Boolean,
        mistakes: Int
    ) {
        val all = db.achievementDao().getAllOnce()
        val today = PersianUtils.currentJalaliDate()
        var bonusMedals = 0
        var bonusCoins = 0

        for (ach in all) {
            if (ach.isUnlocked) continue
            val newProgress = when (ach.id) {
                "ach_first_step" -> 1
                "ach_vault_master" -> if (stage.miniGameType == "VAULT_PUZZLE") ach.progress + 1 else ach.progress
                "ach_treasure_hunter" -> if (stage.miniGameType == "MISSING_FILE" && mistakes == 0) ach.progress + 1 else ach.progress
                "ach_dragon_tamer" -> if (stage.stageNumber == 5) 1 else ach.progress
                "ach_giant_slayer" -> if (stage.stageNumber == 10) 1 else ach.progress
                "ach_daily_hero" -> if (dailyDone) 1 else ach.progress
                "ach_gold_collector" -> if (rating == StageRating.GOLD) ach.progress + 1 else ach.progress
                else -> ach.progress
            }
            val unlocked = newProgress >= ach.target
            if (newProgress != ach.progress || unlocked) {
                db.achievementDao().update(
                    ach.copy(
                        progress = newProgress.coerceAtMost(ach.target),
                        isUnlocked = unlocked,
                        unlockedAtJalali = if (unlocked) today else ""
                    )
                )
                if (unlocked) {
                    bonusMedals += ach.medalReward
                    bonusCoins += ach.coinReward
                }
            }
        }

        if (bonusMedals > 0 || bonusCoins > 0) {
            val profile = db.playerProfileDao().getProfileOnce() ?: return
            db.playerProfileDao().upsertProfile(
                profile.copy(
                    medals = profile.medals + bonusMedals,
                    coins = profile.coins + bonusCoins
                )
            )
        }
    }

    suspend fun purchaseOrEquipCosmetic(itemId: String): Result<String> {
        val item = db.inventoryDao().getById(itemId)
            ?: return Result.failure(IllegalArgumentException("آیتم یافت نشد."))
        val profile = db.playerProfileDao().getProfileOnce()
            ?: return Result.failure(IllegalStateException("پروفایل یافت نشد."))

        if (!item.isOwned) {
            if (profile.coins < item.priceCoins) {
                return Result.failure(IllegalStateException("سکه کافی ندارید! با تکمیل مراحل سکه بیشتر بگیرید."))
            }
            db.playerProfileDao().upsertProfile(profile.copy(coins = profile.coins - item.priceCoins))
            // Unlock bazaar achievement
            val allAch = db.achievementDao().getAllOnce()
            val bazaarAch = allAch.firstOrNull { it.id == "ach_bazaar_buyer" && !it.isUnlocked }
            if (bazaarAch != null) {
                db.achievementDao().update(
                    bazaarAch.copy(
                        progress = 1,
                        isUnlocked = true,
                        unlockedAtJalali = PersianUtils.currentJalaliDate()
                    )
                )
                val updated = db.playerProfileDao().getProfileOnce()
                if (updated != null) {
                    db.playerProfileDao().upsertProfile(
                        updated.copy(
                            medals = updated.medals + bazaarAch.medalReward,
                            coins = updated.coins + bazaarAch.coinReward
                        )
                    )
                }
            }
        }

        // Equip exclusively within category
        val currentlyEquipped = db.inventoryDao().getEquippedInCategory(item.category)
        for (eq in currentlyEquipped) {
            if (eq.id != item.id) {
                db.inventoryDao().update(eq.copy(isEquipped = false))
            }
        }
        db.inventoryDao().update(item.copy(isOwned = true, isEquipped = true))
        return Result.success(if (item.isOwned) "آیتم «${item.title}» فعال شد." else "آیتم «${item.title}» خریداری و فعال شد!")
    }

    private suspend fun updateLocalPlayerInLeaderboard(
        nickname: String,
        characterId: String,
        level: Int,
        score: Int
    ) {
        db.announcementAndLeaderboardDao().insertLeaderboard(
            listOf(
                LeaderboardCacheEntity(
                    id = "lb_current_user",
                    rank = 0,
                    displayName = nickname,
                    characterId = characterId,
                    level = level,
                    score = score,
                    isCurrentUser = true,
                    isVerifiedOnline = false
                )
            )
        )
    }

    fun isNetworkConnected(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (_: Exception) {
            false
        }
    }

    suspend fun synchronizeNow(): Result<String> {
        if (!isNetworkConnected()) {
            val msg = "حالت آفلاین: پیشرفت شما در حافظه امن دستگاه ذخیره است."
            prefs.recordSyncOutcome(System.currentTimeMillis(), msg)
            return Result.failure(IllegalStateException(msg))
        }
        val uid = AuthManager.authOrNull(context)?.currentUser?.uid
        if (uid.isNullOrBlank()) {
            val msg = if (AuthManager.isFirebaseAvailable(context)) {
                "برای همگام‌سازی ابری، در بخش همگام‌سازی وارد حساب گوگل شوید."
            } else {
                "حالت آفلاین کامل: اطلاعات بازی به‌طور کامل روی دستگاه شما ذخیره می‌شود."
            }
            prefs.recordSyncOutcome(System.currentTimeMillis(), msg)
            return Result.failure(IllegalStateException(msg))
        }

        val profile = db.playerProfileDao().getProfileOnce() ?: return Result.failure(IllegalStateException("پروفایل یافت نشد"))

        // 1. Sync profile (preserving newer authoritative server XP/coins/score if higher)
        val profileSync = remote.syncUserProfile(profile)
        if (profileSync.isSuccess) {
            val remoteProfile = profileSync.getOrNull()
            if (remoteProfile != null && remoteProfile.authoritativeXp > profile.xp) {
                val mergedXp = remoteProfile.authoritativeXp.toInt()
                db.playerProfileDao().upsertProfile(
                    profile.copy(
                        firebaseUid = uid,
                        xp = mergedXp,
                        level = LevelProgression.rankForXp(mergedXp).level,
                        coins = maxOf(profile.coins, remoteProfile.authoritativeCoins.toInt()),
                        medals = maxOf(profile.medals, remoteProfile.authoritativeMedals.toInt()),
                        score = maxOf(profile.score, remoteProfile.authoritativeScore.toInt()),
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                )
            } else {
                db.playerProfileDao().upsertProfile(
                    profile.copy(firebaseUid = uid, lastSyncTimestamp = System.currentTimeMillis())
                )
            }
        }

        // 2. Upload pending sessions idempotently
        val pending = db.gameSessionDao().getPendingSyncSessions()
        var uploadedCount = 0
        if (pending.isNotEmpty()) {
            val uploadRes = remote.uploadPendingSessions(pending)
            val statusMap = uploadRes.getOrNull().orEmpty()
            for (session in pending) {
                val newStatus = statusMap[session.operationId] ?: SyncStatus.SYNC_FAILED
                if (newStatus != SyncStatus.SYNC_FAILED) uploadedCount++
                db.gameSessionDao().updateSession(
                    session.copy(
                        syncStatus = newStatus.name,
                        retryCount = if (newStatus == SyncStatus.SYNC_FAILED) session.retryCount + 1 else session.retryCount,
                        lastError = if (newStatus == SyncStatus.SYNC_FAILED) "خطای موقت ارسال" else null
                    )
                )
            }
        }

        // 3. Refresh verification statuses from server
        remote.refreshSubmittedSessionStatuses(uid).getOrNull()?.let { statuses ->
            val allSessions = db.gameSessionDao().getPendingSyncSessions()
            // Keep existing local history intact even if rejected
            for (s in allSessions) {
                statuses[s.operationId]?.let { serverStatus ->
                    db.gameSessionDao().updateSession(s.copy(syncStatus = serverStatus.name))
                }
            }
        }

        // 4. Refresh verified leaderboard, announcements, and economy settings
        remote.fetchVerifiedLeaderboard().getOrNull()?.let { entries ->
            if (entries.isNotEmpty()) {
                db.announcementAndLeaderboardDao().clearVerifiedOnlineEntries()
                db.announcementAndLeaderboardDao().insertLeaderboard(entries)
            }
        }
        remote.fetchPublishedAnnouncements().getOrNull()?.let { remoteAnnouncements ->
            if (remoteAnnouncements.isNotEmpty()) {
                db.announcementAndLeaderboardDao().insertAnnouncements(remoteAnnouncements)
            }
        }
        remote.fetchEconomyConfig().getOrNull()?.let { config ->
            economyConfig = config
        }

        val now = System.currentTimeMillis()
        val summaryMsg = "همگام‌سازی کامل شد • ${PersianUtils.toPersianDigits(uploadedCount)} عملیات ارسال گردید"
        prefs.recordSyncOutcome(now, summaryMsg)
        return Result.success(summaryMsg)
    }

    companion object {
        @Volatile
        private var INSTANCE: GameRepository? = null

        fun getInstance(context: Context): GameRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = GameRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
