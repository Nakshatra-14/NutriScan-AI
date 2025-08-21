package com.example.scanwithonline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.scanwithonline.navigation.AppNavigation
import com.example.scanwithonline.ui.theme.ScanWithOnlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanWithOnlineTheme {
                // This is the entry point for your app's UI.
                // It sets up the navigation graph we created earlier.
                AppNavigation()
            }
        }
    }
}
