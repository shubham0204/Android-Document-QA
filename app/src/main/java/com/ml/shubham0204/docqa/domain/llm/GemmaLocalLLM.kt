package com.ml.shubham0204.docqa.domain.llm

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GemmaLocalLLM(context: Context) {

    private val options =
        LlmInference.LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/llm/model.bin")
            .setMaxTokens(1000)
            .setTopK(40)
            .setTemperature(0.8f)
            .setRandomSeed(101)
            .build()
    private val llmInference = LlmInference.createFromOptions(context, options)

    suspend fun getResponse(prompt: String): String? =
        withContext(Dispatchers.IO) {
            Log.e("APP", "Prompt given: $prompt")
            val response = llmInference.generateResponse(prompt)
            return@withContext response
        }
}
