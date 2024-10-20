package com.ml.shubham0204.docqa.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.docqa.domain.DocumentsUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DocsViewModel(val documentsUseCase: DocumentsUseCase) : ViewModel() {

    val documentsFlow = documentsUseCase.getAllDocuments()
}
