package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.*
import ru.ntcrckr.peer.code.review.dao.LessonEntity
import ru.ntcrckr.peer.code.review.dao.Lessons
import ru.ntcrckr.peer.code.review.pair.suspendPcrpTransaction
import ru.ntcrckr.peer.code.review.ui.StudentPair.Companion.toStudentPair

@Composable
@Preview
fun ClassOverview() {
    var classes by remember { mutableStateOf(listOf<ClassEntity>()) }
    var selectedClass by remember { mutableStateOf<ClassEntity?>(null) }
    LaunchedEffect(Unit) {
        suspendPcrpTransaction { classes = ClassService.Classes.getAll() }
        selectedClass = classes.firstOrNull()
    }
    var lessons by remember { mutableStateOf(listOf<LessonEntity>()) }
    var selectedLesson by remember { mutableStateOf<LessonEntity?>(null) }
    LaunchedEffect(selectedClass) {
        lessons = selectedClass?.let {
            suspendPcrpTransaction { Lessons.getAllForClass(it.id) }
        } ?: emptyList()
        selectedLesson = lessons.firstOrNull()
    }
    var studentPairs by remember { mutableStateOf(listOf<StudentPair>()) }
    LaunchedEffect(selectedLesson) {
        selectedLesson?.let { lessonEntity ->
            suspendPcrpTransaction {
                studentPairs = RepoPairs
                    .getAllForLesson(lessonEntity.id)
                    .map { it.toStudentPair() }
            }
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Class Overview", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(16.dp))
        DropdownPicker(
            options = classes,
            selectedOption = selectedClass,
            defaultText = "Select a class",
            onOptionSelect = { selectedClass = it },
        )
        Spacer(Modifier.height(16.dp))
        DropdownPicker(
            options = lessons,
            selectedOption = selectedLesson,
            defaultText = "Select a lesson",
            onOptionSelect = { selectedLesson = it },
        )
        Spacer(Modifier.height(16.dp))
        StudentPairsTable(studentPairs)
    }
}