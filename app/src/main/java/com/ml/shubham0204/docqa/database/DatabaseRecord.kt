package com.ml.shubham0204.docqa.database

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class DatabaseRecord(
    @Id var recordId: Long = 0,
    @Index var docId: String = "",
    var chunkId: String = "",
    var chunkData: String = "",
    @HnswIndex(dimensions = 100) var chunkEmbedding: FloatArray = floatArrayOf()
)
