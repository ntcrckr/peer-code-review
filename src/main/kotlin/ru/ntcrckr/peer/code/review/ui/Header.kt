package ru.ntcrckr.peer.code.review.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.ntcrckr.peer.code.review.dao.AppSetupEntity

@Composable
fun Header(
    appSetup: AppSetupEntity,
    setupUpdated: () -> Unit,
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Пользователь GitHub:")
        LinkText(appSetup.userUrl, appSetup.userUrl.path.substringAfter("/"))
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
            setupUpdated()
        },
    )
}