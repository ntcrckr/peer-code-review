package ru.ntcrckr.peer.code.review.functions.remote

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ntcrckr.peer.code.review.client
import java.io.File

suspend fun downloadRepository(fileName: String, username: String, repoName: String, refPath: String) {
    val file = File(fileName)

    val response = client.get {
        url {
            protocol = URLProtocol.HTTPS
            host = "api.github.com"
            appendPathSegments("repos", username, repoName, "zipball", refPath)
        }
    }
    withContext(Dispatchers.IO) {
        file.createNewFile()
    }
    val body: ByteArray = response.body()
    file.writeBytes(body)
}