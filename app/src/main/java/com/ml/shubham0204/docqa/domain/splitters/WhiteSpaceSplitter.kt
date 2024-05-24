package com.ml.shubham0204.docqa.domain.splitters

import android.util.Log
import kotlin.math.max
import kotlin.math.min

class WhiteSpaceSplitter {

    companion object {

        fun createChunks(
            docText: String,
            chunkSize: Int,
            chunkOverlap: Int,
            separatorParagraph: String = "\n\n",
            separator: String = " "
        ): List<String> {
            val textChunks = ArrayList<String>()
            docText.split(separatorParagraph).forEach { paragraph ->
                var currChunk = ""
                val chunks = ArrayList<String>()
                paragraph.split(separator).forEach { word ->
                    val newChunk =
                        currChunk +
                            (if (currChunk.isNotEmpty()) {
                                separator
                            } else {
                                ""
                            }) +
                            word
                    if (newChunk.length <= chunkSize) {
                        currChunk = newChunk
                    } else {
                        if (currChunk.isNotEmpty()) {
                            chunks.add(currChunk)
                        }
                        currChunk = word
                    }
                }
                if (currChunk.isNotEmpty()) {
                    chunks.add(currChunk)
                }
                Log.e("APP", "Chunks are ${chunks.toTypedArray().contentToString()}")

                val overlappingChunks = ArrayList<String>(chunks)
                if (chunkOverlap > 1 && chunks.size > 0) {
                    for (i in 0..<chunks.size - 1) {
                        val overlapStart = max(0, chunks[i].length - chunkOverlap)
                        val overlapEnd = min(chunkOverlap, chunks[i + 1].length)
                        overlappingChunks.add(
                            chunks[i].substring(overlapStart) +
                                " " +
                                chunks[i + 1].substring(0..<overlapEnd)
                        )
                    }
                }

                textChunks.addAll(overlappingChunks)
            }
            return textChunks
        }
    }
}
