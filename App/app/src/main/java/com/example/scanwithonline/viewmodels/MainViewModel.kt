package com.example.scanwithonline.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanwithonline.data.network.ToggleRetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Defines the possible states for the application's lock status.
 * Using a sealed interface is a clean way to represent a finite set of states.
 */
sealed interface AppStatus {
    object Loading : AppStatus // The app is currently checking the status.
    object Active : AppStatus  // The app is unlocked and usable.
    object Blocked : AppStatus // The app is locked.
}

/**
 * ViewModel responsible for fetching the app's lock status and providing it to the UI.
 */
class MainViewModel : ViewModel() {

    // Private mutable state flow that can be updated within the ViewModel.
    private val _appStatus = MutableStateFlow<AppStatus>(AppStatus.Loading)
    // Public immutable state flow that the UI can observe for changes.
    val appStatus: StateFlow<AppStatus> = _appStatus

    /**
     * The init block is called when the ViewModel is first created.
     * It immediately triggers the process to fetch the app's status.
     */
    init {
        fetchAppStatus()
    }

    /**
     * Fetches the switch value from the remote server using Retrofit.
     * It runs in a coroutine to avoid blocking the main thread.
     */
    private fun fetchAppStatus() {
        viewModelScope.launch {
            _appStatus.value = AppStatus.Loading
            try {
                // Make the network call to get the boolean value.
                val isActive = ToggleRetrofitInstance.api.getSwitchValue()
                // Update the state based on the server's response.
                _appStatus.value = if (isActive) {
                    AppStatus.Active
                } else {
                    AppStatus.Blocked
                }
            } catch (e: Exception) {
                // If there's any network error or other exception,
                // default to the "Blocked" state for security.
                _appStatus.value = AppStatus.Blocked
            }
        }
    }
}
