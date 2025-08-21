package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.scanwithonline.R
import com.example.scanwithonline.data.network.FullAnalysisResponse
import com.example.scanwithonline.data.network.Product
import com.example.scanwithonline.viewmodels.DetailsViewModel
import com.example.scanwithonline.viewmodels.ProductUiState

// --- Helper Functions to Convert Scores to Grades ---
private fun convertNutriToGrade(score: Int): String {
    return when {
        score <= -1 -> "a"
        score <= 2 -> "b"
        score <= 10 -> "c"
        score <= 18 -> "d"
        else -> "e"
    }
}

private fun convertEcoToGrade(score: Int): String {
    return when {
        score <= 20 -> "a"
        score <= 40 -> "b"
        score <= 60 -> "c"
        score <= 80 -> "d"
        else -> "e"
    }
}

@Composable
fun DetailsScreen(
    barcode: String,
    navController: NavController,
    detailsViewModel: DetailsViewModel = viewModel()
) {
    val uiState by detailsViewModel.uiState.collectAsState()
    val languageChosen by detailsViewModel.languageChosen.collectAsState()

    // Handle TTS lifecycle
    val ttsManager = detailsViewModel.ttsManager
    DisposableEffect(Unit) {
        onDispose {
            ttsManager?.shutdown()
        }
    }

    if (!languageChosen) {
        LanguageSelectionDialog(
            onDismiss = {
                navController.popBackStack()
            },
            onLanguageSelected = { languageCode ->
                detailsViewModel.fetchProductDetails(barcode, languageCode)
            }
        )
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is ProductUiState.Loading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }
                is ProductUiState.Success -> {
                    // Autoplay is removed. User initiates TTS with the button.
                    ProductDetailsLayout(
                        product = state.product,
                        fullAnalysis = state.fullAnalysis,
                        navController = navController,
                        onReplayTts = { detailsViewModel.replayTts() }
                    )
                }
                is ProductUiState.Error -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailsLayout(
    product: Product,
    fullAnalysis: FullAnalysisResponse,
    navController: NavController,
    onReplayTts: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("AI Analysis", "Nutrition Details")

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = product.selected_images?.front?.display?.en,
                contentDescription = product.product_name,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.product_name ?: "Unknown Product",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Brand: ${product.brands ?: "N/A"} | Quantity: ${product.quantity ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("score_info_screen") },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreColumn(
                    score = convertNutriToGrade(fullAnalysis.scores.nutriScore),
                    scoreType = "nutri",
                    label = "Nutri-Score (AI)"
                )
                Spacer(modifier = Modifier.width(32.dp))
                ScoreColumn(
                    score = convertEcoToGrade(fullAnalysis.scores.ecoScore),
                    scoreType = "eco",
                    label = "Eco-Score (AI)"
                )
            }
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> OverviewTab(fullAnalysis, navController, onReplayTts)
            1 -> NutritionTab(product)
        }
    }
}

@Composable
fun OverviewTab(
    fullAnalysis: FullAnalysisResponse,
    navController: NavController,
    onReplayTts: () -> Unit
) {
    val analysisText = fullAnalysis.analysis
    val modelScores = fullAnalysis.scores

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            DiabetesGuidanceChip(gi = modelScores.glycemicIndex, navController = navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                // ▼▼▼ THIS IS THE FIX ▼▼▼
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = analysisText.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 40.dp) // Add padding to avoid overlap
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysisText.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Score Insights",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = analysisText.scoreInsights.glycemicInsight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = analysisText.scoreInsights.nutriScoreInsight)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = analysisText.scoreInsights.ecoScoreInsight)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pro-Tip",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = analysisText.proTip)
                    }

                    // Position the button in the top right corner
                    IconButton(
                        onClick = onReplayTts,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Play Analysis")
                    }
                }
                // ▲▲▲ END OF FIX ▲▲▲
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            NutrientRow(
                name = "Glycemic Index (Model Est.)",
                value = "${modelScores.glycemicIndex}",
                modifier = Modifier.clickable { navController.navigate("gi_info_screen") }
            )
        }
    }
}

