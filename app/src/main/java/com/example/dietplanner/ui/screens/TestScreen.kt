package com.example.dietplanner.com.example.dietplanner.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dietplanner.data.model.DietPlanState
import com.example.dietplanner.ui.screens.DietPlanScreen
import com.example.dietplanner.ui.theme.DietPlannerTheme


@Composable
fun TestScreen() {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎵 Test Notification Sound",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                try {
                    val soundUri =
                        Uri.parse("android.resource://${context.packageName}/raw/diet_reminder")
                    Log.d("SoundTest", "Sound URI = $soundUri")

                    val player = MediaPlayer.create(context, soundUri)
                    player?.apply {
                        setOnCompletionListener {
                            it.release()
                            isPlaying = false
                        }
                        start()
                        isPlaying = true
                    }
                } catch (e: Exception) {
                    Log.e("SoundTest", "❌ Error playing sound", e)
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isPlaying) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                )
                Text("Playing...")
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Play Sound")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun T() {
    DietPlannerTheme {
        TestScreen(

        )
    }
}
