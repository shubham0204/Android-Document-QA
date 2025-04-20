package com.ml.shubham0204.docqa.domain.embeddings

import android.content.Context
import com.ml.shubham0204.sentence_embeddings.SentenceEmbedding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import java.io.File

@Single
class SentenceEmbeddingProvider(
    private val context: Context,
) {
    private val sentenceEmbedding = SentenceEmbedding()

    init {
        val onnxLocalFile = copyToLocalStorage("all-MiniLM-L6-V2.onnx")
        val tokenizerLocalFile = copyToLocalStorage("tokenizer.json")
        val tokenizerBytes = tokenizerLocalFile.readBytes()
        runBlocking(Dispatchers.IO) {
            sentenceEmbedding.init(
                onnxLocalFile.absolutePath,
                tokenizerBytes,
                useTokenTypeIds = false,
                outputTensorName = "last_hidden_state",
                normalizeEmbeddings = false,
            )
        }
    }

    fun encodeText(text: String): FloatArray =
        runBlocking(Dispatchers.Default) {
            return@runBlocking sentenceEmbedding.encode(text)
        }

    private fun copyToLocalStorage(filename: String): File {
        val tokenizerBytes = context.assets.open(filename).readBytes()
        val storageFile = File(context.filesDir, filename)
        if (!storageFile.exists()) {
            storageFile.writeBytes(tokenizerBytes)
        }
        return storageFile
    }
}
