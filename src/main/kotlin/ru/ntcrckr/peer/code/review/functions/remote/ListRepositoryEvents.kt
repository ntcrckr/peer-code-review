package ru.ntcrckr.peer.code.review.functions.remote

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.ntcrckr.peer.code.review.client

suspend fun listRepositoryEvents(username: String, repoName: String) {
    val response = client.get {
        url {
            protocol = URLProtocol.HTTPS
            host = "api.github.com"
            appendPathSegments("repos", username, repoName, "events")
        }
        headers {
            accept(ContentType("application", "vnd.github+json"))
        }
        bearerAuth(System.getenv("OAUTH_TOKEN"))
    }
    val body: String = response.body()
    val prettyJson = Json { prettyPrint = true }
    println(
        prettyJson.encodeToString(
            prettyJson.parseToJsonElement(body)
        )
    )
}