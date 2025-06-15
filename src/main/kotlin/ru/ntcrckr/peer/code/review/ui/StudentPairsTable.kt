package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.AppSetup
import ru.ntcrckr.peer.code.review.dao.LessonEntity
import ru.ntcrckr.peer.code.review.dao.RepoPairs
import ru.ntcrckr.peer.code.review.pair.RepoPairController
import ru.ntcrckr.peer.code.review.pair.suspendPcrpTransaction
import ru.ntcrckr.peer.code.review.ui.StudentPair.Companion.toStudentPair

@Composable
@Preview
fun StudentPairsTable(
    selectedLesson: LessonEntity?,
    controller: RepoPairController,
) = Column(modifier = Modifier.padding(32.dp)) {
    var studentPairs by remember { mutableStateOf(listOf<StudentPair>()) }
    var addPairTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(selectedLesson, addPairTrigger) {
        studentPairs = selectedLesson?.let { lessonEntity ->
            suspendPcrpTransaction {
                val teacherUsername = AppSetup.get()!!.teacherUsername
                RepoPairs.getAllForLesson(lessonEntity.id).map { it.toStudentPair(teacherUsername) }
            }
        } ?: emptyList()
        addPairTrigger = false
    }

    TableTitle(
        canAdd = selectedLesson != null,
        onAddPair = { performerPullLink, reviewerUsername ->
            controller.addPair(selectedLesson!!.id, performerPullLink, reviewerUsername)
            addPairTrigger = true
        }
    )
    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("Проверяемый", modifier = Modifier.weight(2f))
        Text("Проверяющий", modifier = Modifier.weight(2f))
        Text("Репозиторий", modifier = Modifier.weight(4f))
        Text("Копия репозитория", modifier = Modifier.weight(4f))
    }
    Divider()
    LazyColumn {
        items(studentPairs) { userPair ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(userPair.performer.username, modifier = Modifier.weight(2f))
                Text(userPair.reviewer.username, modifier = Modifier.weight(2f))
                LinkText(
                    userPair.sourceUrl,
                    userPair.sourceUrl.path.substringAfter("/"),
                    modifier = Modifier.weight(4f)
                )
                LinkText(userPair.copyUrl, userPair.sourceUrl.path.substringAfter("/"), modifier = Modifier.weight(4f))
            }
            Divider()
        }
    }
}

@Composable
private fun TableTitle(
    canAdd: Boolean,
    onAddPair: (performerPullLink: String, reviewerUsername: String) -> Unit,
) =
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        Text("Проверки в этом классе:", style = MaterialTheme.typography.h6)
        if (canAdd) {
            AddEntityPopupButton(
                icon = Icons.Filled.Add,
                title = "Новая проверка",
                fields = listOf(
                    TextField(
                        label = "Ссылка на PR проверяемого",
                        errorMessage = "Введенное значение не является GitHub ссылкой на PR",
                        isError = { !githubPullUrlRegex.matches(it) },
                    ),
                    TextField(
                        label = "Никнейм проверяющего",
                        errorMessage = "Никнейм не может быть пустым",
                        isError = { it.isBlank() },
                    ),
                ),
                onAdd = { (performerPullLink, reviewerUsername) ->
                    onAddPair(performerPullLink, reviewerUsername)
                },
            )
        }
    }