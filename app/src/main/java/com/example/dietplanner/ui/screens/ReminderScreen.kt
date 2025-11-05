package com.example.dietplanner.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dietplanner.ui.theme.DietPlannerTheme

data class Reminder(
    val id: Int,
    val title: String,
    val time: String,
    val emoji: String,
    val type: String,
    val enabled: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(onBack: () -> Unit) {
    Log.d("ReminderScreen", "Screen loaded")

    val reminders = remember {
        listOf(
            Reminder(1, "Morning Water", "7:00 AM", "💧", "Hydration", true),
            Reminder(2, "Breakfast Time", "8:00 AM", "🍳", "Meal", true),
            Reminder(3, "Mid-Morning Snack", "10:30 AM", "🍎", "Snack", false),
            Reminder(4, "Lunch Time", "1:00 PM", "🍱", "Meal", true),
            Reminder(5, "Afternoon Water", "3:00 PM", "💧", "Hydration", true),
            Reminder(6, "Evening Exercise", "6:00 PM", "🏃", "Exercise", true),
            Reminder(7, "Dinner Time", "8:00 PM", "🍽️", "Meal", true),
            Reminder(8, "Night Water", "10:00 PM", "💧", "Hydration", true)
        )
    }

    var reminderStates by remember {
        mutableStateOf(reminders.associate { it.id to it.enabled })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daily Reminders",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("ReminderScreen", "Back clicked")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(40.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "⏰ Coming Soon",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Daily reminders for meals, hydration, and exercise will be available in the next update!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Preview: Upcoming Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(reminders) { reminder ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                reminder.emoji,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Column {
                                Text(
                                    reminder.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            reminder.type,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Text(
                                        reminder.time,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Switch(
                            checked = reminderStates[reminder.id] ?: false,
                            onCheckedChange = { checked ->
                                Log.d("ReminderScreen", "Reminder ${reminder.id} toggled: $checked")
                                reminderStates = reminderStates.toMutableMap().apply {
                                    put(reminder.id, checked)
                                }
                            },
                            enabled = false // Disabled as placeholder
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderScreenPreview() {
    DietPlannerTheme {
        ReminderScreen(onBack = {})
    }
}