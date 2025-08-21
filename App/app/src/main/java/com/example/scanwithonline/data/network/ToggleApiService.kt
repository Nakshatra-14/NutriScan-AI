package com.example.scanwithonline.data.network

import retrofit2.http.GET

/**
 * A Retrofit service interface for the toggle API.
 * This defines the HTTP requests that can be made to the Firebase URL.
 */
interface ToggleApiService {
    /**
     * Fetches the boolean value from the 'switchValue.json' endpoint.
     * The 'suspend' keyword means this function can be paused and resumed,
     * making it suitable for use in coroutines without blocking the main thread.
     */
    @GET("switchValue.json")
    suspend fun getSwitchValue(): Boolean
}
