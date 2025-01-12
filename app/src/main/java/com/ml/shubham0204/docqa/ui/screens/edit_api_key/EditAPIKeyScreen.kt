package com.ml.shubham0204.docqa.ui.screens.edit_api_key

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ml.shubham0204.docqa.ui.theme.DocQATheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAPIKeyScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    DocQATheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Edit API Key",
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
            val viewModel: EditAPIKeyViewModel = koinViewModel()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            ) {
                var apiKey by remember { mutableStateOf(viewModel.getAPIKey() ?: "") }
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            disabledTextColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                    placeholder = { Text(text = "Enter Gemini API key...") },
                )
                Button(
                    enabled = apiKey.isNotBlank(),
                    onClick = {
                        viewModel.saveAPIKey(apiKey)
                        Toast.makeText(context, "API key saved", Toast.LENGTH_LONG).show()
                    },
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save API key")
                    Text(text = "Save API Key")
                }
            }
        }
    }
}
