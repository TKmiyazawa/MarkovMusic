# 🎵 Melody Math - 確率と音楽 -

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.9.1-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Gemini](https://img.shields.io/badge/Gemini%20AI-1.5%20Flash-8E75B2?logo=googlegemini&logoColor=white)](https://ai.google.dev/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green)](/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> *From 2005 Thesis to 2025 AI Tech*

<p align="center">
  <img src="docs/images/demo.png" width="300" alt="Melody Math Demo Screen">
</p>

---

## 📖 このプロジェクトについて

**Melody Math** は、「**確率**」と「**音楽**」の関係を体験できる教育向けアプリです。

### 🕰️ 20年の時を超えたリバイバル

2005年、開発者が大学生時代に卒業研究として作成した「**確率（マルコフ連鎖）を音楽で体感する**」プロジェクト。当時は **Excel VBA + Flash** という技術で実装されていました。

それから20年——。

2025年、**Kotlin Multiplatform** と **Jetpack Compose**、そして **Google Gemini AI** という現代の技術スタックで完全リバイバルしました。

### 🎯 目的

**中学3年生**が以下の3つの違いを「**聴覚**」と「**視覚**」で直感的に理解できるようにすること：

1. **ランダム** - 完全な無秩序
2. **確率モデル** - マルコフ連鎖による調和
3. **生成AI** - ニューラルネットワークによる知能

> 💡 数学と情報科学の面白さを、音楽という身近なテーマで伝えます。

---

## ✨ 3つの生成モード

| モード | 🎲 ランダム生成 | ✨ マルコフ連鎖 | 🤖 AI生成 (Gemini) |
|:---:|:---:|:---:|:---:|
| **アルゴリズム** | 一様乱数 | 確率行列 | ニューラルネットワーク |
| **結果** | カオス・不協和音 | 美しい調和 | 文脈を理解した作曲 |
| **特徴** | 完全ランダム | 2005年のロジックを再現 | 楽曲全体の構成を考慮 |
| **技術** | `kotlin.random.Random` | 重み付き遷移確率 | Gemini 1.5 Flash + JSON Mode |

### 🎲 Mode A: ランダム生成 (Chaos)

```
完全にランダムな音を生成。
音楽理論を無視した「カオス」がどう聴こえるかを体験。
→ 結論：ランダムだけでは音楽にならない！
```

### ✨ Mode B: マルコフ連鎖 (Probability)

```
パッヘルベルのカノン進行 [D → A → Bm → F#m → G → D → G → A] に基づき、
各コードに対する「次の音の確率」を定義した遷移行列を使用。
→ 確率の力で「それっぽい」メロディが生まれる！
```

### 🤖 Mode C: AI生成 (Intelligence)

```
Google Gemini 1.5 Flash を使用。
プロンプトで楽曲のコンテキスト（コード進行、音域、拍数）を伝え、
JSON Mode で構造化されたメロディデータを取得。
→ 「確率」を超えた「知能」による作曲を体験！
```

---

## 🛠️ 技術スタック

### Core
- **Kotlin Multiplatform (KMP)** - Android & iOS を単一コードベースでサポート
- **Kotlin 2.2.20** - 最新の言語機能を活用

### UI
- **Jetpack Compose Multiplatform 1.9.1** - 宣言的UIフレームワーク
- **Canvas API** - 五線譜・音符のカスタム描画

### AI / Network
- **Google Gemini 1.5 Flash** - 高速な生成AI
- **Ktor Client 3.0.3** - マルチプラットフォームHTTPクライアント
- **kotlinx.serialization** - JSON パース

### Audio
| Platform | Implementation |
|:---:|:---|
| **Android** | `AudioTrack` API による波形合成 |
| **iOS** | `AVFoundation` (Kotlin/Native interop) |

### Architecture
```
composeApp/
├── src/
│   ├── commonMain/          # 共通コード
│   │   ├── model/           # データモデル (Note, Pitch, Chord, Score)
│   │   ├── generator/       # 生成ロジック (Random, Markov, Gemini)
│   │   ├── network/         # API クライアント
│   │   ├── ui/              # Compose UI コンポーネント
│   │   └── audio/           # オーディオインターフェース (expect)
│   ├── androidMain/         # Android 固有実装
│   └── iosMain/             # iOS 固有実装
```

---

## 🚀 Getting Started

### 必要なもの

- Android Studio Ladybug 以降 (推奨)
- Xcode 15+ (iOS ビルド用)
- Google AI Studio アカウント (Gemini API Key 取得用)

### セットアップ手順

#### 1. リポジトリをクローン

```bash
git clone https://github.com/your-username/MarkovMusic.git
cd MarkovMusic
```

#### 2. Gemini API Key を取得

1. [Google AI Studio](https://aistudio.google.com/) にアクセス
2. API Key を作成

#### 3. API Key を設定

プロジェクトルートに `local.properties` を作成（または編集）:

```properties
# local.properties
GEMINI_API_KEY=your_api_key_here
```

> ⚠️ `local.properties` は `.gitignore` に含まれており、リポジトリにはコミットされません。

#### 4. iOS用設定ファイルを作成

```bash
# テンプレートからConfig.xcconfigを作成
cp iosApp/Configuration/Config.xcconfig.template iosApp/Configuration/Config.xcconfig

# local.propertiesからAPIキーを自動反映
./gradlew updateIosGeminiApiKey
```

#### 5. ビルド & 実行

**Android:**
```bash
./gradlew :composeApp:assembleDebug
```
または Android Studio から直接実行

**iOS:**
1. Xcode で `iosApp/iosApp.xcodeproj` を開く
2. シミュレータまたは実機で実行

> 💡 `Config.xcconfig` は `.gitignore` に含まれており、APIキーが漏洩しない設計になっています。

---

## 📚 教育的な使い方

### 授業での活用例

1. **導入**: 3つのモードを順番に試させ、音の違いを聴き比べる
2. **考察**: なぜマルコフ連鎖の方が「音楽っぽく」聴こえるのか議論
3. **発展**: AI生成との違いから「知能とは何か」を考える

### 学習ポイント

- **確率分布** と **条件付き確率** の直感的理解
- **マルコフ性** (現在の状態のみが次を決める) の体験
- **生成AI** の仕組みへの興味喚起

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- パッヘルベルのカノン - 300年以上愛され続ける名曲の進行を使用
- 2005年の卒業研究 - 20年前のアイデアを現代に蘇らせました
- Google Gemini - 生成AIの力を体験させてくれる素晴らしいAPI

---

<p align="center">
  <strong>🎵 確率の美しさを、音楽で体感しよう 🎵</strong>
</p>
