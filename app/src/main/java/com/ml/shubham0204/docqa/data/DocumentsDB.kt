package com.ml.shubham0204.docqa.data

import io.objectbox.kotlin.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DocumentsDB {

    private val docsBox = ObjectBoxStore.store.boxFor(Document::class.java)

    fun addDocument(document: Document): Long {
        return docsBox.put(document)
    }

    fun removeDocument(docId: Long) {
        docsBox.remove(docId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllDocuments(): Flow<MutableList<Document>> =
        docsBox.query(Document_.docId.notNull()).build().flow().flowOn(Dispatchers.IO)

    fun getDocsCount(): Long {
        return docsBox.count()
    }
}
