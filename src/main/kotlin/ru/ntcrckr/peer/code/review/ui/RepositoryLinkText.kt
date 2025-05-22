package ru.ntcrckr.peer.code.review.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import java.awt.Desktop
import java.net.URI

@Composable
fun RepositoryLinkText(url: URI, modifier: Modifier) {
    Text(
        text = url.toString(),
        color = Color.Blue,
        textDecoration = TextDecoration.Underline,
        modifier = modifier.then(Modifier.clickable {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(url)
            }
        }),
    )
}