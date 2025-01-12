package com.ml.shubham0204.docqa.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

private var title = ""
private var text = ""
private var positiveButtonText = ""
private var negativeButtonText: String? = null
private var positiveButtonOnClick: (() -> Unit) = {}
private var negativeButtonOnClick: (() -> Unit)? = null
private val alertDialogShowStatus = mutableStateOf(false)

@Composable
fun AppAlertDialog() {
    val visible by remember { alertDialogShowStatus }
    if (visible) {
        AlertDialog(
            title = { Text(text = title) },
            text = { Text(text = text) },
            onDismissRequest = { /* All alert dialogs are non-cancellable */ },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertDialogShowStatus.value = false
                        positiveButtonOnClick()
                    },
                ) {
                    Text(text = positiveButtonText)
                }
            },
            dismissButton = {
                if (negativeButtonText != null && negativeButtonOnClick != null) {
                    TextButton(
                        onClick = {
                            alertDialogShowStatus.value = false
                            negativeButtonOnClick!!()
                        },
                    ) {
                        Text(text = negativeButtonText!!)
                    }
                }
            },
        )
    }
}

fun createAlertDialog(
    dialogTitle: String,
    dialogText: String,
    dialogPositiveButtonText: String,
    dialogNegativeButtonText: String?,
    onPositiveButtonClick: (() -> Unit),
    onNegativeButtonClick: (() -> Unit)?,
) {
    title = dialogTitle
    text = dialogText
    positiveButtonOnClick = onPositiveButtonClick
    negativeButtonOnClick = onNegativeButtonClick
    positiveButtonText = dialogPositiveButtonText
    negativeButtonText = dialogNegativeButtonText
    alertDialogShowStatus.value = true
}
