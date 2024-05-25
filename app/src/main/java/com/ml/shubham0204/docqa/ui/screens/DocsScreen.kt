package com.ml.shubham0204.docqa.ui.screens

import AppProgressDialog
import android.content.Intent
import android.provider.OpenableColumns
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ml.shubham0204.docqa.R
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.ui.components.AppAlertDialog
import com.ml.shubham0204.docqa.ui.components.createAlertDialog
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import com.ml.shubham0204.docqa.ui.viewmodels.DocsViewModel
import hideProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import showProgressDialog

@Composable
fun DocsScreen(onBackClick: (() -> Unit)) {
    DocQATheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val docsViewModel: DocsViewModel = hiltViewModel()
            Column(modifier = Modifier.padding(innerPadding).padding(top = 22.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back",
                        modifier = Modifier.clickable { onBackClick() }.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "Manage Documents",
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().weight(1f),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                DocsList(docsViewModel)
                DocOperations(docsViewModel)
                // AnswerQuestion(docsViewModel)
                AppProgressDialog()
                AppAlertDialog()
            }
        }
    }
}

@Composable
private fun ColumnScope.DocsList(docsViewModel: DocsViewModel) {
    val docs by docsViewModel.documentsFlow.collectAsState(emptyList())
    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
        items(docs) { doc ->
            DocsListItem(
                doc,
                onRemoveDocClick = { docId -> docsViewModel.documentsUseCase.removeDocument(docId) }
            )
        }
    }
}

@Composable
private fun DocsListItem(document: Document, onRemoveDocClick: ((Long) -> Unit)) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Text(
                    text = document.docFileName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = document.docText.trim().replace("\n", ""),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateUtils.getRelativeTimeSpanString(document.docAddedTime).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
            Icon(
                modifier =
                    Modifier.clickable {
                        createAlertDialog(
                            dialogTitle = "Remove document",
                            dialogText =
                                "Are you sure to remove this document from the database. Responses to " +
                                    "further queries will not refer content from this document.",
                            dialogPositiveButtonText = "Remove",
                            onPositiveButtonClick = { onRemoveDocClick(document.docId) },
                            dialogNegativeButtonText = "Cancel",
                            onNegativeButtonClick = {}
                        )
                    },
                imageVector = Icons.Default.Clear,
                contentDescription = "Remove this document"
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Composable
private fun DocOperations(docsViewModel: DocsViewModel) {
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

    Row(
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { launcher.launch(intent) }) { Text(text = "Add document") }
        Button(onClick = { launcher.launch(intent) }) { Text(text = "Add document") }
    }
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
