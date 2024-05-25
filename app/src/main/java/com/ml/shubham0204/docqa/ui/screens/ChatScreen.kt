package com.ml.shubham0204.docqa.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ml.shubham0204.docqa.ui.theme.DocQATheme

@Composable
fun ChatScreen(onOpenDocsClick: (() -> Unit)) {
    DocQATheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Text(text = "Type message here")
                Button(onClick = onOpenDocsClick) { Text(text = "Open Docs") }
            }
        }
    }
}
