package com.ml.shubham0204.docqa.domain

import android.util.Log
import com.ml.shubham0204.docqa.data.Chunk
import com.ml.shubham0204.docqa.data.ChunksDB
import com.ml.shubham0204.docqa.domain.embeddings.UniversalSentenceEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChunksUseCase
@Inject
constructor(private val chunksDB: ChunksDB, private val sentenceEncoder: UniversalSentenceEncoder) {

    fun addChunk(docId: Long, docFileName: String, chunkText: String) {
        val embedding = sentenceEncoder.encodeText(chunkText)
        Log.e("APP", "Embedding dims ${embedding.size}")
        chunksDB.addChunk(
            Chunk(
                docId = docId,
                docFileName = docFileName,
                chunkData = chunkText,
                chunkEmbedding = embedding
            )
        )
    }

    fun removeChunks(docId: Long) {
        chunksDB.removeChunks(docId)
    }

    fun getSimilarChunks(query: String, n: Int = 5): List<Pair<Float, Chunk>> {
        val queryEmbedding = sentenceEncoder.encodeText(query)
        return chunksDB.getSimilarChunks(queryEmbedding, n)
    }
}
