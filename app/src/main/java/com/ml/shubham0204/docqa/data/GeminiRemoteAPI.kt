package com.ml.shubham0204.docqa.data

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.ml.shubham0204.docqa.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRemoteAPI {

    private val apiKey = BuildConfig.geminiKey
    private val generativeModel = GenerativeModel(modelName = "gemini-pro", apiKey = apiKey)

    suspend fun getResponse(prompt: String): String? =
        withContext(Dispatchers.IO) {
            Log.e("APP", "Prompt given: $prompt")
            val response = generativeModel.generateContent(prompt)
            return@withContext response.text
        }
}
