package com.ml.shubham0204.docqa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import com.ml.shubham0204.docqa.ui.viewmodels.DocsViewModel

@Composable
fun DocsScreen() {
    DocQATheme {
        Surface(modifier = Modifier.background(Color.White)) {
            val docsViewModel: DocsViewModel = hiltViewModel()
            Column { DocsList(docsViewModel = docsViewModel) }
        }
    }
}

@Composable
private fun DocsList(docsViewModel: DocsViewModel) {
    val docs by docsViewModel.documentsFlow.collectAsState(emptyList())
    LazyColumn { items(docs) { DocsListItem(doc = it) } }
}

@Composable
private fun DocsListItem(doc: Document) {
    Column { Text(text = doc.docFileName) }
}
