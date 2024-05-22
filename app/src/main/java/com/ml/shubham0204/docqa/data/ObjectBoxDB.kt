package com.ml.shubham0204.docqa.data

import android.content.Context
import io.objectbox.BoxStore

object ObjectBoxDB {

    lateinit var store: BoxStore
        private set

    fun init(context: Context) {
        store = MyObjectBox.builder().androidContext(context).build()
    }
}
