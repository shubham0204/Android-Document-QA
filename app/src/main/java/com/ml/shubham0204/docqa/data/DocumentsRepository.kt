package com.ml.shubham0204.docqa.data

import io.objectbox.reactive.DataObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class DocumentsRepository {

    private val docsBox = ObjectBoxDB.store.boxFor(Document::class.java)

    fun addDocument(document: Document) {
        docsBox.put(document)
    }

    fun removeDocument(docId: Long) {
        docsBox.remove(docId)
    }

    fun getAllDocuments(): Flow<List<Document>> =
        callbackFlow<List<Document>> {
                val docsObserver = DataObserver<List<Document>> { trySend(it) }
                val query = docsBox.query(Document_.docId.notNull()).build()
                query.subscribe().observer(docsObserver)
                awaitClose { query.close() }
            }
            .flowOn(Dispatchers.IO)
}
