package com.example.scanwithonline.data.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * A singleton object to provide a configured instance of Retrofit for the toggle API.
 * Using an object ensures that we only have one instance of Retrofit for this base URL,
 * which is efficient.
 */
object ToggleRetrofitInstance {

    // The base URL for your Firebase Realtime Database.
    private const val BASE_URL = "https://toggle-api-default-rtdb.firebaseio.com/"

    /**
     * Lazily creates a Retrofit instance.
     * The 'lazy' delegate means the Retrofit object is only created the first time it's accessed.
     * We use ScalarsConverterFactory because the JSON response is a simple boolean literal (true/false),
     * not a full JSON object, and this converter can handle plain text and primitive types.
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    /**
     * Lazily creates an implementation of the ToggleApiService interface.
     * This is the object we will use to make the actual network request.
     */
    val api: ToggleApiService by lazy {
        retrofit.create(ToggleApiService::class.java)
    }
}
