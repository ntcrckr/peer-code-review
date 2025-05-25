package ru.ntcrckr.peer.code.review

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.ntcrckr.peer.code.review.dao.AppSetup
import ru.ntcrckr.peer.code.review.dao.setupDatabase
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction
import ru.ntcrckr.peer.code.review.ui.Overview
import ru.ntcrckr.peer.code.review.ui.Setup

fun main() = application {
    setupDatabase()
    Window(
        title = "Платформа Peer Code Review",
        state = rememberWindowState(placement = WindowPlacement.Maximized),
        onCloseRequest = ::exitApplication,
    ) {
        var isSetupComplete by remember { mutableStateOf(false) }
        var setupCompleteTrigger by remember { mutableStateOf(false) }
        LaunchedEffect(setupCompleteTrigger) {
            isSetupComplete = pcrpTransaction { AppSetup.get() } != null
            setupCompleteTrigger = false
        }
        when (isSetupComplete) {
            false -> Setup(onSetupComplete = { setupCompleteTrigger = true })
            true -> Overview()
        }
    }
}