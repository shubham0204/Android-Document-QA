package com.ml.shubham0204.docqa.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.docqa.data.DocumentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocsViewModel @Inject constructor(documentsRepository: DocumentsRepository) : ViewModel() {

    val documentsFlow = documentsRepository.getAllDocuments()
}
