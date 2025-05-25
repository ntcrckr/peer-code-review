package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.*
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction
import ru.ntcrckr.peer.code.review.pair.suspendPcrpTransaction

@Composable
@Preview
fun Overview() = Column(modifier = Modifier.padding(16.dp)) {
    var selectedLesson by remember { mutableStateOf<LessonEntity?>(null) }

    Header()
    Spacer(Modifier.height(8.dp))
    Text("Обзор проверок", style = MaterialTheme.typography.h5)
    Spacer(Modifier.height(16.dp))
    Navigation(
        selectedLesson = selectedLesson,
        selectLesson = { selectedLesson = it },
    )
    Spacer(Modifier.height(8.dp))
    StudentPairsTable(selectedLesson)
}

@Composable
private fun Header() = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceEvenly,
) {
    var appSetup by remember { mutableStateOf(pcrpTransaction { AppSetup.get()!! }) }
    var setupUpdatedTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(setupUpdatedTrigger) {
        appSetup = pcrpTransaction { AppSetup.get()!! }
        setupUpdatedTrigger = false
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Пользователь GitHub:")
        LinkText(appSetup.userUrl)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Папка с исходными репозиториями:")
        PathLinkText(appSetup.sourceReposFolder, appSetup.sourceReposFolder.fileName.toString())
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Папка с копиями репозиториев:")
        PathLinkText(appSetup.copyReposFolder, appSetup.copyReposFolder.fileName.toString())
    }
    AddEntityPopupButton(
        icon = Icons.Filled.Edit,
        title = "Обновление настроек приложения",
        fields = setupFields,
        onAdd = { (teacherUsername, githubToken, sourceReposFolder, copyReposFolder) ->
            updateSetup(listOf(teacherUsername, githubToken, sourceReposFolder, copyReposFolder))
            setupUpdatedTrigger = true
        },
    )
}

@Composable
private fun Navigation(
    selectedLesson: LessonEntity?,
    selectLesson: (LessonEntity?) -> Unit,
) = Row(verticalAlignment = Alignment.CenterVertically) {
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