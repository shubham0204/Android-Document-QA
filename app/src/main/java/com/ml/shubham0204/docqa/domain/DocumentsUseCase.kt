package com.ml.shubham0204.docqa.domain

import android.util.Log
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.data.DocumentsDB
import com.ml.shubham0204.docqa.domain.readers.Readers
import com.ml.shubham0204.docqa.domain.splitters.WhiteSpaceSplitter
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import setProgressDialogText

@Singleton
class DocumentsUseCase
@Inject
constructor(private val chunksUseCase: ChunksUseCase, private val documentsDB: DocumentsDB) {

    suspend fun addDocument(
        inputStream: InputStream,
        fileName: String,
        documentType: Readers.DocumentType
    ) =
        withContext(Dispatchers.IO) {
            val text =
                Readers.getReaderForDocType(documentType).readFromInputStream(inputStream)
                    ?: return@withContext
            Log.e("APP", "PDF Text: $text")
            val newDocId =
                documentsDB.addDocument(
                    Document(
                        docText = text,
                        docFileName = fileName,
                        docAddedTime = System.currentTimeMillis()
                    )
                )
            setProgressDialogText("Creating chunks...")
            val chunks = WhiteSpaceSplitter.createChunks(text, chunkSize = 200, chunkOverlap = 50)
            setProgressDialogText("Adding chunks to database...")
            chunks.forEach {
                Log.e("APP", "Chunk added: $it")
                chunksUseCase.addChunk(newDocId, it)
            }
        }

    fun getAllDocuments(): Flow<List<Document>> {
        return documentsDB.getAllDocuments()
    }

    fun removeDocument(docId: Long) {
        documentsDB.removeDocument(docId)
        chunksUseCase.removeChunks(docId)
    }

    fun getDocsCount(): Long {
        return documentsDB.getDocsCount()
    }

}
