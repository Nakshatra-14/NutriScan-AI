package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiInfoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Glycemic Index (GI)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GiInfoCard(
                    title = "The Estimation Formula",
                    description = "We use a simplified formula to estimate the Glycemic Index based on the nutritional data provided by the API:\n\nGI ≈ ( (Sugars + (Carbs - Sugars) * 0.5) / Carbs ) * 100"
                )
            }
            item {
                GiInfoCard(
                    title = "What the Variables Mean",
                    description = "• Carbs: The total carbohydrates in the food. This is the main nutrient that affects blood sugar.\n\n• Sugars: The total amount of simple sugars in the food. These are digested quickly and have a high impact on blood sugar.\n\n• (Carbs - Sugars) * 0.5: This part of the formula estimates the effect of complex carbohydrates, assuming they have about half the impact of simple sugars."
                )
            }
            item {
                GiInfoCard(
                    title = "What do the values mean?",
                    description = "A lower GI value (typically below 55) suggests that the food will cause a slower, smaller rise in blood sugar levels, which is generally better for managing diabetes. Higher values mean a quicker and higher spike in blood sugar."
                )
            }
            item {
                Text(
                    text = "Disclaimer: This is an estimation for informational purposes only and is not a substitute for clinical measurement or professional medical advice.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun GiInfoCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}
