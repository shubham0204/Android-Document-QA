package com.ml.shubham0204.docqa.domain

import android.util.Log
import com.ml.shubham0204.docqa.data.QueryResult
import com.ml.shubham0204.docqa.data.RetrievedContext
import com.ml.shubham0204.docqa.domain.llm.GeminiRemoteAPI
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class QAUseCase
@Inject
constructor(
    private val documentsUseCase: DocumentsUseCase,
    private val chunksUseCase: ChunksUseCase,
    private val geminiRemoteAPI: GeminiRemoteAPI
) {

    fun getAnswer(query: String, prompt: String, onResponse: ((QueryResult) -> Unit)) {
        var jointContext = ""
        val retrievedContextList = ArrayList<RetrievedContext>()
        chunksUseCase.getSimilarChunks(query, n = 5).forEach {
            jointContext += " " + it.second.chunkData
            retrievedContextList.add(RetrievedContext(it.second.docFileName, it.second.chunkData))
        }
        Log.e("APP", "Context: $jointContext")
        val inputPrompt = prompt.replace("\$CONTEXT", jointContext).replace("\$QUERY", query)
        CoroutineScope(Dispatchers.IO).launch {
            geminiRemoteAPI.getResponse(inputPrompt)?.let { llmResponse ->
                onResponse(QueryResult(llmResponse, retrievedContextList))
            }
        }
    }

    fun canGenerateAnswers(): Boolean {
        return documentsUseCase.getDocsCount() > 0
    }
}