@Composable
fun NutritionTab(product: Product) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Nutritional Information (per 100g)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            NutrientRow("Energy", "${product.nutriments?.energyKcal100g ?: "N/A"} kcal")
            NutrientRow("Protein", "${product.nutriments?.proteins_100g ?: "N/A"} g")
            NutrientRow("Carbohydrates", "${product.nutriments?.carbohydrates_100g ?: "N/A"} g")
            NutrientRow("Sugars", "${product.nutriments?.sugars_100g ?: "N/A"} g", product.nutrient_levels?.sugars)
            NutrientRow("Fat", "${product.nutriments?.fat_100g ?: "N/A"} g", product.nutrient_levels?.fat)
            NutrientRow("Saturated Fat", "${product.nutriments?.saturatedFat100g ?: "N/A"} g", product.nutrient_levels?.saturatedFat)
            NutrientRow("Salt", "${product.nutriments?.salt_100g ?: "N/A"} g", product.nutrient_levels?.salt)
        }
        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            val allergens = product.allergens_from_ingredients
            val allergensToShow = if (allergens.isNullOrEmpty()) {
                "None"
            } else {
                allergens.split(",")
                    .map { it.removePrefix("en:").trim().replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
                    .first()
            }
            NutrientRow("Allergens", allergensToShow)
            NutrientRow("NOVA Group", getNovaGroupDescription(product.nova_group))
        }
    }
}

@Composable
fun DiabetesGuidanceChip(gi: Int, navController: NavController) {
    val (text, color, emoji) = when {
        gi <= 55 -> Triple("Good for Diabetics", Color(0xFF4CAF50), "👍") // Green
        gi <= 69 -> Triple("Moderate Impact", Color(0xFFFFC107), "🤔") // Amber
        else -> Triple("High Impact", Color(0xFFF44336), "⚠️") // Red
    }

    Card(
        modifier = Modifier.clickable { navController.navigate("diabetes_guidance_screen") },
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

fun getNovaGroupDescription(novaGroup: Int?): String {
    return when (novaGroup) {
        1 -> "1 - Unprocessed or minimally processed foods"
        2 -> "2 - Processed culinary ingredients"
        3 -> "3 - Processed foods"
        4 -> "4 - Ultra-processed food and drink products"
        else -> "N/A"
    }
}

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("English" to "en", "हिन्दी" to "hi", "বাংলা" to "bn")
    var selectedLanguage by remember { mutableStateOf(languages[0].second) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                languages.forEach { (name, code) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (code == selectedLanguage),
                                onClick = { selectedLanguage = code }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (code == selectedLanguage),
                            onClick = { selectedLanguage = code }
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onLanguageSelected(selectedLanguage) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ScoreColumn(score: String?, scoreType: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        val imageRes = when (score?.lowercase()) {
            "a" -> if (scoreType == "nutri") R.drawable.nutriscore_a else R.drawable.ecoscore_a
            "b" -> if (scoreType == "nutri") R.drawable.nutriscore_b else R.drawable.ecoscore_b
            "c" -> if (scoreType == "nutri") R.drawable.nutriscore_c else R.drawable.ecoscore_c
            "d" -> if (scoreType == "nutri") R.drawable.nutriscore_d else R.drawable.ecoscore_d
            "e" -> if (scoreType == "nutri") R.drawable.nutriscore_e else R.drawable.ecoscore_e
            else -> null
        }

        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "$label: $score",
                modifier = Modifier.height(40.dp)
            )
        } else {
            Text(text = "N/A")
        }
    }
}

@Composable
fun NutrientRow(name: String, value: String, level: String? = null, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (level != null) {
                val color = when (level.lowercase()) {
                    "high" -> Color.Red
                    "moderate" -> Color(0xFFFFC107) // Amber
                    "low" -> Color(0xFF4CAF50) // Green
                    else -> Color.Gray
                }
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(color, shape = MaterialTheme.shapes.small))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = value)
        }
    }
}