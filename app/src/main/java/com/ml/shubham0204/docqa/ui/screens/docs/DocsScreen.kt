package com.ml.shubham0204.docqa.ui.screens.docs

import AppProgressDialog
import android.content.Intent
import android.provider.OpenableColumns
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.window.Dialog
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.domain.readers.Readers
import com.ml.shubham0204.docqa.ui.components.AppAlertDialog
import com.ml.shubham0204.docqa.ui.components.createAlertDialog
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import hideProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import showProgressDialog

private val showDocDetailDialog = mutableStateOf(false)
private val dialogDoc = mutableStateOf<Document?>(null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsScreen(onBackClick: (() -> Unit)) {
    DocQATheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Manage Documents",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate Back",
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            val docsViewModel: DocsViewModel = koinViewModel()
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(12.dp))
                DocsList(docsViewModel)
                DocOperations(docsViewModel)
                AppProgressDialog()
                AppAlertDialog()
                DocDetailDialog()
            }
        }
    }
}

@Composable
private fun ColumnScope.DocsList(docsViewModel: DocsViewModel) {
    val docs by docsViewModel.getAllDocuments().collectAsState(emptyList())
    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
        items(docs) { doc ->
            DocsListItem(
                doc.copy(
                    docText =
                        if (doc.docText.length > 200) {
                            doc.docText.substring(0, 200) + " ..."
                        } else {
                            doc.docText
                        },
                ),
                onRemoveDocClick = { docId -> docsViewModel.removeDocument(docId) },
            )
        }
    }
}

@Composable
private fun DocsListItem(
    document: Document,
    onRemoveDocClick: ((Long) -> Unit),
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    dialogDoc.value = document
                    showDocDetailDialog.value = true
                }.background(Color.White)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text(
                text = document.docFileName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = document.docText.trim().replace("\n", ""),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = DateUtils.getRelativeTimeSpanString(document.docAddedTime).toString(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray,
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
                        onNegativeButtonClick = {},
                    )
                },
            imageVector = Icons.Default.Clear,
            contentDescription = "Remove this document",
        )
        Spacer(modifier = Modifier.width(2.dp))
    }
}

@Composable
private fun DocOperations(docsViewModel: DocsViewModel) {
    val context = LocalContext.current
    // Intent to get file from user's device
    // See https://developer.android.com/guide/components/intents-common#GetFile
    var docType = Readers.DocumentType.PDF
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
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
                        docsViewModel.addDocument(
                            inputStream,
                            docFileName,
                            docType,
                        )
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
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            onClick = {
                docType = Readers.DocumentType.PDF
                launcher.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply { type = "application/pdf" },
                )
            },
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add PDF document")
            Text(text = "PDF")
        }
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            onClick = {
                docType = Readers.DocumentType.MS_DOCX
                launcher.launch(
                    Intent(Intent.ACTION_GET_CONTENT).apply {
                        type =
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    },
                )
            },
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add DOCX document")
            Text(text = "DOCX")
        }
    }
}

@Composable
private fun DocDetailDialog() {
    var isVisible by remember { showDocDetailDialog }
    val context = LocalContext.current
    val doc by remember { dialogDoc }
    if (isVisible && doc != null) {
        Dialog(onDismissRequest = { /* Progress dialogs are non-cancellable */ }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(24.dp),
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = doc?.docFileName ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = doc?.docText ?: "",
                        modifier = Modifier.height(200.dp).verticalScroll(rememberScrollState()),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            onClick = {
                                val sendIntent: Intent =
                                    Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, doc?.docText)
                                        type = "text/plain"
                                    }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                        ) {
                            Text(text = "Share Text")
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            onClick = { isVisible = false },
                        ) {
                            Text(text = "Close")
                        }
                    }
                }
            }
        }
    }
}
