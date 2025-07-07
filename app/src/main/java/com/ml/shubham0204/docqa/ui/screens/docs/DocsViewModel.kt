package com.ml.shubham0204.docqa.ui.screens.docs

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.shubham0204.docqa.data.Chunk
import com.ml.shubham0204.docqa.data.ChunksDB
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.data.DocumentsDB
import com.ml.shubham0204.docqa.domain.SentenceEmbeddingProvider
import com.ml.shubham0204.docqa.domain.WhiteSpaceSplitter
import com.ml.shubham0204.docqa.domain.readers.Readers
import hideProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import setProgressDialogText
import showProgressDialog
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.min

sealed interface DocsScreenUIEvent {
    data class OnDocSelected(
        val fileUri: Uri,
        val docType: Readers.DocumentType,
    ) : DocsScreenUIEvent

    data class OnDocURLSubmitted(
        val context: Context,
        val url: String,
        val docType: Readers.DocumentType,
    ) : DocsScreenUIEvent

    data class OnRemoveDoc(
        val docId: Long,
    ) : DocsScreenUIEvent
}

enum class DocDownloadState {
    DOWNLOAD_NONE,
    DOWNLOAD_IN_PROGRESS,
    DOWNLOAD_SUCCESS,
    DOWNLOAD_FAILURE,
}

data class DocsScreenUIState(
    val documents: List<Document> = emptyList(),
    val docDownloadState: DocDownloadState = DocDownloadState.DOWNLOAD_NONE,
)

@KoinViewModel
class DocsViewModel(
    private val contentResolver: ContentResolver,
    private val documentsDB: DocumentsDB,
    private val chunksDB: ChunksDB,
    private val sentenceEncoder: SentenceEmbeddingProvider,
) : ViewModel() {
    private val _docsScreenUIState = MutableStateFlow(DocsScreenUIState())
    val docsScreenUIState: StateFlow<DocsScreenUIState> = _docsScreenUIState

    init {
        viewModelScope.launch {
            documentsDB.getAllDocuments().collect {
                _docsScreenUIState.value = _docsScreenUIState.value.copy(documents = it)
            }
        }
    }

    fun onEvent(event: DocsScreenUIEvent) {
        when (event) {
            is DocsScreenUIEvent.OnDocSelected -> {
                var docFileName = ""
                // Retrieve file information from URI
                // See
                // https://developer.android.com/training/secure-file-sharing/retrieve-info#RetrieveFileInfo
                contentResolver.query(event.fileUri, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    docFileName = cursor.getString(nameIndex)
                }
                contentResolver.openInputStream(event.fileUri)?.let { inputStream ->
                    showProgressDialog()
                    viewModelScope.launch(Dispatchers.IO) {
                        addChunksFromInputStream(
                            docFileName,
                            event.docType,
                            inputStream,
                        )
                    }
                }
            }

            is DocsScreenUIEvent.OnDocURLSubmitted -> {
                showProgressDialog()
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val connection = URL(event.url).openConnection() as HttpURLConnection
                        connection.connect()
                        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                            withContext(Dispatchers.Main) {
                                _docsScreenUIState.value =
                                    _docsScreenUIState.value.copy(
                                        docDownloadState = DocDownloadState.DOWNLOAD_SUCCESS,
                                    )
                            }

                            val inputStream = connection.inputStream
                            val fileName = getFileNameFromURL(event.url)
                            val file = File(event.context.cacheDir, fileName)

                            file.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }

                            // Pass file to your document handling logic
                            val docFileName = getFileNameFromURL(event.url)
                            addChunksFromInputStream(
                                docFileName,
                                event.docType,
                                inputStream,
                            )
                        } else {
                            withContext(Dispatchers.Main) {
                                _docsScreenUIState.value =
                                    _docsScreenUIState.value.copy(
                                        docDownloadState = DocDownloadState.DOWNLOAD_FAILURE,
                                    )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            _docsScreenUIState.value =
                                _docsScreenUIState.value.copy(
                                    docDownloadState = DocDownloadState.DOWNLOAD_FAILURE,
                                )
                        }
                    }
                }
            }

            is DocsScreenUIEvent.OnRemoveDoc -> {
                documentsDB.removeDocument(event.docId)
                chunksDB.removeChunks(event.docId)
            }
        }
    }

    private suspend fun addChunksFromInputStream(
        docFileName: String,
        docType: Readers.DocumentType,
        inputStream: InputStream,
    ) {
        val text =
            Readers
                .getReaderForDocType(docType)
                .readFromInputStream(inputStream)
                ?: return
        val newDocId =
            documentsDB.addDocument(
                Document(
                    docText = text,
                    docFileName = docFileName,
                    docAddedTime = System.currentTimeMillis(),
                ),
            )
        setProgressDialogText("Creating chunks...")
        val chunks =
            WhiteSpaceSplitter.createChunks(
                text,
                chunkSize = 500,
                chunkOverlap = 50,
            )
        setProgressDialogText("Adding chunks to database...")
        val size = chunks.size
        chunks.forEachIndexed { index, s ->
            setProgressDialogText("Added ${index + 1}/$size chunk(s) to database...")
            val embedding = sentenceEncoder.encodeText(s)
            chunksDB.addChunk(
                Chunk(
                    docId = newDocId,
                    docFileName = docFileName,
                    chunkData = s,
                    chunkEmbedding = embedding,
                ),
            )
        }
        withContext(Dispatchers.IO) {
            hideProgressDialog()
            inputStream.close()
        }
    }

    // Extracts the file name from the URL
    // Source: https://stackoverflow.com/a/11576046/13546426
    private fun getFileNameFromURL(url: String?): String {
        if (url == null) {
            return ""
        }
        try {
            val resource = URL(url)
            val host = resource.host
            if (host.isNotEmpty() && url.endsWith(host)) {
                return ""
            }
        } catch (e: MalformedURLException) {
            return ""
        }
        val startIndex = url.lastIndexOf('/') + 1
        val length = url.length
        var lastQMPos = url.lastIndexOf('?')
        if (lastQMPos == -1) {
            lastQMPos = length
        }
        var lastHashPos = url.lastIndexOf('#')
        if (lastHashPos == -1) {
            lastHashPos = length
        }
        val endIndex = min(lastQMPos.toDouble(), lastHashPos.toDouble()).toInt()
        return url.substring(startIndex, endIndex)
    }
}
