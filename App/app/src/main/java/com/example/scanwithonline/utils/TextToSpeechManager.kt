package com.example.scanwithonline.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(
    context: Context,
    // The onInitializationComplete callback is no longer needed here
) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(context, this)
    private var isInitialized = false
    private var languageCode: String = "en" // Default to English

    // NEW: This will "remember" the text if speak() is called before the engine is ready
    private var pendingText: String? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            val locale = Locale(languageCode)
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

            // NEW: If there's a pending text, speak it now that we are ready
            pendingText?.let {
                speak(it)
                pendingText = null
            }
        } else {
            isInitialized = false
            Log.e("TTS", "Initialization Failed!")
        }
    }

    fun setLanguage(langCode: String) {
        this.languageCode = langCode
        if (isInitialized) {
            val locale = Locale(languageCode)
            tts.setLanguage(locale)
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            // If we're ready, speak immediately.
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // If not ready, save the text to be spoken later.
            pendingText = text
        }
    }

    fun shutdown() {
        if (isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}