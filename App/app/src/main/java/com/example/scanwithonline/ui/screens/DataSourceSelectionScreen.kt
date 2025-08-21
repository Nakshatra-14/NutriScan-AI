package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.scanwithonline.data.DataSourceManager
import com.example.scanwithonline.data.DataSourceType
import com.example.scanwithonline.ui.theme.ScanWithOnlineTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSourceSelectionScreen(navController: NavController) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Data Source",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose whether to fetch product data from the live internet database or your local offline source.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Online Mode Button
            SourceButton(
                text = "Online Mode",
                description = "Use the live Open Food Facts database.",
                onClick = {
                    // --- UPDATE THIS ---
                    DataSourceManager.currentSource = DataSourceType.ONLINE
                    navController.navigate("home_screen") {
                        popUpTo("status_checker_screen") { inclusive = true }
                    }
                },
                backgroundColor = Color(0xFF52CCA7)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Offline Mode Button
            SourceButton(
                text = "Offline Mode",
                description = "Use your custom-added product data.",
                onClick = {
                    // --- UPDATE THIS ---
                    DataSourceManager.currentSource = DataSourceType.OFFLINE
                    navController.navigate("home_screen") {
                        popUpTo("status_checker_screen") { inclusive = true }
                    }
                },
                backgroundColor = Color(0xFF659EEB)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceButton(
    text: String,
    description: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DataSourceSelectionScreenPreview() {
    ScanWithOnlineTheme {
        DataSourceSelectionScreen(navController = rememberNavController())
    }
}