package com.ml.shubham0204.docqa.data

class ChunksDB {

    private val chunksBox = ObjectBoxStore.store.boxFor(Chunk::class.java)

    fun addChunk(chunk: Chunk) {
        chunksBox.put(chunk)
    }

    fun getSimilarChunks(queryEmbedding: FloatArray, n: Int = 5): List<Pair<Float, Chunk>> {
        return chunksBox
            .query(Chunk_.chunkEmbedding.nearestNeighbors(queryEmbedding, n))
            .build()
            .findWithScores()
            .map { Pair(it.score.toFloat(), it.get()) }
    }

    fun removeChunks(docId: Long) {
        chunksBox.removeByIds(chunksBox.query(Chunk_.docId.equal(docId)).build().findIds().toList())
    }
}
