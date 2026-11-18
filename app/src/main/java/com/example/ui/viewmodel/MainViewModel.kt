package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AchievementEntity
import com.example.data.local.AnnouncementEntity
import com.example.data.local.AppPreferences
import com.example.data.local.GameSessionEntity
import com.example.data.local.InventoryItemEntity
import com.example.data.local.LeaderboardCacheEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.StageEntity
import com.example.data.local.WorldEntity
import com.example.data.remote.AuthManager
import com.example.data.remote.SyncWorker
import com.example.data.repository.GameRepository
import com.example.data.repository.StageCompletionSummary
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isInitialized: Boolean = false,
    val profile: PlayerProfileEntity = PlayerProfileEntity(),
    val worlds: List<WorldEntity> = emptyList(),
    val stages: List<StageEntity> = emptyList(),
    val achievements: List<AchievementEntity> = emptyList(),
    val inventory: List<InventoryItemEntity> = emptyList(),
    val sessions: List<GameSessionEntity> = emptyList(),
    val announcements: List<AnnouncementEntity> = emptyList(),
    val leaderboard: List<LeaderboardCacheEntity> = emptyList(),
    val preferences: AppPreferences = AppPreferences(),
    val pendingSyncCount: Int = 0,
    val completedPlayableStages: Int = 0,
    val isOnline: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository.getInstance(application)

    private val _isInitialized = MutableStateFlow(false)
    private val _syncInProgress = MutableStateFlow(false)
    val syncInProgress: StateFlow<Boolean> = _syncInProgress.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _lastCompletion = MutableStateFlow<StageCompletionSummary?>(null)
    val lastCompletion: StateFlow<StageCompletionSummary?> = _lastCompletion.asStateFlow()

    val currentUser: StateFlow<FirebaseUser?> = AuthManager.authStateFlow(AuthManager.authOrNull(application))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AuthManager.authOrNull(application)?.currentUser
        )

    private val coreFlows = combine(
        _isInitialized,
        repository.profileFlow,
        repository.worldsFlow,
        repository.stagesFlow,
        repository.preferencesFlow
    ) { initialized, profile, worlds, stages, prefs ->
        CoreBundle(initialized, profile ?: PlayerProfileEntity(), worlds, stages, prefs)
    }

    private val metaFlows = combine(
        repository.achievementsFlow,
        repository.inventoryFlow,
        repository.sessionsFlow,
        repository.announcementsFlow,
        repository.leaderboardFlow
    ) { achievements, inventory, sessions, announcements, leaderboard ->
        MetaBundle(achievements, inventory, sessions, announcements, leaderboard)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        coreFlows,
        metaFlows,
        repository.pendingSyncCountFlow,
        repository.completedPlayableStagesFlow
    ) { core, meta, pendingCount, completedCount ->
        HomeUiState(
            isInitialized = core.initialized,
            profile = core.profile,
            worlds = core.worlds,
            stages = core.stages,
            achievements = meta.achievements,
            inventory = meta.inventory,
            sessions = meta.sessions,
            announcements = meta.announcements,
            leaderboard = meta.leaderboard,
            preferences = core.prefs,
            pendingSyncCount = pendingCount,
            completedPlayableStages = completedCount,
            isOnline = repository.isNetworkConnected()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            repository.initializeOfflineData()
            _isInitialized.value = true
            SyncWorker.schedulePeriodicSync(getApplication())
        }
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun showBanner(msg: String) {
        _statusBanner.value = msg
    }

    fun completeOnboarding(nickname: String, characterId: String, departmentType: String) {
        viewModelScope.launch {
            repository.completeOnboarding(nickname, characterId, departmentType)
        }
    }

    fun updateIdentity(nickname: String, characterId: String, departmentType: String) {
        viewModelScope.launch {
            repository.updateProfileIdentity(nickname, characterId, departmentType)
            _statusBanner.value = "مشخصات قهرمان با موفقیت به‌روزرسانی شد."
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(enabled) }
    }

    suspend fun loadQuestionsForStage(worldId: Int, count: Int): List<QuestionEntity> {
        return repository.getQuestionsForStage(worldId, count)
    }

    suspend fun loadStage(stageId: Int): StageEntity? {
        return repository.getStageById(stageId)
    }

    fun submitStageResult(
        stageId: Int,
        correctCount: Int,
        totalCount: Int,
        mistakes: Int,
        durationSeconds: Int,
        onComplete: (StageCompletionSummary) -> Unit
    ) {
        viewModelScope.launch {
            val summary = repository.recordStageResult(
                stageId = stageId,
                correctCount = correctCount,
                totalCount = totalCount,
                mistakes = mistakes,
                durationSeconds = durationSeconds
            )
            _lastCompletion.value = summary
            onComplete(summary)
        }
    }

    fun purchaseOrEquipCosmetic(itemId: String) {
        viewModelScope.launch {
            val res = repository.purchaseOrEquipCosmetic(itemId)
            _statusBanner.value = res.getOrElse { it.message ?: "خطا در انجام عملیات" }
        }
    }

    fun synchronizeNow() {
        viewModelScope.launch {
            if (_syncInProgress.value) return@launch
            _syncInProgress.value = true
            val res = repository.synchronizeNow()
            _syncInProgress.value = false
            _statusBanner.value = res.getOrElse { it.message ?: "همگام‌سازی انجام نشد." }
        }
    }

    private data class CoreBundle(
        val initialized: Boolean,
        val profile: PlayerProfileEntity,
        val worlds: List<WorldEntity>,
        val stages: List<StageEntity>,
        val prefs: AppPreferences
    )

    private data class MetaBundle(
        val achievements: List<AchievementEntity>,
        val inventory: List<InventoryItemEntity>,
        val sessions: List<GameSessionEntity>,
        val announcements: List<AnnouncementEntity>,
        val leaderboard: List<LeaderboardCacheEntity>
    )
}
