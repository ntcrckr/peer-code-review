package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.*
import ru.ntcrckr.peer.code.review.pair.suspendPcrpTransaction
import ru.ntcrckr.peer.code.review.ui.StudentPair.Companion.toStudentPair

@Composable
@Preview
fun ClassOverview() {
    var classes by remember { mutableStateOf(listOf<ClassEntity>()) }
    var selectedClass by remember { mutableStateOf<ClassEntity?>(null) }
    LaunchedEffect(Unit) {
        classes = suspendPcrpTransaction { ClassService.Classes.getAll() }
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
        studentPairs = selectedLesson?.let { lessonEntity ->
            suspendPcrpTransaction { RepoPairs.getAllForLesson(lessonEntity.id).map { it.toStudentPair() } }
        } ?: emptyList()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Список проверки", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DropdownPicker(
                options = classes,
                selectedOption = selectedClass,
                defaultText = "Выберите группу",
                onOptionSelect = { selectedClass = it },
            )
            AddTextButtonAndPopup(
                title = "Добавление новой группы",
                label = "Название группы",
                errorMessage = "Название группы не может быть пустым",
                onAdd = { println("Adding class: $it") },
            )
            Spacer(Modifier.width(16.dp))
            DropdownPicker(
                options = lessons,
                selectedOption = selectedLesson,
                defaultText = "Выберите занятие",
                onOptionSelect = { selectedLesson = it },
            )
            AddTextButtonAndPopup(
                title = "Добавление нового занятия",
                label = "Название занятия",
                errorMessage = "Название занятия не может быть пустым",
                onAdd = { println("Adding lesson: $it") },
            )
        }
        Spacer(Modifier.height(16.dp))
        StudentPairsTable(studentPairs)
    }
}