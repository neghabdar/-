package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profile WHERE id = 'local_player' LIMIT 1")
    fun observeProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 'local_player' LIMIT 1")
    suspend fun getProfileOnce(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: PlayerProfileEntity)
}

@Dao
interface WorldAndStageDao {
    @Query("SELECT * FROM worlds ORDER BY id ASC")
    fun observeWorlds(): Flow<List<WorldEntity>>

    @Query("SELECT * FROM worlds WHERE id = :worldId LIMIT 1")
    suspend fun getWorldById(worldId: Int): WorldEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorlds(worlds: List<WorldEntity>)

    @Update
    suspend fun updateWorld(world: WorldEntity)

    @Query("SELECT * FROM stages ORDER BY stageNumber ASC")
    fun observeAllStages(): Flow<List<StageEntity>>

    @Query("SELECT * FROM stages WHERE worldId = :worldId ORDER BY stageNumber ASC")
    fun observeStagesForWorld(worldId: Int): Flow<List<StageEntity>>

    @Query("SELECT * FROM stages WHERE id = :stageId LIMIT 1")
    suspend fun getStageById(stageId: Int): StageEntity?

    @Query("SELECT * FROM stages WHERE stageNumber = :stageNumber LIMIT 1")
    suspend fun getStageByNumber(stageNumber: Int): StageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStages(stages: List<StageEntity>)

    @Update
    suspend fun updateStage(stage: StageEntity)

    @Query("SELECT COUNT(*) FROM stages WHERE worldId <= 2 AND timesCompleted > 0")
    fun observeCompletedPlayableStageCount(): Flow<Int>
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE publicationStatus = 'APPROVED'")
    suspend fun getAllApprovedQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE publicationStatus = 'APPROVED' AND (worldId = :worldId OR worldId = 0)")
    suspend fun getApprovedQuestionsForWorld(worldId: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements")
    suspend fun getAllOnce(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AchievementEntity>)

    @Update
    suspend fun update(item: AchievementEntity)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY isOwned DESC, priceCoins ASC")
    fun observeInventory(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE category = :category AND isEquipped = 1")
    suspend fun getEquippedInCategory(category: String): List<InventoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryItemEntity>)

    @Update
    suspend fun update(item: InventoryItemEntity)
}

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_sessions ORDER BY playedAtMillis DESC")
    fun observeSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions WHERE syncStatus IN ('PENDING_VERIFICATION', 'SYNC_FAILED') ORDER BY playedAtMillis ASC")
    suspend fun getPendingSyncSessions(): List<GameSessionEntity>

    @Query("SELECT COUNT(*) FROM game_sessions WHERE syncStatus IN ('PENDING_VERIFICATION', 'SYNC_FAILED')")
    fun observePendingSyncCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSession(session: GameSessionEntity)

    @Update
    suspend fun updateSession(session: GameSessionEntity)
}

@Dao
interface AnnouncementAndLeaderboardDao {
    @Query("SELECT * FROM announcements ORDER BY isSpecialEvent DESC, id ASC")
    fun observeAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(items: List<AnnouncementEntity>)

    @Query("SELECT * FROM leaderboard_cache ORDER BY score DESC")
    fun observeLeaderboard(): Flow<List<LeaderboardCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(entries: List<LeaderboardCacheEntity>)

    @Query("DELETE FROM leaderboard_cache WHERE isVerifiedOnline = 1")
    suspend fun clearVerifiedOnlineEntries()
}
