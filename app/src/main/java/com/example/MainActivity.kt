package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.QuestionEntity
import com.example.data.local.StageEntity
import com.example.data.remote.AuthManager
import com.example.ui.components.BottomNavTab
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AnnouncementsScreen
import com.example.ui.screens.CharacterSelectionScreen
import com.example.ui.screens.FutureWorldsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MiniGamePlayScreen
import com.example.ui.screens.MissionIntroScreen
import com.example.ui.screens.MissionResultScreen
import com.example.ui.screens.NicknameSetupScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StageSelectionScreen
import com.example.ui.screens.SyncStatusScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.WorldDetailScreen
import com.example.ui.screens.WorldMapScreen
import com.example.ui.theme.ParsmindaTheme
import com.example.ui.viewmodel.MainViewModel

object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val NICKNAME = "nickname"
    const val CHARACTER_ONBOARD = "character_onboard"
    const val CHARACTER_EDIT = "character_edit"
    const val HOME = "home"
    const val MAP = "map"
    const val ACHIEVEMENTS = "achievements"
    const val INVENTORY = "inventory"
    const val PROFILE = "profile"
    const val WORLD_DETAIL = "world_detail/{worldId}"
    const val STAGE_SELECT = "stage_select/{worldId}"
    const val MISSION_INTRO = "mission_intro/{stageId}"
    const val MINI_GAME = "mini_game/{stageId}"
    const val MISSION_RESULT = "mission_result"
    const val SETTINGS = "settings"
    const val SYNC_STATUS = "sync_status"
    const val ANNOUNCEMENTS = "announcements"
    const val FUTURE_WORLDS = "future_worlds"
    const val LEADERBOARD = "leaderboard"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParsmindaTheme {
                ParsmindaApp()
            }
        }
    }
}

