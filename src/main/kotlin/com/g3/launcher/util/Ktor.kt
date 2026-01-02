package com.g3.launcher.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

val httpClient: HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(json)
    }
}

val largeFileHttpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 30 * 60 * 1000L // 30 минут для запроса
        connectTimeoutMillis = 60 * 1000L // 1 минута на подключение
        socketTimeoutMillis = 30 * 60 * 1000L // 30 минут на сокет
    }

    engine {
        endpoint {
            connectTimeout = 60_000 // 60 секунд
            connectAttempts = 5 // 5 попыток подключения
            keepAliveTime = 30_000 // 30 секунд
            requestTimeout = 30 * 60 * 1000L // 30 минут
        }

        maxConnectionsCount = 1 // Ограничиваем одновременные подключения
    }
}
