package com.example.markovmusic.network

import com.example.markovmusic.model.GeminiNoteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Gemini REST API クライアント（共通実装）
 * Ktorを使用してGoogle AI Studio APIに直接アクセス
 */
class GeminiApiClient(private val apiKey: String) {

    init {
        println("[$TAG] === GeminiApiClient 初期化 ===")
        println("[$TAG] APIキー状態: ${when {
            apiKey.isEmpty() -> "未設定（空文字）"
            apiKey.isBlank() -> "未設定（空白のみ）"
            apiKey.length < 10 -> "不正な可能性あり（${apiKey.length}文字、短すぎます）"
            else -> "設定済み（${apiKey.take(4)}****、${apiKey.length}文字）"
        }}")
        println("[$TAG] 使用モデル順: ${MODELS.joinToString(" → ")}")
    }

    companion object {
        private const val TAG = "GeminiApiClient"
        private const val MAX_RETRIES = 3
        private const val RETRY_DELAY_MS = 2000L
        private const val FALLBACK_DELAY_MS = 2000L     // モデル切り替え間のディレイ
        private const val RATE_LIMIT_DELAY_MS = 5000L   // 429エラー時のディレイ
        private const val REQUEST_TIMEOUT_MS = 600_000L // 10分
        private const val CONNECT_TIMEOUT_MS = 30_000L  // 30秒
        private const val SOCKET_TIMEOUT_MS = 600_000L  // 10分
        private const val PRIMARY_MODEL = "gemini-2.5-flash"
        private const val FALLBACK_MODEL = "gemini-2.0-flash"
        private const val STABLE_MODEL = "gemini-1.5-flash"
        private val MODELS = listOf(PRIMARY_MODEL, FALLBACK_MODEL, STABLE_MODEL)

        /**
         * モデルに応じたAPIバージョンを返す
         * 新しいモデル（2.5系）は v1beta、それ以外は v1beta を使用
         */
        private fun apiVersionForModel(model: String): String {
            return "v1beta"  // 全モデルで v1beta を使用（v1 では一部モデルが 400 を返す）
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }
    }

    private val defaultSafetySettings = listOf(
        SafetySetting(category = "HARM_CATEGORY_HARASSMENT", threshold = "BLOCK_NONE"),
        SafetySetting(category = "HARM_CATEGORY_HATE_SPEECH", threshold = "BLOCK_NONE"),
        SafetySetting(category = "HARM_CATEGORY_SEXUALLY_EXPLICIT", threshold = "BLOCK_NONE"),
        SafetySetting(category = "HARM_CATEGORY_DANGEROUS_CONTENT", threshold = "BLOCK_NONE")
    )

    /**
     * エラーメッセージからAPIキーをマスクする
     */
    private fun maskApiKey(message: String): String {
        if (apiKey.isEmpty()) return message
        return message
            .replace(apiKey, "***")
            .replace(Regex("[?&]key=[^&\\s)\"']+"), "?key=***")
            .replace(Regex("AIza[A-Za-z0-9_-]{30,}"), "***")
    }

    /**
     * Gemini APIにリクエストを送信してメロディを生成
     * gemini-2.5-flash → gemini-2.0-flash → gemini-1.5-flash の順にフォールバック
     */
    suspend fun generateMelody(prompt: String): Result<List<GeminiNoteResponse>> {
        if (apiKey.isEmpty()) {
            return Result.failure(
                Exception(
                    "APIキーが設定されていません。local.propertiesにGEMINI_API_KEY=your_keyを設定し、" +
                        "プロジェクトをクリーン＆リビルド（./gradlew clean assembleDebug）してください。"
                )
            )
        }

        // デバッグ: プロンプト内容をログ出力
        println("[$TAG] === リクエスト送信 ===")
        println("[$TAG] APIキー: ${apiKey.take(4)}****（${apiKey.length}文字）")
        println("[$TAG] プロンプト長: ${prompt.length}文字")
        println("[$TAG] プロンプト先頭200文字: ${prompt.take(200)}")
        if (prompt.isBlank()) {
            println("[$TAG] 警告: プロンプトが空白です！")
            return Result.failure(Exception("プロンプトが空です。生成を中止しました。"))
        }

        val errors = mutableMapOf<String, String>()

        for ((index, model) in MODELS.withIndex()) {
            when (index) {
                0 -> println("[$TAG] プライマリモデル ($model) で生成開始")
                1 -> println("[$TAG] フォールバック ($model) で再試行")
                2 -> println("[$TAG] 最新モデルが利用できないため、安定版モデル ($model) を使用します")
            }

            val result = callGenerateContent(model, prompt)

            if (result.isSuccess) {
                if (index > 0) {
                    println("[$TAG] $model で生成成功（フォールバック使用）")
                } else {
                    println("[$TAG] $model で生成成功")
                }
                return result
            }

            val errorMsg = result.exceptionOrNull()?.message ?: "不明なエラー"
            errors[model] = maskApiKey(errorMsg)
            println("[$TAG] $model 失敗: ${maskApiKey(errorMsg)}")

            // 次のモデルへのフォールバック前にディレイ（429対策）
            if (index < MODELS.size - 1) {
                println("[$TAG] 次のモデルへ切り替え前に ${FALLBACK_DELAY_MS}ms 待機...")
                delay(FALLBACK_DELAY_MS)
            }
        }

        // すべて失敗
        val errorDetail = errors.entries.joinToString("\n") { "  [${it.key}] ${it.value}" }
        return Result.failure(
            Exception(
                "すべてのモデルで生成に失敗しました。\n$errorDetail\n" +
                    "APIキーを再確認してください。変更した場合は ./gradlew clean assembleDebug を実行してください。"
            )
        )
    }

    /**
     * 全モデル共通のリクエスト構築関数
     * BLOCK_NONE設定とgenerationConfigを統一的に適用
     */
    private fun buildRequest(prompt: String): GeminiRequest {
        return GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.9f
            ),
            safetySettings = defaultSafetySettings
        )
    }

    /**
     * 指定モデルでgenerateContent APIを呼び出す（リトライ付き）
     */
    private suspend fun callGenerateContent(
        model: String,
        prompt: String
    ): Result<List<GeminiNoteResponse>> {
        val apiVersion = apiVersionForModel(model)
        val url = "https://generativelanguage.googleapis.com/$apiVersion/models/$model:generateContent?key=$apiKey"

        val requestBody = buildRequest(prompt)

        // デバッグ: 送信直前のプロンプト空チェック
        val actualPromptText = requestBody.contents.firstOrNull()?.parts?.firstOrNull()?.text
        println("[$TAG] [$model] 送信直前プロンプト空チェック: ${if (actualPromptText.isNullOrBlank()) "空！" else "OK (${actualPromptText.length}文字)"}")

        // [Gemini Request Context] プロンプト全文をログ出力（デバッグ用）
        println("[Gemini Request Context] [$model] ===== プロンプト開始 =====")
        println("[Gemini Request Context] [$model] $prompt")
        println("[Gemini Request Context] [$model] ===== プロンプト終了 (${prompt.length}文字) =====")

        // デバッグ: safetySettings確認
        val safetyLog = requestBody.safetySettings?.joinToString(", ") { "${it.category}=${it.threshold}" } ?: "なし"
        println("[$TAG] [$model] safetySettings: $safetyLog")

        // デバッグ: リクエストBodyをログ出力（APIキーはBodyに含まれないため安全）
        val requestJson = json.encodeToString(GeminiRequest.serializer(), requestBody)
        println("[$TAG] [$model] リクエストBody (先頭500文字): ${requestJson.take(500)}...")

        var lastException: Exception? = null

        repeat(MAX_RETRIES) { attempt ->
            println("[$TAG] [$model] 試行 ${attempt + 1}/$MAX_RETRIES")
            try {
                val response: HttpResponse = client.post(url) {
                    contentType(ContentType.Application.Json)
                    headers {
                        append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    }
                    setBody(requestBody)
                }

                val rawBody = response.bodyAsText()
                val statusCode = response.status.value
                // ログ出力時はURLからAPIキーをマスク
                println("[$TAG] [$model] HTTPステータス: $statusCode")
                println("[$TAG] [$model] レスポンスBody (先頭500文字): ${maskApiKey(rawBody.take(500))}")

                // --- HTTPエラーハンドリング ---
                if (!response.status.isSuccess()) {
                    val errorDetail = parseErrorMessage(rawBody)
                    println("[$TAG] [$model] HTTPエラー $statusCode: $errorDetail")

                    when (statusCode) {
                        429 -> {
                            // レートリミット: リトライ可能
                            println("[$TAG] [$model] レートリミット（429）検出。${RATE_LIMIT_DELAY_MS}ms 待機後にリトライ...")
                            lastException = Exception("レートリミット（429 Too Many Requests）: $errorDetail")
                            if (attempt < MAX_RETRIES - 1) {
                                delay(RATE_LIMIT_DELAY_MS * (attempt + 1))
                            }
                            return@repeat // 次のリトライへ
                        }
                        400 -> {
                            // Bad Request: リトライしても無意味なのでこのモデルは即打ち切り
                            return Result.failure(
                                Exception("リクエストエラー (400): $errorDetail（モデル: $model）")
                            )
                        }
                        403 -> {
                            return Result.failure(
                                Exception("APIキーが無効または権限不足です (403)。Google AI Studioでキーを確認してください。")
                            )
                        }
                        404 -> {
                            return Result.failure(
                                Exception("モデル $model が見つかりません (404)。モデル名を確認してください。")
                            )
                        }
                        else -> {
                            return Result.failure(
                                Exception("サーバーエラー ($statusCode): $errorDetail（モデル: $model）")
                            )
                        }
                    }
                }

                // --- 正常レスポンスの処理 ---
                val geminiResponse = json.decodeFromString<GeminiResponse>(rawBody)

                // プロンプトレベルでのブロックチェック
                val blockReason = geminiResponse.promptFeedback?.blockReason
                if (blockReason != null) {
                    println("[$TAG] [$model] プロンプトブロック: $blockReason")
                    return Result.failure(
                        Exception("リクエストが安全フィルターによりブロックされました（理由: $blockReason）。プロンプトを調整してください。")
                    )
                }

                // candidatesの存在チェック
                val candidate = geminiResponse.candidates?.firstOrNull()
                if (candidate == null) {
                    println("[$TAG] [$model] candidatesが空: promptFeedback=${geminiResponse.promptFeedback}")
                    return Result.failure(Exception("Geminiからの応答が空です（candidatesなし）。モデル: $model"))
                }

                // finishReasonによるブロックチェック
                val finishReason = candidate.finishReason
                println("[$TAG] [$model] finishReason: $finishReason")
                if (finishReason == "SAFETY") {
                    val blockedCategories = candidate.safetyRatings
                        ?.filter { it.probability in listOf("HIGH", "MEDIUM") }
                        ?.mapNotNull { it.category }
                        ?.joinToString(", ")
                        ?: "不明"
                    println("[$TAG] [$model] SAFETYブロック: $blockedCategories")
                    return Result.failure(
                        Exception("応答が安全フィルターによりブロックされました（カテゴリ: $blockedCategories）。プロンプトを調整してください。")
                    )
                }

                val responseText = candidate.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

                if (responseText.isNullOrEmpty()) {
                    println("[$TAG] [$model] responseTextが空: finishReason=$finishReason")
                    return Result.failure(
                        Exception("Geminiからの応答が空です（finishReason: ${finishReason ?: "不明"}、モデル: $model）")
                    )
                }

                println("[$TAG] [$model] レスポンステキスト長: ${responseText.length}文字")
                val notes = json.decodeFromString<List<GeminiNoteResponse>>(responseText)
                println("[$TAG] [$model] パース成功: ${notes.size}音符")
                return Result.success(notes)

            } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
                println("[$TAG] [$model] リクエストタイムアウト (試行${attempt + 1})")
                lastException = e
                if (attempt < MAX_RETRIES - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                }
            } catch (e: io.ktor.client.network.sockets.SocketTimeoutException) {
                println("[$TAG] [$model] ソケットタイムアウト (試行${attempt + 1})")
                lastException = e
                if (attempt < MAX_RETRIES - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                }
            } catch (e: Exception) {
                val safeMsg = maskApiKey(e.message ?: "不明なエラー")
                println("[$TAG] [$model] エラー: ${e::class.simpleName}: $safeMsg")
                return Result.failure(Exception("API呼び出しエラー ($model): $safeMsg"))
            }
        }

        return Result.failure(
            Exception("サーバーが混雑しています。少し待ってから再試行してください（モデル: $model）")
        )
    }

    /**
     * APIエラーレスポンスのJSONからメッセージを抽出する（APIキーをマスク済みで返す）
     */
    private fun parseErrorMessage(rawBody: String): String {
        return try {
            val errorResponse = json.decodeFromString<GeminiErrorResponse>(rawBody)
            maskApiKey(errorResponse.error?.message ?: "詳細不明")
        } catch (_: Exception) {
            maskApiKey(rawBody.take(200))
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
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
data class SafetySetting(
    val category: String,
    val threshold: String
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
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null
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
    val blockReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
data class SafetyRating(
    val category: String? = null,
    val probability: String? = null
)

@Serializable
data class GeminiErrorResponse(
    val error: GeminiErrorDetail? = null
)

@Serializable
data class GeminiErrorDetail(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)
