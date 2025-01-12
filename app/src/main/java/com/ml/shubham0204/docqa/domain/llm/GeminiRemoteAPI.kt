package com.ml.shubham0204.docqa.domain.llm

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRemoteAPI(
    private val apiKey: String,
) {
    private val generativeModel: GenerativeModel

    init {
        // Here's a good reference on topK, topP and temperature
        // parameters, which are used to control the output of a LLM
        // See
        // https://ivibudh.medium.com/a-guide-to-controlling-llm-model-output-exploring-top-k-top-p-and-temperature-parameters-ed6a31313910
        val configBuilder = GenerationConfig.Builder()
        configBuilder.topP = 0.4f
        configBuilder.temperature = 0.3f
        generativeModel =
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                generationConfig = configBuilder.build(),
                systemInstruction =
                    content {
                        text(
                            "You are an intelligent search engine. You will be provided with some retrieved context, as well as the users query. Your job is to understand the request, and answer based on the retrieved context.",
                        )
                    },
            )
    }

    suspend fun getResponse(prompt: String): String? =
        withContext(Dispatchers.IO) {
            Log.e("APP", "Prompt given: $prompt")
            val response = generativeModel.generateContent(prompt)
            return@withContext response.text
        }
}
