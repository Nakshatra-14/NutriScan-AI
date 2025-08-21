package com.example.scanwithonline.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.scanwithonline.ui.screens.*
import com.example.scanwithonline.viewmodels.MainViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // The ViewModel is hoisted here to be shared by the checker and potentially other screens
    val mainViewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        // New route for checking the app status
        composable("status_checker_screen") {
            StatusCheckerScreen(navController = navController, mainViewModel = mainViewModel)
        }
        // To route for the data source selection
        composable("data_source_selection_screen") {
            DataSourceSelectionScreen(navController = navController)
        }
        // New route for the blocked screen
        composable("blocked_screen") {
            BlockedScreen()
        }
        composable("home_screen") {
            HomeScreen(navController = navController)
        }
        composable("profile_screen") {
            ProfileScreen(navController = navController)
        }
        composable("scanner_screen") {
            ScannerScreen(navController = navController)
        }
        composable("about_us_screen") {
            AboutUsScreen(navController = navController)
        }
        composable("learning_screen") {
            LearningScreen(navController = navController)
        }
        composable("manual_input_screen") {
            ManualInputScreen(navController = navController)
        }
        composable(
            route = "details_screen/{barcode}",
            arguments = listOf(navArgument("barcode") { type = NavType.StringType })
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode")
            if (barcode != null) {
                DetailsScreen(barcode = barcode, navController = navController)
            }
        }
        composable("score_info_screen") {
            ScoreInfoScreen(navController = navController)
        }
        composable("gi_info_screen") {
            GiInfoScreen(navController = navController)
        }
        composable("diabetes_guidance_screen") {
            DiabetesGuidanceScreen(navController = navController)
        }
    }
}