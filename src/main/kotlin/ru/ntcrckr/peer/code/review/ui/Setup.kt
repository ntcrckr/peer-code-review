package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.AppSetup
import ru.ntcrckr.peer.code.review.dao.AppSetupEntity
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction
import kotlin.io.path.Path
import kotlin.io.path.notExists

@Composable
@Preview
fun Setup(
    onSetupComplete: () -> Unit,
) = Column(modifier = Modifier.padding(16.dp)) {
    AddEntityPopup(
        title = "Настройка приложения",
        fields = listOf(
            TextField(
                label = "Имя пользователя GitHub",
                errorMessage = "Имя пользователя не может быть пустым",
                isError = { it.isBlank() },
            ),
            TextField(
                label = "GitHub токен",
                errorMessage = "GitHub токен не может быть пустым",
                isError = { it.isBlank() },
            ),
            TextField(
                label = "Путь к папке для хранения исходных репозиториев",
                errorMessage = "Такого пути не существует",
                isError = { it.isBlank() || Path(it).toAbsolutePath().notExists() },
            ),
            TextField(
                label = "Путь к папке для хранения копированных репозиториев (не может совпадать с папкой для исходных репозиториев)",
                errorMessage = "Такого пути не существует",
                isError = { it.isBlank() || Path(it).toAbsolutePath().notExists() },
            )
        ),
        onAdd = { (teacherUsername, githubToken, sourceReposFolder, copyReposFolder) ->
            pcrpTransaction {
                AppSetup.upsert(AppSetupEntity(teacherUsername, githubToken, sourceReposFolder, copyReposFolder))
            }
        },
        showPopup = true,
        canBeClosed = false,
        onClose = onSetupComplete,
    )
}