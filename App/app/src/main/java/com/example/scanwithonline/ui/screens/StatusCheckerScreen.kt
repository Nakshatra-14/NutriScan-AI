package com.example.scanwithonline.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.scanwithonline.viewmodels.AppStatus
import com.example.scanwithonline.viewmodels.MainViewModel

@Composable
fun StatusCheckerScreen(navController: NavController, mainViewModel: MainViewModel = viewModel()) {
    val appStatus by mainViewModel.appStatus.collectAsState()

    // This effect will run when appStatus changes
    LaunchedEffect(appStatus) {
        when (appStatus) {
            is AppStatus.Active -> {
                // --- THIS LINE IS THE FIX ---
                // Navigate to the data source selection screen instead of the home screen.
                navController.navigate("data_source_selection_screen") { //
                    popUpTo("splash_screen") { inclusive = true }
                }
            }
            is AppStatus.Blocked -> {
                // Navigate to blocked screen and clear the back stack
                navController.navigate("blocked_screen") { //
                    popUpTo("splash_screen") { inclusive = true }
                }
            }
            AppStatus.Loading -> {
                // Do nothing, just show the loading indicator
            }
        }
    }

    // Show a loading indicator while the check is in progress
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (appStatus == AppStatus.Loading) {
            CircularProgressIndicator() //
        }
    }
}