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
import com.example.scanwithonline.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiabetesGuidanceScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Diabetes Guidance") },
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
                InfoCard(
                    title = "How is this calculated?",
                    description = "This guidance is based on the food's estimated Glycemic Index (GI). The GI is a rating system for foods containing carbohydrates. It shows how quickly each food affects your blood sugar (glucose) level when that food is eaten on its own."
                )
            }
            item {
                InfoCard(
                    title = "Understanding the Labels",
                    description = "👍 Good for Diabetics (GI ≤ 55):\nFoods with a low GI are digested and absorbed more slowly, causing a slower and smaller rise in blood sugar levels.\n\n🤔 Moderate Impact (GI 56-69):\nThese foods have a moderate effect on blood sugar levels. They should be eaten in moderation and paired with low-GI foods.\n\n⚠️ High Impact (GI ≥ 70):\nFoods with a high GI are rapidly digested and absorbed, resulting in a marked fluctuation in blood sugar levels. These should be consumed with caution."
                )
            }
            item {
                Text(
                    text = "Disclaimer: This guidance is based on an estimated GI and is for informational purposes only. Always consult with a healthcare professional for personalized medical advice.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
