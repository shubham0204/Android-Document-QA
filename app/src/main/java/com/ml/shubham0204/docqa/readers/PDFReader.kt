package com.ml.shubham0204.docqa.readers

import java.io.IOException
import java.io.InputStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

class PDFReader : Reader() {

    override fun readFromInputStream(inputStream: InputStream): String? {
        try {
            val pdfDoc = PDDocument.load(inputStream)
            val textStripper = PDFTextStripper()
            val pdfText = textStripper.getText(pdfDoc)
            pdfDoc.close()
            return pdfText
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
