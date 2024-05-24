package com.ml.shubham0204.docqa.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.docqa.domain.ChunksUseCase
import com.ml.shubham0204.docqa.domain.DocumentsUseCase
import com.ml.shubham0204.docqa.domain.QAUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocsViewModel
@Inject
constructor(
    val documentsUseCase: DocumentsUseCase,
    val chunksUseCase: ChunksUseCase,
    val qaUseCase: QAUseCase
) : ViewModel() {

    val documentsFlow = documentsUseCase.getAllDocuments()
}
