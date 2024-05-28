package com.ml.shubham0204.docqa.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.domain.ChunksUseCase
import com.ml.shubham0204.docqa.domain.DocumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocsViewModel
@Inject
constructor(val documentsUseCase: DocumentsUseCase) :
    ViewModel() {

    val documentsFlow = documentsUseCase.getAllDocuments()

}
