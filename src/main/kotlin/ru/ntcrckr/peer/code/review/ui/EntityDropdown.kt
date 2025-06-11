package ru.ntcrckr.peer.code.review.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("EntityDropdown.kt")

interface DropdownOption {
    val text: String
}

@Composable
fun <E : DropdownOption, W : Any?> EntityDropdown(
    selectedEntity: E?,
    watched: Array<W>,
    getAll: suspend (Array<W>) -> List<E>,
    onOptionSelect: (E?) -> Unit,
    defaultText: String,
) = Box {
    var entities by remember { mutableStateOf(listOf<E>()) }
    LaunchedEffect(*watched) {
        entities = getAll(watched)
        logger.info("Watch $watched changed - Updated entities: $entities")
        onOptionSelect(entities.firstOrNull())
    }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Button(onClick = { dropdownExpanded = true }) {
        Text(selectedEntity?.text ?: defaultText)
    }
    DropdownMenu(
        expanded = dropdownExpanded,
        onDismissRequest = { dropdownExpanded = false }
    ) {
        entities.forEach { entity ->
            DropdownMenuItem(onClick = {
                onOptionSelect(entity)
                dropdownExpanded = false
            }) {
                Text(entity.text)
            }
        }
    }
}