package com.ml.shubham0204.docqa.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ml.shubham0204.docqa.R
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import com.ml.shubham0204.docqa.ui.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onOpenDocsClick: (() -> Unit)) {
    DocQATheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Chat", style = MaterialTheme.typography.headlineSmall) },
                    actions = {
                        IconButton(onClick = onOpenDocsClick) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Open Documents"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth()) {
                val chatViewModel: ChatViewModel = hiltViewModel()
                Column {
                    QALayout(chatViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                    QueryInput(chatViewModel)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.QALayout(chatViewModel: ChatViewModel) {
    val question by remember { chatViewModel.questionState }
    val response by remember { chatViewModel.responseState }
    val isGeneratingResponse by remember { chatViewModel.isGeneratingResponseState }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).weight(1f),
    ) {
        if (question.trim().isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(75.dp),
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
                Text(text = "Enter a query to see answers", style=MaterialTheme.typography.labelSmall)
            }
        } else {
            Text(text = question, style = MaterialTheme.typography.headlineLarge)
            if (isGeneratingResponse) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = response,
                    color = Color.White,
                    modifier =
                        Modifier.background(Color.Blue, RoundedCornerShape(16.dp))
                            .padding(32.dp)
                            .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun QueryInput(chatViewModel: ChatViewModel) {
    var questionText by remember { mutableStateOf("") }
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.fillMaxWidth().background(Color.White).weight(1f),
            value = questionText,
            onValueChange = { questionText = it },
            shape = RoundedCornerShape(16.dp),
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = Color.Blue,
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
            placeholder = { Text(text = "Ask documents...") }
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            onClick = {
                chatViewModel.questionState.value = questionText
                questionText = ""
                chatViewModel.isGeneratingResponseState.value = true
                chatViewModel.qaUseCase.getAnswer(
                    chatViewModel.questionState.value,
                    context.getString(R.string.prompt_1)
                ) {
                    chatViewModel.isGeneratingResponseState.value = false
                    chatViewModel.responseState.value = it
                }
            }
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send query")
        }
    }
}
