package com.ml.shubham0204.docqa.readers

import java.io.IOException
import java.io.InputStream
import org.apache.poi.xwpf.usermodel.XWPFDocument

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
