package com.g3.launcher.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

val httpClient: HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
}
