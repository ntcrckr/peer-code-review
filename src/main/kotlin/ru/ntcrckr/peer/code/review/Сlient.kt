package ru.ntcrckr.peer.code.review

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client = HttpClient(CIO) {
    install(Logging.Companion) {
        logger = Logger.Companion.DEFAULT
        level = LogLevel.HEADERS
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
    install(ContentNegotiation.Plugin) {
        json(Json {
            prettyPrint = true
        })
    }
}