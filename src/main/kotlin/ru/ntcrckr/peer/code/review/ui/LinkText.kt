package ru.ntcrckr.peer.code.review.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import java.awt.Desktop
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

@Composable
fun LinkText(url: URI, text: String? = null, modifier: Modifier? = null) {
    val initialModifier = Modifier.clickable {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(url)
        }
    }
    val finalModifier = when (modifier) {
        null -> initialModifier
        else -> initialModifier.then(modifier)
    }
    Text(
        text = text ?: url.toString(),
        color = Color.Blue,
        textDecoration = TextDecoration.Underline,
        modifier = finalModifier,
    )
}

@Composable
fun PathLinkText(path: Path, text: String? = null) {
    Text(
        text = text ?: path.toString(),
        color = Color.Blue,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable {
            openFolder(path)
        }
    )
}

fun openFolder(path: Path) {
    try {
        if (path.exists() && path.isDirectory()) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(path.toFile())
            } else {
                println("Desktop is not supported on this platform")
            }
        } else {
            println("Folder does not exist: $path")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}