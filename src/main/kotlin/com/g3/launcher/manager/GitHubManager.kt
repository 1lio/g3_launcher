package com.g3.launcher.manager

import com.g3.launcher.Constants
import com.g3.launcher.util.httpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object GitHubManager {

    @Serializable
    data class GitHubReleaseResponse(
        @SerialName("tag_name")
        val tagName: String = "",
        val name: String = "",
        @SerialName("html_url")
        val htmlUrl: String = "",
        val body: String? = null,
        @SerialName("published_at")
        val publishedAt: String = "",
        val draft: Boolean = false,
        val prerelease: Boolean = false,
        val assets: List<ReleaseAsset> = emptyList()
    )

    @Serializable
    data class ReleaseAsset(
        val name: String = "",
        @SerialName("browser_download_url")
        val browserDownloadUrl: String = "",
        val size: Long = 0
    )

    private const val BASE_URL = "https://api.github.com/repos/1lio/g3_launcher/releases/latest"

    suspend fun isNewVersionAvailable(): Boolean {
        return try {
            val response = httpClient.get(BASE_URL) {
                headers {
                    append(HttpHeaders.Accept, "application/vnd.github.v3+json")
                }
                timeout {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                }
            }

            val release = response.body<GitHubReleaseResponse>()
            val latestVersion = release.tagName.removePrefix("v")

            val result = compareVersions(latestVersion) < 0
            result
        } catch (e: Exception) {
            println("Ошибка при проверке обновлений: ${e.message}")
            false // При ошибке считаем, что обновлений нет
        }
    }

    private fun compareVersions(latestVersion: String): Int {
        val currentParts = Constants.LAUNCHER_VERSION.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latestVersion.split(".").map { it.toIntOrNull() ?: 0 }

        for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
            val current = currentParts.getOrElse(i) { 0 }
            val latest = latestParts.getOrElse(i) { 0 }

            val compare = current.compareTo(latest)
            if (compare != 0) {
                return compare
            }
        }

        return 0
    }

    suspend fun getLatestReleaseInfo(): GitHubReleaseResponse? {
        return try {
            val response = httpClient.get(BASE_URL) {
                headers {
                    append(HttpHeaders.Accept, "application/vnd.github.v3+json")
                }
            }
            response.body()
        } catch (e: Exception) {
            println("Ошибка при получении информации о релизе: ${e.message}")
            null
        }
    }

    suspend fun getAssetDownloadUrl(fileName: String): String? {
        return try {
            val release = getLatestReleaseInfo()
            release?.assets?.find { it.name == fileName }?.browserDownloadUrl
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAvailableAssets(): List<String> {
        return try {
            val release = getLatestReleaseInfo()
            release?.assets?.map { it.name } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun close() {
        httpClient.close()
    }
}
