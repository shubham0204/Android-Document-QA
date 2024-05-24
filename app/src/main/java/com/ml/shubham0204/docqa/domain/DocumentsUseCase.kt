package com.ml.shubham0204.docqa.domain

import android.util.Log
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.data.DocumentsDB
import com.ml.shubham0204.docqa.domain.readers.PDFReader
import com.ml.shubham0204.docqa.domain.splitters.WhiteSpaceSplitter
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
class DocumentsUseCase
@Inject
constructor(private val chunksUseCase: ChunksUseCase, private val documentsDB: DocumentsDB) {

    suspend fun addDocument(inputStream: InputStream, fileName: String) =
        withContext(Dispatchers.IO) {
            val pdfReader = PDFReader()
            val pdfText = pdfReader.readFromInputStream(inputStream)
            Log.e("APP", "PDF Text: $pdfText")
            val newDocId =
                documentsDB.addDocument(
                    Document(
                        docText = pdfText,
                        docFileName = fileName,
                        docAddedTime = System.currentTimeMillis()
                    )
                )
            WhiteSpaceSplitter.createChunks(pdfText, chunkSize = 70, chunkOverlap = 30).forEach {
                Log.e("APP", "Chunk added: $it")
                chunksUseCase.addChunk(newDocId, it)
            }
        }

    fun getAllDocuments(): Flow<List<Document>> {
        return documentsDB.getAllDocuments()
    }

    fun removeDocument(docId: Long) {
        documentsDB.removeDocument(docId)
    }
}
