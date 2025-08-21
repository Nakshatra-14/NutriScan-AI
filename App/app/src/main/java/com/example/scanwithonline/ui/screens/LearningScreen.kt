package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.scanwithonline.ui.theme.ScanWithOnlineTheme

// Data class to represent a learning topic
data class LearningTopic(
    val title: String,
    val description: String
)

// The list of learning topics
val learningTopics = listOf(
    LearningTopic("Understanding Macronutrients", "Learn about proteins, carbs, and fats."),
    LearningTopic("The Importance of Hydration", "Discover why water is crucial for your health."),
    LearningTopic("Micronutrients: Vitamins & Minerals", "A guide to the essential vitamins and minerals."),
    LearningTopic("Healthy Snacking Ideas", "Find nutritious and delicious snack options."),
    LearningTopic("The Role of Fiber in Diet", "Understand the benefits of a high-fiber diet."),
    LearningTopic("Benefits of a Balanced Diet", "Why variety and balance are key to good health."),
    LearningTopic("Mindful Eating Techniques", "Connect with your food and improve digestion."),
    LearningTopic("Understanding Food Allergies", "Common allergens and how to spot them."),
    LearningTopic("Meal Prepping for a Healthy Week", "Tips and tricks for planning your meals in advance."),
    LearningTopic("The Dangers of Processed Foods", "Learn what to avoid for a healthier lifestyle."),
    LearningTopic("Sugar: The Bitter Truth", "How sugar affects your body and how to reduce it.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Center") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(learningTopics) { topic ->
                LearningTopicCard(topic = topic)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningTopicCard(topic: LearningTopic) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { /* TODO: Navigate to a detail screen for the topic */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Learning Topic Icon",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go to topic",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LearningScreenPreview() {
    ScanWithOnlineTheme {
        LearningScreen(navController = rememberNavController())
    }
}
