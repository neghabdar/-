package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.seed.QuestionSeedData
import com.example.data.seed.WorldAndMiniGameSeedData
import com.example.util.PersianUtils

@Database(
    entities = [
        PlayerProfileEntity::class,
        WorldEntity::class,
        StageEntity::class,
        QuestionEntity::class,
        AchievementEntity::class,
        InventoryItemEntity::class,
        GameSessionEntity::class,
        AnnouncementEntity::class,
        LeaderboardCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ParsmindaDatabase : RoomDatabase() {
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun worldAndStageDao(): WorldAndStageDao
    abstract fun questionDao(): QuestionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun announcementAndLeaderboardDao(): AnnouncementAndLeaderboardDao

    suspend fun ensureSeeded() {
        val profileDao = playerProfileDao()
        val worldStageDao = worldAndStageDao()
        val qDao = questionDao()
        if (profileDao.getProfileOnce() == null) {
            profileDao.upsertProfile(
                PlayerProfileEntity(
                    id = "local_player",
                    nickname = "قهرمان تأمین",
                    departmentType = "شعبه نمونه",
                    selectedCharacterId = "hesabgar",
                    xp = 0,
                    coins = 120,
                    medals = 1,
                    score = 0,
                    level = 1,
                    currentStageNumber = 1,
                    dailyMissionProgress = 0,
                    dailyMissionTarget = 3,
                    dailyMissionCompleted = false,
                    dailyMissionDateJalali = PersianUtils.currentJalaliDate(),
                    lastCompletedStageTitle = "آماده آغاز ماجرا در قلعه خزانه"
                )
            )
            worldStageDao.insertWorlds(WorldAndMiniGameSeedData.worlds())
            worldStageDao.insertStages(WorldAndMiniGameSeedData.stages())
            qDao.insertQuestions(QuestionSeedData.allQuestions())
            achievementDao().insertAll(WorldAndMiniGameSeedData.achievements())
            inventoryDao().insertAll(WorldAndMiniGameSeedData.inventoryItems())
            announcementAndLeaderboardDao().insertAnnouncements(WorldAndMiniGameSeedData.announcements())
            announcementAndLeaderboardDao().insertLeaderboard(WorldAndMiniGameSeedData.initialLeaderboard())
        } else if (qDao.getQuestionCount() == 0) {
            qDao.insertQuestions(QuestionSeedData.allQuestions())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ParsmindaDatabase? = null

        fun getInstance(context: Context): ParsmindaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParsmindaDatabase::class.java,
                    "parsminda_game.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
