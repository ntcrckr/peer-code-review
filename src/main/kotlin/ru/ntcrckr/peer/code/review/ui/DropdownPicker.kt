package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*

@Composable
@Preview
fun <T : DropdownOption> DropdownPicker(
    options: List<T>,
    selectedOption: T?,
    defaultText: String,
    onOptionSelect: (T) -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { dropdownExpanded = true }) {
            Text(selectedOption?.text ?: defaultText)
        }
        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            options.forEach { cls ->
                DropdownMenuItem(onClick = {
                    onOptionSelect(cls)
                    dropdownExpanded = false
                }) {
                    Text(cls.text)
                }
            }
        }
    }
}