package com.example.dietplanner.com.example.dietplanner.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietplanner.com.example.dietplanner.data.local.database.DayPlanEntity
import com.example.dietplanner.com.example.dietplanner.util.EdgeToEdgeScaffold
import com.example.dietplanner.ui.theme.DietPlannerTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DietInDaysScreen(
    dietPlan: com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanEntity?,
    dayPlans: List<DayPlanEntity>,
    onBack: () -> Unit
) {
    var selectedDayIndex by remember { mutableStateOf(0) }

    EdgeToEdgeScaffold(statusBarColor = MaterialTheme.colorScheme.primaryContainer) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            dietPlan?.name ?: "Diet Plan",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(padding)
            ) {
                if (dayPlans.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Day Plans Available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Your structured plan will appear here once generated.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        // 🗓️ Day selector tabs
                        ScrollableTabRow(
                            selectedTabIndex = selectedDayIndex,
                            edgePadding = 16.dp,
                            containerColor = Color.Transparent
                        ) {
                            dayPlans.forEachIndexed { index, dayPlan ->
                                Tab(
                                    selected = selectedDayIndex == index,
                                    onClick = { selectedDayIndex = index },
                                    text = {
                                        Text(
                                            dayPlan.dayName,
                                            fontWeight = if (selectedDayIndex == index)
                                                FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }

                        // 📋 Day details
                        AnimatedContent(
                            targetState = selectedDayIndex,
                            transitionSpec = {
                                slideInVertically(
                                    animationSpec = tween(300),
                                    initialOffsetY = { it / 2 }
                                ) + fadeIn(animationSpec = tween(300)) with
                                        fadeOut(animationSpec = tween(200))
                            },
                            label = "dayContent"
                        ) { targetIndex ->
                            DayPlanContent(dayPlan = dayPlans[targetIndex])
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayPlanContent(dayPlan: DayPlanEntity) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (dayPlan.breakfast.isNotBlank()) {
            item {
                AnimatedMealCard(
                    title = "Breakfast",
                    content = dayPlan.breakfast,
                    icon = Icons.Rounded.WbSunny,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }

        if (dayPlan.lunch.isNotBlank()) {
            item {
                AnimatedMealCard(
                    title = "Lunch",
                    content = dayPlan.lunch,
                    icon = Icons.Rounded.LunchDining,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }

        if (dayPlan.dinner.isNotBlank()) {
            item {
                AnimatedMealCard(
                    title = "Dinner",
                    content = dayPlan.dinner,
                    icon = Icons.Rounded.Nightlight,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        }

        if (dayPlan.snacks.isNotBlank()) {
            item {
                AnimatedMealCard(
                    title = "Snacks",
                    content = dayPlan.snacks,
                    icon = Icons.Rounded.Fastfood,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        if (dayPlan.exercise.isNotBlank()) {
            item {
                AnimatedMealCard(
                    title = "Exercise",
                    content = dayPlan.exercise,
                    icon = Icons.Rounded.FitnessCenter,
                    color = MaterialTheme.colorScheme.errorContainer
                )
            }
        }

        if (dayPlan.hydration.isNotBlank()) {
            item {
                AnimatedMealCard(
                    title = "Hydration",
                    content = dayPlan.hydration,
                    icon = Icons.Rounded.WaterDrop,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

/**
 * Animated card for meals with fade + slide-in animation
 */
@Composable
fun AnimatedMealCard(
    title: String,
    content: String,
    icon: ImageVector,
    color: Color
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(600)
        )
    ) {
        MealCard(title, content, icon, color)
    }
}

@Composable
fun MealCard(
    title: String,
    content: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DietInDaysScreenPreview() {
    DietPlannerTheme {
        DietInDaysScreen(
            dietPlan = null,
            dayPlans = emptyList(),
            onBack = {}
        )
    }
}
