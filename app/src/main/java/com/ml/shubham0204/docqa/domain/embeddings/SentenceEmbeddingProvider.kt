package com.ml.shubham0204.docqa.domain.embeddings

import android.content.Context
import com.ml.shubham0204.sentence_embeddings.SentenceEmbedding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SentenceEmbeddingProvider(context: Context) {

    private val sentenceEmbedding = SentenceEmbedding(context)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            sentenceEmbedding.init()
        }
    }

    fun encodeText(text: String): FloatArray = runBlocking(Dispatchers.IO) {
        return@runBlocking sentenceEmbedding.encode(text)
    }
}
