package com.ml.shubham0204.docqa.domain.readers

import java.io.InputStream

abstract class Reader {
    abstract fun readFromInputStream(inputStream: InputStream): String?
}
