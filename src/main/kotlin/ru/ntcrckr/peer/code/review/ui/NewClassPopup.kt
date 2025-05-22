package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
@Preview
fun AddTextButtonAndPopup(
    title: String,
    label: String,
    errorMessage: String,
    onAdd: (String) -> Unit,
) {
    var showPopup by remember { mutableStateOf(false) }
    IconButton(onClick = { showPopup = true }) {
        Icon(Icons.Filled.Add, contentDescription = title)
    }
    AddTextPopup(
        showPopup = showPopup,
        title = title,
        label = label,
        errorMessage = errorMessage,
        onClose = { showPopup = false },
        onAdd = onAdd,
    )
}

@Composable
@Preview
fun AddTextPopup(
    showPopup: Boolean,
    title: String,
    label: String,
    errorMessage: String,
    onClose: () -> Unit,
    onAdd: (String) -> Unit,
) {
    if (showPopup) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onClose,
            properties = PopupProperties(focusable = true)
        ) {
            Surface(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(width = 600.dp, height = 300.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(title, style = MaterialTheme.typography.subtitle1)
                    var isError by remember { mutableStateOf(false) }
                    var text by remember { mutableStateOf("") }
                    CheckedTextField(
                        label = label,
                        text = text,
                        isError = isError,
                        error = errorMessage,
                        onChange = {
                            text = it
                            if (isError && it.isNotBlank()) isError = false
                        }
                    )
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(onClick = onClose) { Text("Закрыть") }
                        Button(onClick = {
                            if (text.isBlank()) {
                                isError = true
                            } else {
                                onAdd(text)
                                text = ""
                                isError = false
                                onClose()
                            }
                        }) { Text("Добавить") }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckedTextField(label: String, text: String, isError: Boolean, error: String, onChange: (String) -> Unit) {
    TextField(
        value = text, onValueChange = onChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) },
        isError = isError, singleLine = true, colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = if (isError) Red else colors.primary,
            unfocusedIndicatorColor = if (isError) Red else colors.onSurface.copy(alpha = 0.5f)
        )
    )
    if (isError) Text(text = error, color = Red, style = MaterialTheme.typography.caption)
}