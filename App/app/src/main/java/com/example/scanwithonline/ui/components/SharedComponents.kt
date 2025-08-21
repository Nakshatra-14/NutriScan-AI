package com.example.scanwithonline.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scanwithonline.R

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

// Add this function here
@Composable
fun InfoCard(
    title: String,
    description: String,
    content: @Composable (() -> Unit)? = null
) {
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
            if (content != null) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}
