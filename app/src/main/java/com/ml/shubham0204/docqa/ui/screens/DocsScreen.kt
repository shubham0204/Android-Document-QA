package com.ml.shubham0204.docqa.ui.screens

import AppProgressDialog
import android.content.Intent
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ml.shubham0204.docqa.R
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import com.ml.shubham0204.docqa.ui.viewmodels.DocsViewModel
import hideProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import showProgressDialog

@Composable
fun DocsScreen() {
    DocQATheme {
        Surface(modifier = Modifier.background(Color.White)) {
            val docsViewModel: DocsViewModel = hiltViewModel()
            Column {
                DocsList(docsViewModel)
                AddDocumentsButton(docsViewModel)
                AnswerQuestion(docsViewModel)
                AppProgressDialog()
            }
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

@Composable
private fun AddDocumentsButton(docsViewModel: DocsViewModel) {
    val context = LocalContext.current

    // Intent to get file from user's device
    // See https://developer.android.com/guide/components/intents-common#GetFile
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "application/pdf" }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let { uri ->
                var docFileName = ""
                // Retrieve file information from URI
                // See
                // https://developer.android.com/training/secure-file-sharing/retrieve-info#RetrieveFileInfo
                context.contentResolver.query(uri, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    docFileName = cursor.getString(nameIndex)
                }
                context.contentResolver.openInputStream(uri)?.let { inputStream ->
                    showProgressDialog()
                    CoroutineScope(Dispatchers.IO).launch {
                        docsViewModel.documentsUseCase.addDocument(inputStream, docFileName)
                        withContext(Dispatchers.Main) {
                            hideProgressDialog()
                            inputStream.close()
                        }
                    }
                }
            }
        }

    Button(onClick = { launcher.launch(intent) }) { Text(text = "Add document") }
}

@Composable
private fun AnswerQuestion(docsViewModel: DocsViewModel) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    TextField(value = query, onValueChange = { query = it })
    Button(
        onClick = {
            showProgressDialog()
            docsViewModel.qaUseCase.getAnswer(query, context.getString(R.string.prompt_1)) {
                hideProgressDialog()
                Log.e("RAGResponse", "Response $it")
            }
        }
    ) {
        Text(text = "Answer")
    }
}
