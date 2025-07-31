package com.example.spendmend.screens

// IncomeDialog.kt

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField

@Composable
fun IncomeDialog(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val income = input.toDoubleOrNull()
                if (income != null && income > 0) {
                    onSave(income) // ✅ just call onSave
                } else {
                    isError = true
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Enter Monthly Income") },
        text = {
            Column {
                TextField(
                    value = input,
                    onValueChange = {
                        input = it
                        isError = false
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Income Amount") },
                    isError = isError
                )
                if (isError) {
                    Text("Please enter a valid amount", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}
