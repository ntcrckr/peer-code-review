package ru.ntcrckr.peer.code.review

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.ntcrckr.peer.code.review.dao.setupDatabase
import ru.ntcrckr.peer.code.review.ui.ClassOverview

fun main() = application {
    setupDatabase()
    Window(onCloseRequest = ::exitApplication, title = "Peer Code Review Platform") {
        ClassOverview()
    }
}