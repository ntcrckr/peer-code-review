package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun <T : DropdownOption> DropdownPicker(
    options: List<T>,
    selectedOption: T?,
    defaultText: String,
    onOptionSelect: (T) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Select:", style = MaterialTheme.typography.h6)
        // Dropdown for classes
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
}