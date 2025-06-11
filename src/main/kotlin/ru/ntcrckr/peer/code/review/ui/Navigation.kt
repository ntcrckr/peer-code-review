package ru.ntcrckr.peer.code.review.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.ClassEntity
import ru.ntcrckr.peer.code.review.dao.Classes
import ru.ntcrckr.peer.code.review.dao.LessonEntity
import ru.ntcrckr.peer.code.review.dao.Lessons
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction
import ru.ntcrckr.peer.code.review.pair.suspendPcrpTransaction

@Composable
fun Navigation(
    selectedLesson: LessonEntity?,
    selectLesson: (LessonEntity?) -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
) {
    var selectedClass by remember { mutableStateOf<ClassEntity?>(null) }
    var newClassTrigger by remember { mutableStateOf(false) }
    var newLessonTrigger by remember { mutableStateOf(false) }

    EntityDropdown(
        selectedEntity = selectedClass,
        watched = arrayOf(newClassTrigger),
        getAll = { suspendPcrpTransaction { Classes.getAll() }.also { newClassTrigger = false } },
        onOptionSelect = { selectedClass = it },
        defaultText = "Группы отсутствуют",
    )
    AddEntityPopupButton(
        icon = Icons.Filled.Add,
        title = "Добавление новой группы",
        fields = listOf(
            TextField(
                label = "Название группы",
                errorMessage = "Название группы не может быть пустым",
                isError = { it.isBlank() },
            ),
        ),
        onAdd = { (className) ->
            val insertedClass = pcrpTransaction { Classes.insert(ClassEntity(name = className)) }
            println("Added class: $insertedClass")
            newClassTrigger = true
            selectedClass = insertedClass
        },
    )
    Spacer(Modifier.width(16.dp))
    EntityDropdown(
        selectedEntity = selectedLesson,
        watched = arrayOf(selectedClass, newLessonTrigger),
        getAll = {
            (selectedClass?.let { suspendPcrpTransaction { Lessons.getAllForClass(it.id) } } ?: emptyList())
                .also { newLessonTrigger = false }
        },
        onOptionSelect = selectLesson,
        defaultText = "Занятия отсутствуют",
    )
    AddEntityPopupButton(
        icon = Icons.Filled.Add,
        title = "Добавление нового занятия",
        fields = listOf(
            TextField(
                label = "Название занятия",
                errorMessage = "Название занятия не может быть пустым",
                isError = { it.isBlank() },
            ),
        ),
        onAdd = { (lessonName) ->
            val insertedLesson = pcrpTransaction { Lessons.insert(selectedClass!!.id, LessonEntity(name = lessonName)) }
            println("Added lesson: $insertedLesson")
            newLessonTrigger = true
            selectLesson(insertedLesson)
        },
    )
}