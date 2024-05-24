package com.ml.shubham0204.docqa.domain.readers

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.IOException
import java.io.InputStream

class DOCXReader : Reader() {

    override fun readFromInputStream(inputStream: InputStream): String? {
        try {
            val document = XWPFDocument(inputStream)
            val paragraphs = document.paragraphs
            val text = StringBuilder()
            for (paragraph in paragraphs) {
                text.append(" ").append(paragraph.text)
            }
            return text.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
