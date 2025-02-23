package ru.ntcrckr.peer.code.review.functions.remote

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.URLProtocol.Companion.HTTPS
import ru.ntcrckr.peer.code.review.client

suspend fun createRemoteRepo(repoName: String, isPrivate: Boolean) {
    client.post {
        url {
            protocol = HTTPS
            host = "api.github.com"
            appendPathSegments("user", "repos")
        }
        bearerAuth(System.getenv("OAUTH_TOKEN"))
        setBody("""{"name":$repoName,"private":$isPrivate}""")
    }
}