package com.example.dietplanner.com.example.dietplanner.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanEntity
import com.example.dietplanner.ui.theme.DietPlannerTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
/**
 * HOME SCREEN - Main landing page of the app
 *
 * Features:
 * - Welcome card with user greeting
 * - List of saved diet plans
 * - Empty state with dummy sample plans
 * - Profile button in top bar
 * - FAB for reminders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    savedPlans: List<DietPlanEntity>,
    onProfileClick: () -> Unit,
    onPlanClick: (Long) -> Unit,
    onFabClick: () -> Unit,
    onDeletePlan: (DietPlanEntity) -> Unit
) {
    Log.d("HomeScreen", "=== Home Screen Loaded ===")
    Log.d("HomeScreen", "Displaying ${savedPlans.size} saved plans")

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "🥗",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            "Diet Planner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                },
                actions = {
                    // Profile Button
                    IconButton(
                        onClick = {
                            Log.d("HomeScreen", "Profile button clicked")
                            onProfileClick()
                        }
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Floating Action Button for Reminders
            ExtendedFloatingActionButton(
                onClick = {
                    Log.d("HomeScreen", "FAB clicked - Navigate to reminders")
                    onFabClick()
                },
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Reminders"
                    )
                },
                text = { Text("Reminders", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top spacing
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Welcome Card
            item {
                WelcomeCard(
                    plansCount = savedPlans.size,
                    onCreateNew = onProfileClick
                )
            }

            // Show saved plans or empty state
            if (savedPlans.isNotEmpty()) {
                // Saved Plans Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Your Diet Plans",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Badge(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text("${savedPlans.size}")
                        }
                    }
                }

                // Saved Plans List
                items(savedPlans, key = { it.id }) { plan ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                                Log.d("HomeScreen", "Deleting plan: ${plan.name}")
                                onDeletePlan(plan)
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart ->
                                    MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val alignment = when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                else -> Alignment.Center
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        },
                        content = {
                            DietPlanCard(
                                plan = plan,
                                dateFormatter = dateFormatter,
                                onClick = {
                                    Log.d("HomeScreen", "Plan clicked: ID=${plan.id}, Name=${plan.name}")
                                    onPlanClick(plan.id)
                                }
                            )
                        }
                    )
                }

            } else {
                // Empty State with Dummy Plans
                item {
                    EmptyStateWithDummyPlans(onCreateNew = onProfileClick)
                }
            }

            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * Welcome Card - Shows greeting and quick stats
 */
@Composable
private fun WelcomeCard(
    plansCount: Int,
    onCreateNew: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Welcome Back! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = when {
                        plansCount == 0 -> "Create your first personalized diet plan"
                        plansCount == 1 -> "You have 1 saved plan"
                        else -> "You have $plansCount saved plans"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}

/**
 * Diet Plan Card - Individual saved plan card
 */
@Composable
private fun DietPlanCard(
    plan: DietPlanEntity,
    dateFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    Card(
        onClick = {
            Log.d("HomeScreen", "Card clicked: ${plan.name}")
            onClick()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row (Title + Favorite)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        plan.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Show favorite icon if plan is marked as favorite
                if (plan.isFavorite) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Plan Type & Date Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plan Type Chip
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            plan.planType.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Place,
                            null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    border = null,
                    modifier = Modifier.height(28.dp)
                )

                Text(
                    "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                // Created Date
                Text(
                    dateFormatter.format(Date(plan.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // User Info (if available)
            if (plan.userHeight > 0 && plan.userWeight > 0) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        "👤 ${plan.userGender}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "📏 ${plan.userHeight.toInt()}cm",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "⚖️ ${plan.userWeight.toInt()}kg",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    if (plan.userAge > 0) {
                        Text(
                            "🎂 ${plan.userAge}yrs",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Empty State - Shows when no plans are saved
 * Includes dummy sample plans for visual appeal
 */
@Composable
private fun EmptyStateWithDummyPlans(onCreateNew: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated empty state icon
        var isVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isVisible = true
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(600)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(600)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Empty state icon with pulsing animation
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "No Diet Plans Yet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Create your first personalized diet plan\nto get started on your health journey",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        Log.d("HomeScreen", "Create your plan button clicked")
                        onCreateNew()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create Your Plan",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Dummy Sample Plans Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "✨ Sample Plans",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "Get inspired by these popular diet plans",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            DummyPlanCard(
                title = "Weight Loss Plan",
                description = "Low-calorie balanced meals for healthy weight loss",
                icon = "🏃",
                color = MaterialTheme.colorScheme.primaryContainer
            )

            DummyPlanCard(
                title = "Muscle Gain Plan",
                description = "High-protein diet for muscle building and strength",
                icon = "💪",
                color = MaterialTheme.colorScheme.secondaryContainer
            )

            DummyPlanCard(
                title = "Balanced Nutrition",
                description = "Healthy mix of all nutrients for overall wellness",
                icon = "⚖️",
                color = MaterialTheme.colorScheme.tertiaryContainer
            )

            DummyPlanCard(
                title = "Keto Diet Plan",
                description = "Low-carb, high-fat meals for ketosis",
                icon = "🥑",
                color = MaterialTheme.colorScheme.errorContainer
            )
        }
    }
}

/**
 * Dummy Plan Card - Non-clickable sample plan card
 */
@Composable
private fun DummyPlanCard(
    title: String,
    description: String,
    icon: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                icon,
                style = MaterialTheme.typography.displaySmall,
                fontSize = 40.sp
            )

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // "Sample" badge
            AssistChip(
                onClick = {},
                label = { Text("Sample", style = MaterialTheme.typography.labelSmall) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = null,
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun HomeScreenEmptyPreview() {
    DietPlannerTheme {
        HomeScreen(
            savedPlans = emptyList(),
            onProfileClick = {},
            onPlanClick = {},
            onFabClick = {},
            onDeletePlan = {}
        )
    }
}

@Preview(showBackground = true, name = "With Saved Plans")
@Composable
private fun HomeScreenWithPlansPreview() {
    DietPlannerTheme {
        HomeScreen(
            savedPlans = listOf(
                DietPlanEntity(
                    id = 1,
                    name = "My Weekly Plan",
                    content = "Sample content",
                    cleanedContent = "Sample content",
                    createdAt = System.currentTimeMillis(),
                    planType = "weekly",
                    userHeight = 175f,
                    userWeight = 75f,
                    userAge = 28,
                    userGender = "Male",
                    isFavorite = true
                ),
                DietPlanEntity(
                    id = 2,
                    name = "Muscle Gain Program",
                    content = "Sample content",
                    cleanedContent = "Sample content",
                    createdAt = System.currentTimeMillis() - 86400000,
                    planType = "muscle-gain",
                    userHeight = 180f,
                    userWeight = 85f,
                    userAge = 25,
                    userGender = "Male",
                    isFavorite = false
                )
            ),
            onProfileClick = {},
            onPlanClick = {},
            onFabClick = {},
            onDeletePlan = {}
        )
    }
}