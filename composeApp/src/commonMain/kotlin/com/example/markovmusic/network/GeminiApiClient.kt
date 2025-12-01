package com.example.markovmusic.network

import com.example.markovmusic.model.GeminiNoteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Gemini REST API クライアント（共通実装）
 * Ktorを使用してGoogle AI Studio APIに直接アクセス
 */
class GeminiApiClient(private val apiKey: String) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
    }

    /**
     * Gemini APIにリクエストを送信してメロディを生成
     */
    suspend fun generateMelody(prompt: String): Result<List<GeminiNoteResponse>> {
        return try {
            if (apiKey.isEmpty()) {
                return Result.failure(Exception("APIキーが設定されていません"))
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"

            val requestBody = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(Part(text = prompt))
                    )
                ),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.9f
                )
            )

            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
                setBody(requestBody)
            }

            val geminiResponse = response.body<GeminiResponse>()

            // レスポンスからテキストを抽出
            val responseText = geminiResponse.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

            if (responseText.isNullOrEmpty()) {
                return Result.failure(Exception("Geminiからの応答が空です"))
            }

            // JSONをパースしてノートのリストを返す
            val notes = json.decodeFromString<List<GeminiNoteResponse>>(responseText)
            Result.success(notes)

        } catch (e: Exception) {
            Result.failure(Exception("API呼び出しエラー: ${e.message}"))
        }
    }

    fun close() {
        client.close()
    }
}

// Gemini API リクエスト/レスポンスのデータクラス

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val maxOutputTokens: Int? = null
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
)

@Serializable
data class Candidate(
    val content: ContentResponse? = null,
    val finishReason: String? = null
)

@Serializable
data class ContentResponse(
    val parts: List<PartResponse>? = null,
    val role: String? = null
)

@Serializable
data class PartResponse(
    val text: String? = null
)

@Serializable
data class PromptFeedback(
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
data class SafetyRating(
    val category: String? = null,
    val probability: String? = null
)
