package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.AppSetupEntity
import ru.ntcrckr.peer.code.review.dao.LessonEntity
import ru.ntcrckr.peer.code.review.pair.RepoPairController

@Composable
@Preview
fun Overview(
    setupUpdated: () -> Unit,
    appSetup: AppSetupEntity,
    controller: RepoPairController,
) = Column(modifier = Modifier.padding(16.dp)) {
    var selectedLesson by remember { mutableStateOf<LessonEntity?>(null) }

    Header(appSetup = appSetup, setupUpdated = setupUpdated)
    Spacer(Modifier.height(24.dp))
    Text("Обзор проверок", style = MaterialTheme.typography.h3, modifier = Modifier.align(Alignment.CenterHorizontally))
    Spacer(Modifier.height(16.dp))
    Navigation(
        selectedLesson = selectedLesson,
        selectLesson = { selectedLesson = it },
    )
    Spacer(Modifier.height(8.dp))
    if (selectedLesson != null)
        StudentPairsTable(selectedLesson, controller)
}