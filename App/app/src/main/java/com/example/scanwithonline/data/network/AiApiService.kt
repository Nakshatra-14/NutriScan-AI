package com.example.scanwithonline.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Data class for the REQUEST your app will send to the AI ---
data class AnalysisRequest(
    @SerializedName("product_name")
    val productName: String,
    val age: Int,
    val protein: Double,
    val carb: Double,
    val fat: Double,
    val sugar: Double,
    val fiber: Double,
    val language: String
)


// --- Data classes for the RESPONSE your app will receive from the AI ---


// This is the new top-level response class that matches your Python API
data class FullAnalysisResponse(
    val analysis: AnalysisText,
    val scores: ModelScores
)

// This class holds all the text-based insights
data class AnalysisText(
    val title: String,
    val summary: String,
    val scoreInsights: ScoreInsights,
    val proTip: String
)

// This class holds all the calculated numerical scores
data class ModelScores(
    val nutriScore: Int,
    val ecoScore: Int,
    val glycemicIndex: Int
)

data class ScoreInsights(
    val glycemicInsight: String,
    val nutriScoreInsight: String,
    val ecoScoreInsight: String
)

// --- AI API Service Interface ---
interface AiApiService {
    @POST("/analyze")
    // Updated to expect the new FullAnalysisResponse
    suspend fun getAnalysis(@Body request: AnalysisRequest): FullAnalysisResponse
}

// --- Singleton Retrofit Client for the AI Server ---
object AiRetrofitClient {
    // IMPORTANT: Make sure this is the IP address of the computer running your Python server
    private const val BASE_URL = "http://192.168.0.130:5000/"

    val instance: AiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
    }
}