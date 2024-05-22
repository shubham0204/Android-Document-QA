package com.ml.shubham0204.docqa

import android.app.Application
import com.ml.shubham0204.docqa.data.ObjectBoxDB
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DocQAApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBoxDB.init(this)
    }
}
