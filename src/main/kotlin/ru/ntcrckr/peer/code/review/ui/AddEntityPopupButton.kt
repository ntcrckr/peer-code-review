package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

data class TextField(
    val label: String,
    val errorMessage: String,
    val isError: (String) -> Boolean,
)

@Composable
@Preview
fun AddEntityPopupButton(
    icon: ImageVector,
    title: String,
    fields: List<TextField>,
    onAdd: (List<String>) -> Unit,
) {
    var showPopup by remember { mutableStateOf(false) }
    IconButton(onClick = { showPopup = true }) {
        Icon(icon, contentDescription = title)
    }
    AddEntityPopup(
        title = title,
        fields = fields,
        onAdd = onAdd,
        showPopup = showPopup,
        canBeClosed = true,
        onClose = { showPopup = false },
    )
}

@Composable
@Preview
fun AddEntityPopup(
    title: String,
    fields: List<TextField>,
    onAdd: (List<String>) -> Unit,
    showPopup: Boolean,
    canBeClosed: Boolean,
    onClose: () -> Unit,
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
            ) {
                CheckedTextFields(title, fields, onAdd, canBeClosed, onClose)
            }
        }
    }
}

@Composable
@Preview
fun CheckedTextFields(
    title: String,
    fields: List<TextField>,
    onAdd: (List<String>) -> Unit,
    canBeClosed: Boolean,
    onClose: () -> Unit,
) = Column(
    modifier = Modifier.padding(16.dp).fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    Text(
        text = title,
        style = MaterialTheme.typography.subtitle1
    )
    val values = remember { MutableList(fields.size) { mutableStateOf("") } }
    val isErrors = remember { MutableList(fields.size) { mutableStateOf(false) } }
    fields.forEachIndexed { idx, textField ->
        CheckedTextField(
            label = textField.label,
            value = values[idx].value,
            isError = isErrors[idx].value,
            error = textField.errorMessage,
            onChange = { newValue ->
                values[idx].value = newValue
                if (isErrors[idx].value && !textField.isError(newValue))
                    isErrors[idx].value = false
            }
        )
    }
    Row(
        modifier = Modifier.align(Alignment.End),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (canBeClosed) Button(onClick = onClose) { Text("Закрыть") }
        Button(onClick = {
            values.forEachIndexed { idx, value ->
                if (fields[idx].isError(value.value))
                    isErrors[idx].value = true
            }
            if (isErrors.all { !it.value }) {
                onAdd(values.map { it.value })
                values.forEach { it.value = "" }
                isErrors.forEach { it.value = false }
                onClose()
            }
        }) { Text("Добавить") }
    }
}

@Composable
@Preview
fun CheckedTextField(label: String, value: String, isError: Boolean, error: String, onChange: (String) -> Unit) {
    TextField(
        value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(), label = { Text(label) },
        isError = isError, singleLine = true, colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = if (isError) Red else colors.primary,
            unfocusedIndicatorColor = if (isError) Red else colors.onSurface.copy(alpha = 0.5f)
        )
    )
    if (isError) Text(text = error, color = Red, style = MaterialTheme.typography.caption)
}