package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ParsmindaDatabase
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.SyncStatus
import com.example.data.repository.GameRepository
import com.example.data.seed.WorldAndMiniGameSeedData
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ParsmindaTheme
import com.example.ui.viewmodel.HomeUiState
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }
    }

    @Test
    fun appName_isLocalizedPersianTitle() {
        val appName = context.getString(R.string.app_name)
        assertEquals("پارسمیندا و قهرمانان تأمین", appName)
    }

    @Test
    fun offlineRepository_seedsProgressUnlocksPreventsCoinFarmingAndQueuesSync() = runBlocking {
        val repo = GameRepository.getInstance(context)
        repo.initializeOfflineData()
        repo.completeOnboarding("قهرمان آزمایشی", "hesabgar", "شعبه نمونه")

        val initialProfile = repo.profileFlow.first()!!
        assertEquals("قهرمان آزمایشی", initialProfile.nickname)
        assertEquals(1, initialProfile.currentStageNumber)

        // Complete Stage 1 cleanly
        val firstClear = repo.recordStageResult(
            stageId = 1,
            correctCount = 3,
            totalCount = 3,
            mistakes = 0,
            durationSeconds = 20
        )
        assertTrue(firstClear.isFirstCompletion)
        assertTrue(firstClear.coinsAwarded > 0)
        assertTrue(firstClear.operationId.startsWith("op_"))

        val stagesAfter1 = repo.stagesFlow.first()
        assertTrue("Stage 2 must unlock after Stage 1 clear", stagesAfter1.first { it.stageNumber == 2 }.isUnlocked)

        // Replay Stage 1 with same perfect rating: anti-farming awards 0 base replay coins
        val replay = repo.recordStageResult(
            stageId = 1,
            correctCount = 3,
            totalCount = 3,
            mistakes = 0,
            durationSeconds = 20
        )
        assertFalse(replay.isFirstCompletion)
        assertEquals(0, replay.coinsAwarded)

        // Sessions should be queued as PENDING_VERIFICATION for future cloud sync
        val sessions = repo.sessionsFlow.first()
        assertTrue(sessions.size >= 2)
        assertTrue(sessions.all { it.syncStatus == SyncStatus.PENDING_VERIFICATION.name })

        // Complete Stage 5 -> must unlock World 2
        val stage5Summary = repo.recordStageResult(
            stageId = 5,
            correctCount = 4,
            totalCount = 4,
            mistakes = 0,
            durationSeconds = 25
        )
        assertTrue(stage5Summary.unlockedWorld2)
        val worlds = repo.worldsFlow.first()
        assertTrue(worlds.first { it.id == 2 }.isUnlocked)
    }

    @Test
    fun homeScreen_rendersCorePanelsAndTriggersActions() {
        var continuedStageId = -1
        var leaderboardOpened = false

        val fakeState = HomeUiState(
            isInitialized = true,
            profile = PlayerProfileEntity(
                nickname = "استاد حسابگر",
                selectedCharacterId = "hesabgar",
                xp = 720,
                coins = 2350,
                medals = 12,
                score = 1850,
                level = 5,
                currentStageNumber = 3
            ),
            worlds = WorldAndMiniGameSeedData.worlds(),
            stages = WorldAndMiniGameSeedData.stages(),
            achievements = WorldAndMiniGameSeedData.achievements(),
            inventory = WorldAndMiniGameSeedData.inventoryItems(),
            announcements = WorldAndMiniGameSeedData.announcements(),
            leaderboard = WorldAndMiniGameSeedData.initialLeaderboard()
        )

        composeRule.setContent {
            ParsmindaTheme {
                HomeScreen(
                    uiState = fakeState,
                    onSelectBottomTab = {},
                    onContinueStory = { continuedStageId = it },
                    onOpenDailyMission = {},
                    onOpenLeaderboard = { leaderboardOpened = true },
                    onOpenAnnouncements = {},
                    onOpenSettings = {},
                    onOpenInventory = {},
                    onOpenAchievements = {},
                    onOpenProfile = {}
                )
            }
        }

        composeRule.waitForIdle()

        composeRule.onNodeWithTag("home_continue_story_card").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("home_continue_play_button").performScrollTo().performClick()

        composeRule.onNodeWithTag("home_daily_mission_card").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("home_leaderboard_card").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("home_view_leaderboard_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(3, continuedStageId)
        assertTrue(leaderboardOpened)
    }
}
