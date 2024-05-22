package com.ml.shubham0204.docqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ml.shubham0204.docqa.data.Document
import com.ml.shubham0204.docqa.data.DocumentsRepository
import com.ml.shubham0204.docqa.ui.screens.DocsScreen
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ActivityUI() }
    }

    @Composable
    private fun ActivityUI() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                DocsScreen()
                Button(
                    onClick = {
                        val documentsRepository = DocumentsRepository()
                        documentsRepository.addDocument(
                            Document(
                                docText = UUID.randomUUID().toString(),
                                docFileName = "sample.docx"
                            )
                        )
                    }
                ) {
                    Text(text = "Add doc")
                }
            }
        }
    }
}
