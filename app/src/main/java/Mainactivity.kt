package com.example.dietplanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    // Handle errors
    LaunchedEffect(dietPlanState) {
        if (dietPlanState is DietPlanState.Error) {
            val errorMessage = (dietPlanState as DietPlanState.Error).message
            Log.e("Navigation", "Showing error: $errorMessage")
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
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
                composable(Screen.Home.route) {
                    Log.d("Navigation", "Navigated to: Home")
                    HomeScreen(
                        savedPlans = allDietPlans,
                        onProfileClick = {
                            Log.d("Navigation", "Profile clicked")
                            navController.navigate(Screen.Input.route)
                        },
                        onPlanClick = { planId ->
                            Log.d("Navigation", "Plan clicked: $planId")
                            viewModel.loadDietPlan(planId)
                            navController.navigate(Screen.DietInDays.createRoute(planId))
                        },
                        onFabClick = {
                            Log.d("Navigation", "FAB clicked")
                            navController.navigate(Screen.Reminder.route)
                        },
                        onDeletePlan = { plan -> viewModel.deleteDietPlan(plan) }
                    )
                }

                composable(Screen.Input.route) {
                    Log.d("Navigation", "Navigated to: Input")
                    InputScreen(
                        existingProfile = userProfile,
                        onSubmit = { profile ->
                            Log.d("Navigation", "Form submitted")
                            viewModel.saveUserProfile(profile)
                            viewModel.generateDietPlan(profile)
                            navController.navigate(Screen.DietPlan.route)
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.DietPlan.route) {
                    Log.d("Navigation", "Navigated to: DietPlan")
                    DietPlanScreen(
                        state = dietPlanState,
                        onAccept = {
                            Log.d("Navigation", "Diet plan accepted - saving")
                            viewModel.saveDietPlan()
                            navController.navigate(Screen.Feedback.route)
                        },
                        onModify = {
                            navController.popBackStack()
                        },
                        onBack = {
                            viewModel.resetState()
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.DietInDays.route,
                    arguments = listOf(navArgument("planId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
                    Log.d("Navigation", "Navigated to: DietInDays for plan $planId")

                    DietInDaysScreen(
                        dietPlan = selectedDietPlan,
                        dayPlans = selectedDayPlans,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.Feedback.route) {
                    Log.d("Navigation", "Navigated to: Feedback")
                    FeedbackScreen(
                        onProceedToMonthly = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Monthly plan feature coming soon!")
                            }
                        },
                        onModifyPreferences = {
                            viewModel.resetState()
                            navController.navigate(Screen.Input.route) {
                                popUpTo(Screen.Home.route)
                            }
                        },
                        onRegenerate = {
                            userProfile?.let { profile ->
                                viewModel.generateDietPlan(profile)
                                navController.popBackStack()
                            }
                        },
                        onBack = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Reminder.route) {
                    Log.d("Navigation", "Navigated to: Reminder")
                    ReminderScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}