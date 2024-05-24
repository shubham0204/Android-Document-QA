package com.ml.shubham0204.docqa.domain

import android.util.Log
import com.ml.shubham0204.docqa.data.GeminiRemoteAPI
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class QAUseCase
@Inject
constructor(
    private val chunksUseCase: ChunksUseCase,
    private val geminiRemoteAPI: GeminiRemoteAPI
) {

    fun getAnswer(query: String, prompt: String, onResponse: ((String) -> Unit)) {
        var jointContext = ""
        chunksUseCase.getSimilarChunks(query, n = 3).forEach {
            jointContext += " " + it.second.chunkData
        }
        Log.e("APP", "Context: $jointContext")
        val inputPrompt = prompt.replace("\$CONTEXT", jointContext).replace("\$QUERY", query)
        CoroutineScope(Dispatchers.IO).launch {
            geminiRemoteAPI.getResponse(inputPrompt)?.let(onResponse)
        }
    }
}