@Composable
fun ParsmindaApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val syncInProgress by viewModel.syncInProgress.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val lastCompletion by viewModel.lastCompletion.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendingNickname by rememberSaveable { mutableStateOf("قهرمان تأمین") }
    var pendingDepartment by rememberSaveable { mutableStateOf("شعبه") }

    // Attempt silent auto-sign-in when authorized Google credential exists; never blocks offline play
    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(context, scope)
    }

    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBanner()
        }
    }

    fun navigateBottomTab(tab: BottomNavTab) {
        navController.navigate(tab.route) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH
            ) {
                composable(Routes.SPLASH) {
                    SplashScreen(
                        isReady = uiState.isInitialized,
                        onboardingCompleted = uiState.preferences.onboardingCompleted,
                        onNavigateNext = { onboarded ->
                            val target = if (onboarded) Routes.HOME else Routes.WELCOME
                            navController.navigate(target) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.WELCOME) {
                    WelcomeScreen(
                        onContinue = { navController.navigate(Routes.NICKNAME) }
                    )
                }

                composable(Routes.NICKNAME) {
                    NicknameSetupScreen(
                        initialNickname = uiState.profile.nickname,
                        initialDepartment = uiState.profile.departmentType,
                        onBack = { navController.popBackStack() },
                        onNext = { nick, dept ->
                            pendingNickname = nick
                            pendingDepartment = dept
                            navController.navigate(Routes.CHARACTER_ONBOARD)
                        }
                    )
                }

                composable(Routes.CHARACTER_ONBOARD) {
                    CharacterSelectionScreen(
                        initialCharacterId = uiState.profile.selectedCharacterId,
                        isEditMode = false,
                        onBack = { navController.popBackStack() },
                        onConfirmCharacter = { heroId ->
                            viewModel.completeOnboarding(pendingNickname, heroId, pendingDepartment)
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.WELCOME) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.CHARACTER_EDIT) {
                    CharacterSelectionScreen(
                        initialCharacterId = uiState.profile.selectedCharacterId,
                        isEditMode = true,
                        onBack = { navController.popBackStack() },
                        onConfirmCharacter = { heroId ->
                            viewModel.updateIdentity(
                                uiState.profile.nickname,
                                heroId,
                                uiState.profile.departmentType
                            )
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.HOME) {
                    HomeScreen(
                        uiState = uiState,
                        onSelectBottomTab = ::navigateBottomTab,
                        onContinueStory = { stageId ->
                            navController.navigate("mission_intro/$stageId")
                        },
                        onOpenDailyMission = {
                            val currentStageId = uiState.stages
                                .firstOrNull { it.stageNumber == uiState.profile.currentStageNumber }?.id ?: 1
                            navController.navigate("mission_intro/$currentStageId")
                        },
                        onOpenLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                        onOpenAnnouncements = { navController.navigate(Routes.ANNOUNCEMENTS) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onOpenInventory = { navigateBottomTab(BottomNavTab.INVENTORY) },
                        onOpenAchievements = { navigateBottomTab(BottomNavTab.ACHIEVEMENTS) },
                        onOpenProfile = { navigateBottomTab(BottomNavTab.PROFILE) }
                    )
                }

                composable(Routes.MAP) {
                    WorldMapScreen(
                        worlds = uiState.worlds,
                        stages = uiState.stages,
                        onSelectWorld = { world ->
                            navController.navigate("world_detail/${world.id}")
                        },
                        onOpenFutureWorlds = { navController.navigate(Routes.FUTURE_WORLDS) },
                        onSelectBottomTab = ::navigateBottomTab
                    )
                }

                composable(
                    route = Routes.WORLD_DETAIL,
                    arguments = listOf(navArgument("worldId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val worldId = backStackEntry.arguments?.getInt("worldId") ?: 1
                    val world = uiState.worlds.firstOrNull { it.id == worldId } ?: uiState.worlds.firstOrNull()
                    val worldStages = uiState.stages.filter { it.worldId == worldId }
                    if (world != null) {
                        WorldDetailScreen(
                            world = world,
                            stages = worldStages,
                            onBack = { navController.popBackStack() },
                            onOpenStages = { navController.navigate("stage_select/$worldId") },
                            onSelectStage = { st -> navController.navigate("mission_intro/${st.id}") }
                        )
                    }
                }

                composable(
                    route = Routes.STAGE_SELECT,
                    arguments = listOf(navArgument("worldId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val worldId = backStackEntry.arguments?.getInt("worldId") ?: 1
                    val world = uiState.worlds.firstOrNull { it.id == worldId } ?: uiState.worlds.firstOrNull()
                    val worldStages = uiState.stages.filter { it.worldId == worldId }
                    if (world != null) {
                        StageSelectionScreen(
                            world = world,
                            stages = worldStages,
                            onBack = { navController.popBackStack() },
                            onSelectStage = { st -> navController.navigate("mission_intro/${st.id}") }
                        )
                    }
                }

                composable(
                    route = Routes.MISSION_INTRO,
                    arguments = listOf(navArgument("stageId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val stageId = backStackEntry.arguments?.getInt("stageId") ?: 1
                    val stage = uiState.stages.firstOrNull { it.id == stageId }
                    if (stage != null) {
                        MissionIntroScreen(
                            stage = stage,
                            onBack = { navController.popBackStack() },
                            onStartMission = {
                                navController.navigate("mini_game/$stageId")
                            }
                        )
                    }
                }

                composable(
                    route = Routes.MINI_GAME,
                    arguments = listOf(navArgument("stageId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val stageId = backStackEntry.arguments?.getInt("stageId") ?: 1
                    var stageState by remember(stageId) { mutableStateOf<StageEntity?>(null) }
                    var questionState by remember(stageId) { mutableStateOf<List<QuestionEntity>>(emptyList()) }

                    LaunchedEffect(stageId) {
                        val loadedStage = viewModel.loadStage(stageId)
                        stageState = loadedStage
                        if (loadedStage != null) {
                            questionState = viewModel.loadQuestionsForStage(loadedStage.worldId, 3)
                        }
                    }

                    val st = stageState
                    if (st != null) {
                        MiniGamePlayScreen(
                            stage = st,
                            questions = questionState,
                            soundEnabled = uiState.preferences.soundEnabled,
                            onBack = { navController.popBackStack() },
                            onFinishStage = { correctCount, totalCount, mistakes, durationSec ->
                                viewModel.submitStageResult(
                                    stageId = st.id,
                                    correctCount = correctCount,
                                    totalCount = totalCount,
                                    mistakes = mistakes,
                                    durationSeconds = durationSec
                                ) {
                                    navController.navigate(Routes.MISSION_RESULT) {
                                        popUpTo("mission_intro/$stageId") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }

                composable(Routes.MISSION_RESULT) {
                    val summary = lastCompletion
                    if (summary != null) {
                        MissionResultScreen(
                            summary = summary,
                            onBackToMap = {
                                navController.navigate(Routes.MAP) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                }
                            },
                            onOpenFutureWorlds = { navController.navigate(Routes.FUTURE_WORLDS) },
                            onGoHome = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                composable(Routes.ACHIEVEMENTS) {
                    AchievementsScreen(
                        uiState = uiState,
                        onSelectBottomTab = ::navigateBottomTab
                    )
                }

                composable(Routes.INVENTORY) {
                    InventoryScreen(
                        uiState = uiState,
                        onPurchaseOrEquip = viewModel::purchaseOrEquipCosmetic,
                        onSelectBottomTab = ::navigateBottomTab
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        uiState = uiState,
                        isSignedIn = currentUser != null,
                        onEditCharacter = { navController.navigate(Routes.CHARACTER_EDIT) },
                        onOpenSyncStatus = { navController.navigate(Routes.SYNC_STATUS) },
                        onSelectBottomTab = ::navigateBottomTab
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() },
                        onToggleSound = viewModel::toggleSound,
                        onToggleNotifications = viewModel::toggleNotifications,
                        onEditIdentity = { navController.navigate(Routes.CHARACTER_EDIT) },
                        onOpenSyncStatus = { navController.navigate(Routes.SYNC_STATUS) },
                        onOpenAnnouncements = { navController.navigate(Routes.ANNOUNCEMENTS) },
                        onOpenFutureWorlds = { navController.navigate(Routes.FUTURE_WORLDS) }
                    )
                }

                composable(Routes.SYNC_STATUS) {
                    SyncStatusScreen(
                        uiState = uiState,
                        currentUser = currentUser,
                        syncInProgress = syncInProgress,
                        onBack = { navController.popBackStack() },
                        onSyncNow = viewModel::synchronizeNow,
                        onAuthFeedback = viewModel::showBanner
                    )
                }

                composable(Routes.ANNOUNCEMENTS) {
                    AnnouncementsScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.FUTURE_WORLDS) {
                    FutureWorldsScreen(
                        worlds = uiState.worlds,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.LEADERBOARD) {
                    LeaderboardScreen(
                        uiState = uiState,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
