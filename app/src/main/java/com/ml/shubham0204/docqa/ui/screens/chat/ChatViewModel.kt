package com.ml.shubham0204.docqa.ui.screens.chat

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.docqa.data.ChunksDB
import com.ml.shubham0204.docqa.data.DocumentsDB
import com.ml.shubham0204.docqa.data.RetrievedContext
import com.ml.shubham0204.docqa.domain.embeddings.SentenceEmbeddingProvider
import com.ml.shubham0204.docqa.domain.llm.GeminiRemoteAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ChatViewModel(
    private val documentsDB: DocumentsDB,
    private val chunksDB: ChunksDB,
    private val geminiRemoteAPI: GeminiRemoteAPI,
    private val sentenceEncoder: SentenceEmbeddingProvider,
) : ViewModel() {
    private val _questionState = MutableStateFlow("")
    val questionState: StateFlow<String> = _questionState

    private val _responseState = MutableStateFlow("")
    val responseState: StateFlow<String> = _responseState

    private val _isGeneratingResponseState = MutableStateFlow(false)
    val isGeneratingResponseState: StateFlow<Boolean> = _isGeneratingResponseState

    private val _retrievedContextListState = MutableStateFlow(emptyList<RetrievedContext>())
    val retrievedContextListState: StateFlow<List<RetrievedContext>> = _retrievedContextListState

    fun getAnswer(
        query: String,
        prompt: String,
    ) {
        _isGeneratingResponseState.value = true
        _questionState.value = query
        var jointContext = ""
        val retrievedContextList = ArrayList<RetrievedContext>()
        val queryEmbedding = sentenceEncoder.encodeText(query)
        chunksDB.getSimilarChunks(queryEmbedding, n = 5).forEach {
            jointContext += " " + it.second.chunkData
            retrievedContextList.add(RetrievedContext(it.second.docFileName, it.second.chunkData))
        }
        val inputPrompt = prompt.replace("\$CONTEXT", jointContext).replace("\$QUERY", query)
        CoroutineScope(Dispatchers.IO).launch {
            geminiRemoteAPI.getResponse(inputPrompt)?.let { llmResponse ->
                _responseState.value = llmResponse
                _isGeneratingResponseState.value = false
                _retrievedContextListState.value = retrievedContextList
            }
        }
    }

    fun canGenerateAnswers(): Boolean = documentsDB.getDocsCount() > 0
}
