package ru.ntcrckr.peer.code.review.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun StudentPairsTable(studentPairs: List<StudentPair>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Users from Database", style = MaterialTheme.typography.h6)
        Spacer(Modifier.height(8.dp))

        // Header row
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Проверяемый", modifier = Modifier.weight(2f))
            Text("Проверяющий", modifier = Modifier.weight(2f))
            Text("Репозиторий", modifier = Modifier.weight(4f))
            Text("Копия репозитория", modifier = Modifier.weight(4f))
        }
        Divider()

        // Data rows
        LazyColumn {
            items(studentPairs) { userPair ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(userPair.performer.username, modifier = Modifier.weight(2f))
                    Text(userPair.reviewer.username, modifier = Modifier.weight(2f))
                    RepositoryLinkText(userPair.sourceUrl, modifier = Modifier.weight(4f))
                    RepositoryLinkText(userPair.copyUrl, modifier = Modifier.weight(4f))
                }
                Divider()
            }
        }
    }
}