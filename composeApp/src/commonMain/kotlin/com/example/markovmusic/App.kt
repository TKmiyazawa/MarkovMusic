package com.example.markovmusic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.example.markovmusic.audio.ToneSynthesizer
import com.example.markovmusic.generator.GeminiMelodyGenerator
import com.example.markovmusic.generator.MarkovChainGenerator
import com.example.markovmusic.generator.RandomGenerator
import com.example.markovmusic.model.Score
import com.example.markovmusic.ui.StaffNotation
import com.example.markovmusic.ui.rememberPlaybackState
import com.example.markovmusic.ui.theme.AppColors
import com.example.markovmusic.ui.components.PopButton
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    App(geminiApiKey = getGeminiApiKey())
}

/**
 * Gemini APIキーを取得するプラットフォーム固有の関数
 */
expect fun getGeminiApiKey(): String

@Composable
fun App(geminiApiKey: String) {
    MaterialTheme {
        var currentScore by remember { mutableStateOf<Score?>(null) }
        var isPlaying by remember { mutableStateOf(false) }
        var generationMode by remember { mutableStateOf<String?>(null) }
        var lastSelectedMode by remember { mutableStateOf<String?>(null) } // 最後に選択されたモード
        var isGenerating by remember { mutableStateOf(false) } // Gemini生成中のローディング状態
        var errorMessage by remember { mutableStateOf<String?>(null) } // エラーメッセージ
        val synthesizer = remember { ToneSynthesizer() }
        val coroutineScope = rememberCoroutineScope()

        // Geminiジェネレータのインスタンス
        val geminiGenerator = remember { GeminiMelodyGenerator(geminiApiKey) }

        // エラーメッセージからAPIキーをマスクするユーティリティ
        fun sanitizeError(message: String?): String {
            if (message == null) return "不明なエラー"
            var safe = message
            if (geminiApiKey.isNotEmpty()) {
                safe = safe.replace(geminiApiKey, "***")
            }
            return safe
                .replace(Regex("[?&]key=[^&\\s)\"']+"), "?key=***")
                .replace(Regex("AIza[A-Za-z0-9_-]{30,}"), "***")
        }

        LaunchedEffect(Unit) {
            synthesizer.initialize()
        }

        DisposableEffect(Unit) {
            onDispose {
                synthesizer.release()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
        ) {
            // Top Section: Sheet Music Display（五線譜エリア）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 残り全領域を占有
                    .background(AppColors.Background), // 背景色を適用
                contentAlignment = Alignment.Center // 中央配置
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // アプリタイトル
                    Text(
                        text = "Melody Math",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.ButtonColors.Markov
                    )
                    Text(
                        text = "〜 確率と音楽 〜",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 五線譜または案内メッセージ
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        currentScore?.let { score ->
                            val playbackState = rememberPlaybackState(
                                score = score,
                                synthesizer = synthesizer,
                                isPlaying = isPlaying
                            )

                            // 五線譜を固定の高さで表示
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp), // 固定の高さ
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AppColors.CardBackground
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                StaffNotation(
                                    score = score,
                                    playheadPosition = playbackState.playheadPosition,
                                    generationMode = generationMode,
                                    isPlaying = isPlaying,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } ?: run {
                            // スコアが未生成の場合は案内を表示
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "🎼",
                                    fontSize = 64.sp
                                )
                                Text(
                                    text = "下のボタンから生成モードを選んでください",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextSecondary
                                )
                            }
                        }

                        // ローディングオーバーレイ
                        if (isGenerating) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .background(AppColors.Background.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = AppColors.ButtonColors.Gemini
                                    )
                                    Text(
                                        text = "AIがメロディを作曲中...",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // エラーメッセージ表示
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            fontSize = 14.sp,
                            color = AppColors.ButtonColors.Stop,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Bottom Section: Control Panel（操作パネル）
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AppColors.CardBackground,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 現在のモード表示
                    if (generationMode != null) {
                        Text(
                            text = "現在のモード: $generationMode",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    // 1段目: 生成モード選択（ランダム・マルコフ）
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ランダム生成ボタン
                        PopButton(
                            text = "🎲 ランダム生成",
                            onClick = {
                                errorMessage = null
                                val generator = RandomGenerator()
                                currentScore = generator.generate()
                                generationMode = "ランダム"
                                lastSelectedMode = "ランダム"
                                isPlaying = false
                            },
                            backgroundColor = if (generationMode == "ランダム")
                                AppColors.ButtonColors.Random
                            else AppColors.ButtonColors.Random.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f),
                            enabled = !isGenerating
                        )

                        // マルコフ連鎖生成ボタン
                        PopButton(
                            text = "✨ マルコフ生成",
                            onClick = {
                                errorMessage = null
                                val generator = MarkovChainGenerator()
                                currentScore = generator.generate()
                                generationMode = "マルコフ連鎖"
                                lastSelectedMode = "マルコフ連鎖"
                                isPlaying = false
                            },
                            backgroundColor = if (generationMode == "マルコフ連鎖")
                                AppColors.ButtonColors.Markov
                            else AppColors.ButtonColors.Markov.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f),
                            enabled = !isGenerating
                        )
                    }

                    // 2段目: AI生成ボタン（Gemini）
                    PopButton(
                        text = "🤖 AI生成 - Gemini",
                        onClick = {
                            errorMessage = null
                            isGenerating = true
                            isPlaying = false
                            coroutineScope.launch {
                                val result = geminiGenerator.generateMelody()
                                result.onSuccess { score ->
                                    currentScore = score
                                    generationMode = "AI生成 (Gemini)"
                                    lastSelectedMode = "AI生成 (Gemini)"
                                }.onFailure { error ->
                                    errorMessage = "生成に失敗しました: ${sanitizeError(error.message)}"
                                }
                                isGenerating = false
                            }
                        },
                        backgroundColor = if (generationMode == "AI生成 (Gemini)")
                            AppColors.ButtonColors.Gemini
                        else AppColors.ButtonColors.Gemini.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isGenerating && geminiApiKey.isNotEmpty()
                    )

                    // 3段目: 再生・操作
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 再生/停止ボタン
                        PopButton(
                            text = if (isPlaying) "⏸ 停止" else "▶ 再生",
                            onClick = { isPlaying = !isPlaying },
                            backgroundColor = if (isPlaying)
                                AppColors.ButtonColors.Stop
                            else AppColors.ButtonColors.Play,
                            modifier = Modifier.weight(1f),
                            enabled = currentScore != null && !isGenerating
                        )

                        // 再作成ボタン
                        PopButton(
                            text = "🔄 再作成",
                            onClick = {
                                errorMessage = null
                                lastSelectedMode?.let { mode ->
                                    when (mode) {
                                        "ランダム" -> {
                                            val generator = RandomGenerator()
                                            currentScore = generator.generate()
                                            generationMode = mode
                                            isPlaying = false
                                        }
                                        "マルコフ連鎖" -> {
                                            val generator = MarkovChainGenerator()
                                            currentScore = generator.generate()
                                            generationMode = mode
                                            isPlaying = false
                                        }
                                        "AI生成 (Gemini)" -> {
                                            isGenerating = true
                                            isPlaying = false
                                            coroutineScope.launch {
                                                val result = geminiGenerator.generateMelody()
                                                result.onSuccess { score ->
                                                    currentScore = score
                                                    generationMode = mode
                                                }.onFailure { error ->
                                                    errorMessage = "生成に失敗しました: ${sanitizeError(error.message)}"
                                                }
                                                isGenerating = false
                                            }
                                        }
                                    }
                                }
                            },
                            backgroundColor = AppColors.ButtonColors.Secondary,
                            modifier = Modifier.weight(1f),
                            enabled = lastSelectedMode != null && !isGenerating
                        )
                    }
                }
            }
        }
    }
}