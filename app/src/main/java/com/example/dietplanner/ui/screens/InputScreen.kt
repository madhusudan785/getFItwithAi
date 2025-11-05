package com.example.dietplanner.ui.screens
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dietplanner.data.model.UserProfile
import com.example.dietplanner.ui.theme.DietPlannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    existingProfile: UserProfile? = null,
    onSubmit: (UserProfile) -> Unit,
    onBack: @Composable () -> Unit
) {
    Log.d("InputScreen", "Screen loaded with profile: ${existingProfile != null}")

    var height by remember { mutableStateOf(existingProfile?.height?.toString() ?: "") }
    var weight by remember { mutableStateOf(existingProfile?.weight?.toString() ?: "") }
    var age by remember { mutableStateOf(existingProfile?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(existingProfile?.gender ?: "Male") }
    var likes by remember { mutableStateOf(existingProfile?.likes?.joinToString(", ") ?: "") }
    var dislikes by remember { mutableStateOf(existingProfile?.dislikes?.joinToString(", ") ?: "") }
    var restrictions by remember { mutableStateOf(existingProfile?.dietaryRestrictions ?: "") }
    var activityLevel by remember { mutableStateOf(existingProfile?.activityLevel ?: "Moderate") }

    var genderExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female", "Other")
    val activityOptions = listOf("Sedentary", "Light", "Moderate", "Active", "Very Active")

    val isValid = height.isNotBlank() && weight.isNotBlank() && age.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("InputScreen", "Back button clicked")
                        //onBack() -> this implementation done when home is added to this project in next stage

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Physical Details Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "📏 Physical Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (cm)") },
                        leadingIcon = { Icon(Icons.Default.Check, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        leadingIcon = { Icon(Icons.Default.ThumbUp, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // Personal Details Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "👤 Personal Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                        Log.d("InputScreen", "Gender selected: $option")
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = activityExpanded,
                        onExpandedChange = { activityExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = activityLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Activity Level") },
                            leadingIcon = { Icon(Icons.Default.ArrowForward, null) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = activityExpanded,
                            onDismissRequest = { activityExpanded = false }
                        ) {
                            activityOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        activityLevel = option
                                        activityExpanded = false
                                        Log.d("InputScreen", "Activity level selected: $option")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Food Preferences Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "🍽️ Food Preferences",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = likes,
                        onValueChange = { likes = it },
                        label = { Text("Foods You Like") },
                        placeholder = { Text("e.g., chicken, rice, apples") },
                        leadingIcon = { Text("❤️", style = MaterialTheme.typography.titleLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dislikes,
                        onValueChange = { dislikes = it },
                        label = { Text("Foods to Avoid") },
                        placeholder = { Text("e.g., broccoli, fish") },
                        leadingIcon = { Text("🚫", style = MaterialTheme.typography.titleLarge) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = restrictions,
                        onValueChange = { restrictions = it },
                        label = { Text("Dietary Restrictions (Optional)") },
                        placeholder = { Text("e.g., vegan, gluten-free") },
                        leadingIcon = { Icon(Icons.Default.CheckCircle, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    Log.d("InputScreen", "Submit clicked")
                    Log.d("InputScreen", "Height: $height, Weight: $weight, Age: $age")
                    val profile = UserProfile(
                        height = height.toFloatOrNull() ?: 0f,
                        weight = weight.toFloatOrNull() ?: 0f,
                        age = age.toIntOrNull() ?: 0,
                        gender = gender,
                        likes = likes.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        dislikes = dislikes.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        dietaryRestrictions = restrictions,
                        activityLevel = activityLevel
                    )
                    Log.d("InputScreen", "Profile created: $profile")
                    onSubmit(profile)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Generate My Diet Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!isValid) {
                Text(
                    "⚠️ Please fill in height, weight, and age",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputScreenPreview() {
    DietPlannerTheme {
        InputScreen(onSubmit = {}, onBack = {})
    }
}