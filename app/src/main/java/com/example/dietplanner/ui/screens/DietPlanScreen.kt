package com.example.dietplanner.ui.screens
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dietplanner.com.example.dietplanner.util.EdgeToEdgeScaffold
import com.example.dietplanner.com.example.dietplanner.util.SetStatusBarColor
import com.example.dietplanner.data.model.DietPlanState
import com.example.dietplanner.data.model.ParsedDietPlan
import com.example.dietplanner.ui.theme.DietPlannerTheme
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlanScreen(
    state: DietPlanState,
    onAccept: () -> Unit,
    onModify: () -> Unit,
    onBack: () -> Unit
) {

    val scrollState = rememberScrollState()

    LaunchedEffect(state) {
        Log.d("DietPlanScreen", "State changed: ${state::class.simpleName}")
        if (state is DietPlanState.Streaming || state is DietPlanState.Success) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }


    EdgeToEdgeScaffold(statusBarColor = MaterialTheme.colorScheme.primaryContainer) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Your Diet Plan", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            Log.d("DietPlanScreen", "Back clicked")
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        when (state) {
                            is DietPlanState.Idle -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "⏳",
                                        style = MaterialTheme.typography.displayLarge
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Waiting to generate diet plan...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            is DietPlanState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(60.dp),
                                        strokeWidth = 6.dp
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        "Connecting to AI...",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Creating your personalized diet plan",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            is DietPlanState.Streaming -> {
                                Column(
                                    modifier = Modifier.verticalScroll(scrollState)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "Generating...",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                                    Text(
                                        text = state.content,
                                        style = MaterialTheme.typography.bodyLarge,
                                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                                    )
                                    TypingIndicator()
                                }
                            }

                            is DietPlanState.Success -> {
                                Column(
                                    modifier = Modifier.verticalScroll(scrollState)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            "Plan Ready!",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                                    SelectionContainer {
                                        // 💡 NEW LOGIC
                                        val parsedPlan = remember(state.content) {
                                            try {
                                                Gson().fromJson(state.content, ParsedDietPlan::class.java)
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }

                                        if (parsedPlan != null) {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(
                                                    text = "🍽️ Weekly Diet Plan (${parsedPlan.planType.uppercase()})",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "🔥 ${parsedPlan.caloriesTarget} kcal/day target",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = parsedPlan.notes,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        } else {
                                            // Display coach message text
                                            Text(
                                                text = state.content,
                                                style = MaterialTheme.typography.bodyLarge,
                                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                                            )
                                        }
                                    }
                                }
                            }


                            is DietPlanState.Error -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Connection Error",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        state.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    OutlinedButton(
                                        onClick = onModify,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Try Again")
                                    }
                                }
                            }
                        }
                    }
                }

                if (state is DietPlanState.Success) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                Log.d("DietPlanScreen", "Modify clicked")
                                onModify()
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modify", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                Log.d("DietPlanScreen", "Accept clicked")
                                onAccept()
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Accept", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .graphicsLayer {
                        this.alpha = alpha
                        this.scaleX = alpha
                        this.scaleY = alpha
                    }
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxSize()
                ) {}
            }
        }
        Text(
            "AI is typing",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp).graphicsLayer { this.alpha = alpha }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DietPlanScreenPreview() {
    DietPlannerTheme {
        DietPlanScreen(
            state = DietPlanState.Streaming("## Weekly Diet Plan\n\nMonday:\nBreakfast: Oatmeal..."),
            onAccept = {}, onModify = {}, onBack = {}
        )
    }
}