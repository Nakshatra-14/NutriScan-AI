package com.example.scanwithonline.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanwithonline.data.ProductRepository
import com.example.scanwithonline.data.network.AiRetrofitClient
import com.example.scanwithonline.data.network.AnalysisRequest
import com.example.scanwithonline.data.network.FullAnalysisResponse
import com.example.scanwithonline.data.network.Product
import com.example.scanwithonline.utils.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// The UI state remains the same
sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(
        val product: Product,
        val fullAnalysis: FullAnalysisResponse,
        val ttsNarrative: String
    ) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

// ViewModel now needs Application context for TTS
class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState

    private val _languageChosen = MutableStateFlow(false)
    val languageChosen: StateFlow<Boolean> = _languageChosen

    private var currentBarcode: String? = null

    var ttsManager: TextToSpeechManager? = null
        private set

    fun fetchProductDetails(barcode: String, language: String) {
        if (barcode == currentBarcode && _uiState.value is ProductUiState.Success) {
            return
        }
        currentBarcode = barcode
        _languageChosen.value = true

        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                // Initialize TTS manager. It will warm up in the background.
                initializeTtsManager(language)

                val productData = ProductRepository.getProductDetails(barcode).product

                if (productData != null) {
                    val analysisRequest = AnalysisRequest(
                        productName = productData.product_name ?: "Unknown Product",
                        age = 25,
                        protein = productData.nutriments?.proteins_100g ?: 0.0,
                        carb = productData.nutriments?.carbohydrates_100g ?: 0.0,
                        fat = productData.nutriments?.fat_100g ?: 0.0,
                        sugar = productData.nutriments?.sugars_100g ?: 0.0,
                        fiber = productData.nutriments?.fiber_100g ?: 0.0,
                        language = language
                    )

                    val fullAnalysisResponse = AiRetrofitClient.instance.getAnalysis(analysisRequest)

                    val ttsNarrative = listOfNotNull(
                        fullAnalysisResponse.analysis.summary,
                        fullAnalysisResponse.analysis.scoreInsights.glycemicInsight,
                        fullAnalysisResponse.analysis.scoreInsights.nutriScoreInsight,
                        fullAnalysisResponse.analysis.scoreInsights.ecoScoreInsight,
                        fullAnalysisResponse.analysis.proTip
                    ).joinToString(" ")

                    _uiState.value = ProductUiState.Success(productData, fullAnalysisResponse, ttsNarrative)
                } else {
                    _uiState.value = ProductUiState.Error("Product not found.")
                }
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("An error occurred: ${e.message}")
            }
        }
    }

    // Simplified function to initialize the TTS manager
    private fun initializeTtsManager(language: String) {
        if (ttsManager == null) {
            ttsManager = TextToSpeechManager(getApplication()).apply {
                setLanguage(language)
            }
        } else {
            ttsManager?.setLanguage(language)
        }
    }

    // Function to be called from the UI to replay TTS
    fun replayTts() {
        val currentState = _uiState.value
        if (currentState is ProductUiState.Success) {
            ttsManager?.speak(currentState.ttsNarrative)
        }
    }

    // Clean up TTS when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        ttsManager?.shutdown()
    }
}