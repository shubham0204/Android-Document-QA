package com.ml.shubham0204.docqa.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class Chunk(
    @Id var recordId: Long = 0,
    @Index var docId: Long = 0,
    var chunkId: String = "",
    var chunkData: String = "",
    @HnswIndex(dimensions = 100) var chunkEmbedding: FloatArray = floatArrayOf()
)

@Entity
data class Document(
    @Id var docId: Long = 0,
    var docText: String = "",
    var docFileName: String = "",
    var docAddedTime: Long = 0,
)
