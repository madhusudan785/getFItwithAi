package com.example.dietplanner.com.example.dietplanner.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dietplanner.com.example.dietplanner.data.local.database.DayPlanEntity
import com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanEntity
import com.example.dietplanner.com.example.dietplanner.util.EdgeToEdgeScaffold
import com.example.dietplanner.com.example.dietplanner.util.SetStatusBarColor
import com.example.dietplanner.ui.theme.DietPlannerTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun DietInDaysScreen(
    dietPlan: DietPlanEntity?,
    dayPlans: List<DayPlanEntity>,
    onBack: () -> Unit
) {

    Log.d("DietInDaysScreen", "Loaded ${dayPlans.size} day plans")

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
                        IconButton(onClick = {
                            Log.d("DietInDaysScreen", "Back clicked")
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            if (dayPlans.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            "No Day Plans Available",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "This plan doesn't have day-wise breakdown",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Day selector tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedDayIndex,
                        modifier = Modifier.fillMaxWidth(),
                        edgePadding = 16.dp
                    ) {
                        dayPlans.forEachIndexed { index, dayPlan ->
                            Tab(
                                selected = selectedDayIndex == index,
                                onClick = {
                                    Log.d("DietInDaysScreen", "Selected day: ${dayPlan.dayName}")
                                    selectedDayIndex = index
                                },
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

                    // Selected day content
                    AnimatedContent(
                        targetState = selectedDayIndex,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with
                                    fadeOut(animationSpec = tween(300))
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

@Composable
fun DayPlanContent(dayPlan: DayPlanEntity) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (dayPlan.breakfast.isNotBlank()) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    MealCard(
                        title = "Breakfast",
                        content = dayPlan.breakfast,
                        icon = Icons.Default.Face,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }

        if (dayPlan.lunch.isNotBlank()) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    MealCard(
                        title = "Lunch",
                        content = dayPlan.lunch,
                        icon = Icons.Default.AddCircle,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }

        if (dayPlan.dinner.isNotBlank()) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    MealCard(
                        title = "Dinner",
                        content = dayPlan.dinner,
                        icon = Icons.Default.AddCircle,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }
        }

        if (dayPlan.snacks.isNotBlank()) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    MealCard(
                        title = "Snacks",
                        content = dayPlan.snacks,
                        icon = Icons.Default.ShoppingCart,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        if (dayPlan.exercise.isNotBlank()) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    MealCard(
                        title = "Exercise",
                        content = dayPlan.exercise,
                        icon = Icons.Default.Settings,
                        color = MaterialTheme.colorScheme.errorContainer
                    )
                }
            }
        }

        if (dayPlan.hydration.isNotBlank()) {
            item {
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                    MealCard(
                        title = "Hydration",
                        content = dayPlan.hydration,
                        icon = Icons.Default.FavoriteBorder,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }


}

@Composable

    fun MealCard(
        title: String,
        content: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        color: androidx.compose.ui.graphics.Color
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
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
