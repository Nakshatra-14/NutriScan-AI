package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.scanwithonline.R
import com.example.scanwithonline.ui.theme.ScanWithOnlineTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2000L) // Wait for 2 seconds
        // Navigate to the status checker instead of directly to home
        navController.navigate("status_checker_screen") {
            // Pop the splash screen from the back stack
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    ScanWithOnlineTheme {
        SplashScreen(navController = rememberNavController())
    }
}
