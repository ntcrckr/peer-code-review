package ru.ntcrckr.peer.code.review

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import ru.ntcrckr.peer.code.review.dao.AppSetup
import ru.ntcrckr.peer.code.review.dao.setupDatabase
import ru.ntcrckr.peer.code.review.pair.RepoPairController
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction
import ru.ntcrckr.peer.code.review.ui.Overview
import ru.ntcrckr.peer.code.review.ui.Setup

@Preview
fun main() = application {
    setupDatabase()
    Window(
        title = "Платформа Peer Code Review",
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(Alignment.Center),
            size = DpSize(960.dp, 540.dp),
        ),
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

            true -> {
                var setupUpdatedTrigger by remember { mutableStateOf(false) }
                var appSetup by remember { mutableStateOf(pcrpTransaction { AppSetup.get()!! }) }
                val controller = RepoPairController()
                LaunchedEffect(setupUpdatedTrigger) {
                    appSetup = pcrpTransaction { AppSetup.get()!! }
                    controller.updateAppSetup(appSetup)
                    setupUpdatedTrigger = false
                }
                Overview(setupUpdated = { setupUpdatedTrigger = true }, appSetup, controller)
            }
        }
    }
}