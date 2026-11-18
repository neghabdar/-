package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: String = "local_player",
    val firebaseUid: String? = null,
    val nickname: String = "قهرمان تازه",
    val departmentType: String = "شعبه نمونه",
    val selectedCharacterId: String = "hesabgar",
    val xp: Int = 0,
    val coins: Int = 120,
    val medals: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val currentStageNumber: Int = 1,
    val dailyMissionProgress: Int = 0,
    val dailyMissionTarget: Int = 3,
    val dailyMissionCompleted: Boolean = false,
    val dailyMissionDateJalali: String = "",
    val lastCompletedStageTitle: String = "هنوز مرحله‌ای کامل نشده",
    val lastSyncTimestamp: Long = 0L,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "worlds")
data class WorldEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val bossName: String,
    val stageStart: Int,
    val stageEnd: Int,
    val isUnlocked: Boolean,
    val isFutureContent: Boolean
)

@Entity(tableName = "stages")
data class StageEntity(
    @PrimaryKey val id: Int,
    val worldId: Int,
    val stageNumber: Int,
    val title: String,
    val storyIntro: String,
    val objective: String,
    val miniGameType: String,
    val difficulty: Int,
    val xpReward: Int,
    val coinReward: Int,
    val isBossStage: Boolean,
    val isUnlocked: Boolean,
    val bestRating: String = "NONE",
    val bestScore: Int = 0,
    val timesCompleted: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int,
    val explanation: String,
    val category: String,
    val subCategory: String,
    val difficulty: Int,
    val questionType: String,
    val source: String,
    val version: Int,
    val publicationStatus: String,
    val worldId: Int
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val medalReward: Int,
    val coinReward: Int,
    val isUnlocked: Boolean,
    val unlockedAtJalali: String = "",
    val progress: Int,
    val target: Int,
    val iconKey: String
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priceCoins: Int,
    val isOwned: Boolean,
    val isEquipped: Boolean
)

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey val operationId: String,
    val stageId: Int,
    val stageTitle: String,
    val miniGameType: String,
    val rawScore: Int,
    val xpAwarded: Int,
    val coinsAwarded: Int,
    val rating: String,
    val durationSeconds: Int,
    val correctCount: Int,
    val totalCount: Int,
    val playedAtMillis: Long,
    val playedAtJalali: String,
    val syncStatus: String,
    val retryCount: Int = 0,
    val lastError: String? = null
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val badgeText: String,
    val jalaliDate: String,
    val isSpecialEvent: Boolean,
    val daysRemaining: Int
)

@Entity(tableName = "leaderboard_cache")
data class LeaderboardCacheEntity(
    @PrimaryKey val id: String,
    val rank: Int,
    val displayName: String,
    val characterId: String,
    val level: Int,
    val score: Int,
    val isCurrentUser: Boolean,
    val isVerifiedOnline: Boolean
)
