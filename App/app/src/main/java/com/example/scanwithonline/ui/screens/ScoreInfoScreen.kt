package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.scanwithonline.R
import com.example.scanwithonline.ui.components.InfoCard // Import the InfoCard here

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreInfoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Food Scores") },
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
                    title = "What is Nutri-Score?",
                    description = "A Nutri-Score calculation pinpoints the nutritional value of a product and assigns it one of five colour-coded letter grades (A, B, C, D, or E). Products with an A score have the highest nutritional value, while those with an E have the lowest."
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(painterResource(R.drawable.nutriscore_a), "Nutri-Score A", modifier = Modifier.height(30.dp))
                        Image(painterResource(R.drawable.nutriscore_b), "Nutri-Score B", modifier = Modifier.height(30.dp))
                        Image(painterResource(R.drawable.nutriscore_c), "Nutri-Score C", modifier = Modifier.height(30.dp))
                        Image(painterResource(R.drawable.nutriscore_d), "Nutri-Score D", modifier = Modifier.height(30.dp))
                        Image(painterResource(R.drawable.nutriscore_e), "Nutri-Score E", modifier = Modifier.height(30.dp))
                    }
                }
            }
            item {
                InfoCard(
                    title = "What is Eco-Score?",
                    description = "Eco-Score classes food products from A (low) to E (high) according to their impact on the environment. It aims to guide consumers towards more responsible consumption."
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(painterResource(R.drawable.ecoscore_a), "Eco-Score A", modifier = Modifier.height(40.dp))
                        Image(painterResource(R.drawable.ecoscore_b), "Eco-Score B", modifier = Modifier.height(40.dp))
                        Image(painterResource(R.drawable.ecoscore_c), "Eco-Score C", modifier = Modifier.height(40.dp))
                        Image(painterResource(R.drawable.ecoscore_d), "Eco-Score D", modifier = Modifier.height(40.dp))
                        Image(painterResource(R.drawable.ecoscore_e), "Eco-Score E", modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
