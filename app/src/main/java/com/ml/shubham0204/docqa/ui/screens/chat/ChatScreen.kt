package com.ml.shubham0204.docqa.ui.screens.chat

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.ml.shubham0204.docqa.R
import com.ml.shubham0204.docqa.ui.components.AppAlertDialog
import com.ml.shubham0204.docqa.ui.components.createAlertDialog
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onOpenDocsClick: (() -> Unit),
    onEditAPIKeyClick: (() -> Unit),
) {
    DocQATheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Chat", style = MaterialTheme.typography.headlineSmall) },
                    actions = {
                        IconButton(onClick = onOpenDocsClick) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Open Documents",
                            )
                        }
                        IconButton(onClick = onEditAPIKeyClick) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Edit API Key",
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth()) {
                val chatViewModel: ChatViewModel = koinViewModel()
                Column {
                    QALayout(chatViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                    QueryInput(chatViewModel, onEditAPIKeyClick)
                }
            }
            AppAlertDialog()
        }
    }
}

@Composable
private fun ColumnScope.QALayout(chatViewModel: ChatViewModel) {
    val question by chatViewModel.questionState.collectAsState()
    val response by chatViewModel.responseState.collectAsState()
    val isGeneratingResponse by chatViewModel.isGeneratingResponseState.collectAsState()
    val retrievedContextList by chatViewModel.retrievedContextListState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().weight(1f),
    ) {
        if (question.trim().isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier.size(75.dp),
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.LightGray,
                )
                Text(
                    text = "Enter a query to see answers",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                )
            }
        } else {
            LazyColumn {
                item {
                    Text(text = question, style = MaterialTheme.typography.headlineLarge)
                    if (isGeneratingResponse) {
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                item {
                    if (!isGeneratingResponse) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier =
                                Modifier
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                        ) {
                            MarkdownText(
                                modifier = Modifier.fillMaxWidth(),
                                markdown = response,
                                style =
                                    TextStyle(
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                    ),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                IconButton(
                                    onClick = {
                                        val sendIntent: Intent =
                                            Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, response)
                                                type = "text/plain"
                                            }
                                        val shareIntent = Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share the response",
                                        tint = Color.Black,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Context", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                if (!isGeneratingResponse) {
                    items(retrievedContextList) { retrievedContext ->
                        Column(
                            modifier =
                                Modifier
                                    .padding(8.dp)
                                    .background(Color.Cyan, RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                        ) {
                            Text(
                                text = "\"${retrievedContext.context}\"",
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                            )
                            Text(
                                text = retrievedContext.fileName,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 10.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueryInput(
    chatViewModel: ChatViewModel,
    onEditAPIKeyClick: () -> Unit,
) {
    var questionText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.fillMaxWidth().weight(1f),
            value = questionText,
            onValueChange = { questionText = it },
            shape = RoundedCornerShape(16.dp),
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            placeholder = { Text(text = "Ask documents...") },
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            modifier = Modifier.background(Color.Blue, CircleShape),
            onClick = {
                keyboardController?.hide()
                if (!chatViewModel.checkNumDocuments()) {
                    Toast
                        .makeText(context, "Add documents to execute queries", Toast.LENGTH_LONG)
                        .show()
                    return@IconButton
                }
                if (!chatViewModel.checkValidAPIKey()) {
                    createAlertDialog(
                        dialogTitle = "Invalid API Key",
                        dialogText = "Please enter a Gemini API key to use a LLM for generating responses.",
                        dialogPositiveButtonText = "Add API key",
                        onPositiveButtonClick = onEditAPIKeyClick,
                        dialogNegativeButtonText = "Open Gemini Console",
                        onNegativeButtonClick = {
                            Intent(Intent.ACTION_VIEW).apply {
                                data = "https://aistudio.google.com/apikey".toUri()
                                context.startActivity(this)
                            }
                        },
                    )
                    return@IconButton
                }
                if (questionText.trim().isEmpty()) {
                    Toast.makeText(context, "Enter a query to execute", Toast.LENGTH_LONG).show()
                    return@IconButton
                }
                try {
                    chatViewModel.getAnswer(
                        questionText,
                        context.getString(R.string.prompt_1),
                    )
                } catch (e: Exception) {
                    createAlertDialog(
                        dialogTitle = "Error",
                        dialogText = "An error occurred while generating the response: ${e.message}",
                        dialogPositiveButtonText = "Close",
                        onPositiveButtonClick = {},
                        dialogNegativeButtonText = null,
                        onNegativeButtonClick = {},
                    )
                }
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Send query",
                tint = Color.White,
            )
        }
    }
}
