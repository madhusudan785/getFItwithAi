package com.example.dietplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlannerDatabase
import com.example.dietplanner.com.example.dietplanner.data.repository.DietPlanLocalRepository
import com.example.dietplanner.com.example.dietplanner.ui.screens.DietInDaysScreen
import com.example.dietplanner.com.example.dietplanner.ui.screens.HomeScreen
import com.example.dietplanner.data.local.UserPreferencesManager
import com.example.dietplanner.data.model.DietPlanState
import com.example.dietplanner.data.network.SSEManager
import com.example.dietplanner.data.repository.DietRepository
import com.example.dietplanner.ui.navigation.Screen
import com.example.dietplanner.ui.screens.*
import com.example.dietplanner.ui.theme.DietPlannerTheme
import com.example.dietplanner.ui.viewmodel.DietViewModel
import com.example.dietplanner.ui.viewmodel.DietViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "=== App Started ===")

        val baseUrl = "http://192.168.1.3:8080"
        Log.d(TAG, "Backend URL: $baseUrl")

        // Initialize dependencies
        val sseManager = SSEManager(baseUrl)
        val repository = DietRepository(sseManager, Gson())
        val preferencesManager = UserPreferencesManager(applicationContext)
        val database = DietPlannerDatabase.getDatabase(applicationContext)
        val localRepository = DietPlanLocalRepository(database.dietPlanDao())

        setContent {
            DietPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DietPlannerApp(repository, localRepository, preferencesManager)
                }
            }
        }
    }
}

@Composable
fun DietPlannerApp(
    repository: DietRepository,
    localRepository: DietPlanLocalRepository,
    preferencesManager: UserPreferencesManager
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val viewModel: DietViewModel = viewModel(
        factory = DietViewModelFactory(repository, localRepository, preferencesManager)
    )

    val dietPlanState by viewModel.dietPlanState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val allDietPlans by viewModel.allDietPlans.collectAsState()

    val selectedDietPlan by viewModel.selectedDietPlan.collectAsState()
    val selectedDayPlans by viewModel.selectedDayPlans.collectAsState()

    val isFirstLaunchState = preferencesManager.isFirstLaunch.collectAsState(initial = null)

    isFirstLaunchState.value?.let { firstLaunchDone ->
        val startDestination = if (!firstLaunchDone) Screen.Welcome.route else Screen.Home.route

        // ✅ Handle DietPlanState for UI transitions & errors
        LaunchedEffect(dietPlanState) {
            when (dietPlanState) {
                is DietPlanState.Loading -> {
                    Log.d("Navigation", "Generating diet plan (loading...)")
                }

                is DietPlanState.Streaming -> {
                    if (navController.currentBackStackEntry?.destination?.route != Screen.DietPlan.route) {
                        Log.d("Navigation", "Navigating to DietPlan during streaming")
                        navController.navigate(Screen.DietPlan.route)
                    }
                }

                is DietPlanState.Success -> {
                    Log.d("Navigation", "Diet plan successfully generated.")
                    if (navController.currentBackStackEntry?.destination?.route != Screen.DietPlan.route) {
                        navController.navigate(Screen.DietPlan.route)
                    }
                }

                is DietPlanState.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar((dietPlanState as DietPlanState.Error).message)
                    }
                }

                else -> Unit
            }
        }

        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    }
                ) {
                    // =======================
                    // 🟩 WELCOME SCREEN
                    // =======================
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onGetStarted = {
                                scope.launch {
                                    preferencesManager.setFirstLaunchDone()
                                }
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // =======================
                    // 🏠 HOME SCREEN
                    // =======================
                    composable(Screen.Home.route) {
                        Log.d("Navigation", "Navigated to: Home")
                        HomeScreen(
                            savedPlans = allDietPlans,
                            onProfileClick = { navController.navigate(Screen.Input.route) },
                            onPlanClick = { planId ->
                                Log.d("Navigation", "Plan clicked: $planId")
                                viewModel.loadDietPlan(planId)
                                navController.navigate(Screen.DietInDays.createRoute(planId))
                            },
                            onFabClick = { navController.navigate(Screen.Reminder.route) },
                            onDeletePlan = { plan -> viewModel.deleteDietPlan(plan) }
                        )
                    }

                    // =======================
                    // 🧍 INPUT SCREEN
                    // =======================
                    composable(Screen.Input.route) {
                        Log.d("Navigation", "Navigated to: Input")
                        InputScreen(
                            existingProfile = userProfile,
                            onSubmit = { profile ->
                                Log.d("InputScreen", "Submitting profile: $profile")
                                viewModel.saveUserProfile(profile)
                                viewModel.generateDietPlan(profile)
                                navController.navigate(Screen.DietPlan.route)
                            },
                            onBack = {
                                viewModel.resetState()
                                navController.popBackStack()
                            }
                        )
                    }

                    // =======================
                    // 🥗 DIET PLAN SCREEN
                    // =======================
                    composable(Screen.DietPlan.route) {
                        Log.d("Navigation", "Navigated to: DietPlan")
                        val coroutineScope = rememberCoroutineScope()

                        DietPlanScreen(
                            state = dietPlanState,
                            onAccept = {
                                Log.d("Navigation", "Diet plan accepted - saving structured plan")
                                coroutineScope.launch {
                                    val planId = viewModel.acceptGeneratedPlan()
                                    Log.d("Navigation", "Navigating to Feedback with planId=$planId")
                                    navController.navigate(Screen.Feedback.createRoute(planId ?: -1L)) {
                                        popUpTo(Screen.DietPlan.route) { inclusive = true }
                                    }
                                }
                            },
                            onModify = { navController.popBackStack() },
                            onBack = {
                                viewModel.resetState()
                                navController.popBackStack()
                            }
                        )
                    }

                    // =======================
                    // 💬 FEEDBACK SCREEN
                    // =======================
                    composable(
                        route = Screen.Feedback.route,
                        arguments = listOf(navArgument("planId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val planId = backStackEntry.arguments?.getLong("planId") ?: -1L

                        FeedbackScreen(
                            planId = planId,
                            navController = navController,
                            onProceedToMonthly = { /* TODO */ },
                            onModifyPreferences = { navController.popBackStack() },
                            onRegenerate = { navController.navigate(Screen.DietPlan.route) },
                            onBack = { Screen.Home.route }
                        )
                    }

                    // =======================
                    // 📅 DIET-IN-DAYS SCREEN
                    // =======================
                    composable(
                        route = Screen.DietInDays.route,
                        arguments = listOf(navArgument("planId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val planId = backStackEntry.arguments?.getLong("planId") ?: -1L
                        Log.d("Navigation", "Navigated to: DietInDays for plan $planId")

                        // 🔹 Trigger data load when screen opens
                        LaunchedEffect(planId) {
                            if (planId != -1L) {
                                Log.d("Navigation", "Loading plan data for planId=$planId")
                                viewModel.loadDietPlan(planId)
                            }
                        }

                        // 🔹 Observe data reactively
                        val dietPlan by viewModel.selectedDietPlan.collectAsState()
                        val dayPlans by viewModel.selectedDayPlans.collectAsState()

                        DietInDaysScreen(
                            dietPlan = dietPlan,
                            dayPlans = dayPlans,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // =======================
                    // ⏰ REMINDER SCREEN
                    // =======================
                    composable(Screen.Reminder.route) {
                        Log.d("Navigation", "Navigated to: Reminder")
                        ReminderScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}