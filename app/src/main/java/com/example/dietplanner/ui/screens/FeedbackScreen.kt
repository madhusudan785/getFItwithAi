package com.example.dietplanner.ui.screens

import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dietplanner.ui.navigation.Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    planId: Long,
    navController: NavController,
    onProceedToMonthly: () -> Unit,
    onModifyPreferences: () -> Unit,
    onRegenerate: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scaleAnim = remember { Animatable(0f) }
    val infiniteGlow = rememberInfiniteTransition(label = "glow")

    val glow by infiniteGlow.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glowAnim"
    )

    LaunchedEffect(Unit) {
        val OvershootEasing = Easing { OvershootInterpolator().getInterpolation(it) }

        scaleAnim.animateTo(1f, animationSpec = tween(700, easing = OvershootEasing))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Success!", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB9FBC0),
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFB9FBC0),
                            Color(0xFF96E6A1),
                            Color(0xFF6EE7B7)
                        )
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scaleAnim.value * glow)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Great Choice!",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Your weekly diet plan has been saved successfully.",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (planId != -1L) {
                            navController.navigate(Screen.DietInDays.createRoute(planId))
                        } else {
                            Toast.makeText(context, "No plan found!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA6))
                ) {
                    Icon(Icons.Rounded.MenuBook, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View My Weekly Plan", fontSize = 17.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Regenerate Plan", fontSize = 17.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onModifyPreferences,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Modify Preferences", fontSize = 17.sp)
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun FeedbackScreenPreview() {
//    DietPlannerTheme {
//        FeedbackScreen(
//            onProceedToMonthly = {},
//            onModifyPreferences = {},
//            onRegenerate = {},
//            onBack = {},
//
//        )
//    }
//}