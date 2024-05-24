package com.ml.shubham0204.docqa.domain.embeddings

import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder

class UniversalSentenceEncoder(context: Context) {

    private val baseOptions =
        BaseOptions.builder().setModelAssetPath("universal_sentence_encoder.tflite").build()
    private val textEmbedderOptions =
        TextEmbedder.TextEmbedderOptions.builder().setBaseOptions(baseOptions).build()
    private val textEmbedder = TextEmbedder.createFromOptions(context, textEmbedderOptions)

    fun encodeText(text: String): FloatArray {
        return textEmbedder.embed(text).embeddingResult().embeddings().first().floatEmbedding()
    }
}
