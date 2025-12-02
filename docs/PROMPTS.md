# Development Log & Prompt Engineering

このドキュメントは「Melody Math」プロジェクトの開発過程で使用したClaude Codeへのプロンプトを記録したものです。
プロンプトエンジニアリングの参考資料として、また開発履歴の記録として活用できます。

---

## 目次

- [Phase 1: 概念実証 (MVP)](#phase-1-概念実証-mvp)
  - [1-1. 初期プロジェクト構築](#1-1-初期プロジェクト構築)
  - [1-2. UI改善 - ビジュアル＆配色](#1-2-ui改善---ビジュアル配色)
  - [1-3. 楽曲拡張 - 30秒再生＆スクロール](#1-3-楽曲拡張---30秒再生スクロール)
  - [1-4. バグ修正 - 自動スクロール](#1-4-バグ修正---自動スクロール)
  - [1-5. 和音対応（ポリフォニー）](#1-5-和音対応ポリフォニー)
  - [1-6. UI簡略化 - メロディのみ表示](#1-6-ui簡略化---メロディのみ表示)
  - [1-7. レイアウト改善 - 操作パネル配置](#1-7-レイアウト改善---操作パネル配置)
  - [1-8. レイアウト改善 - 五線譜の中央配置](#1-8-レイアウト改善---五線譜の中央配置)
  - [1-9. タイトル追加](#1-9-タイトル追加)
  - [1-10. 操作パネル最適化](#1-10-操作パネル最適化)
  - [1-11. バグ修正 - 音符のY座標](#1-11-バグ修正---音符のy座標)
  - [1-12. AI解説機能（Vertex AI）](#1-12-ai解説機能vertex-ai)
  - [1-13. Gemini AI作曲モード](#1-13-gemini-ai作曲モード)
- [Phase 2: アーキテクチャ設計](#phase-2-アーキテクチャ設計)
  - [2-1. KMP化（iOS対応）](#2-1-kmp化ios対応)
  - [2-2. README作成](#2-2-readme作成)
  - [2-3. セキュリティ監査](#2-3-セキュリティ監査)
  - [2-4. APIキー設定](#2-4-apiキー設定)
  - [2-5. デモ画像設定](#2-5-デモ画像設定)

---

## Phase 1: 概念実証 (MVP)

**Goal**: Flash時代のアプリをJetpack Composeで再現する

---

### 1-1. 初期プロジェクト構築

> **目的**: 2005年の卒業研究アプリをKMPで再現

```markdown
# Role
あなたは熟練したAndroidエンジニアであり、Jetpack Compose Multiplatform (KMP) のエキスパートです。
以下の要件に基づき、教育用音楽生成アプリのAndroidプロジェクトを実装してください。

# Project Goal
2005年に作成された卒業研究アプリ「マルコフ連鎖で学ぶ音楽生成」を現代の技術でリバイバルします。
ターゲットは中学3年生。目的は「適当なランダム生成」と「マルコフ連鎖によるカノン進行」を聴き比べ、
確率モデルを通じた音楽の美しさと数学の面白さを体験させることです。

# Tech Stack
- Language: Kotlin (Standard Library only for logic. No PyTorch/TensorFlow).
- Framework: Jetpack Compose Multiplatform (Targeting Android initially).
- UI: Compose Canvas for drawing sheet music (Note: Replicating Adobe Flash style).
- Audio: Android `AudioTrack` or `SoundPool` for generative playback
  (Synthesize sine/triangle waves or simple PCM to avoid external assets).

# Core Features & Requirements

## 1. Domain Logic (Music Engine)
`MusicGenerator` クラスを作成し、以下の2つのモードを実装してください。

- **Mode A: ランダム生成**
  - 音階（ドレミ...）をランダムに選択して並べるだけの「カオスな曲」を生成。

- **Mode B: マルコフ連鎖生成 (カノン進行)**
  - パッヘルベルのカノン進行（D -> A -> Bm -> F#m -> G -> D -> G -> A）をベースにする。
  - 現在のコード（和音）という「状態」に基づき、次に鳴る音（メロディ）の確率分布を定義する。
  - 和音の構成音が選ばれやすい確率重み付けを行うことで、「調和のとれた美しい曲」にする。
  - 複雑なAIライブラリは使用せず、Kotlinの `Map` や `List` 、`Random` を使用して遷移確率行列を実装すること。

## 2. UI/UX (Sheet Music Visualization)
- 画面中央に五線譜（Staff）を描画する。
- 生成された音符（Note）を五線譜上に配置する。
- **再生アニメーション**:
  - 画面左端から右端へ、縦の棒（Playhead）が一定速度で移動するアニメーションを実装する。
  - Flashのアニメーションのように、Playheadが音符のX座標に触れた瞬間に音が鳴るようにする。

## 3. Implementation Steps
以下のステップでコードを生成・提案してください。

1. **Data Models**: `Note`, `Score`, `Chord` などのデータクラス定義。
2. **Logic**: `MarkovChainGenerator` の実装（カノン進行のロジックを含む）。
3. **Audio**: `ToneSynthesizer` の実装（周波数を指定して音を鳴らす機能）。
4. **UI**: `Compose Canvas` を使用した五線譜の描画と、`Animatable` を使用したシークバーの実装。
5. **Main Screen**: 2つのボタン（「ランダム生成」「マルコフ連鎖生成」）と再生エリアの統合。

# Constraint
- UIは教育用アプリらしく、シンプルかつ直感的にすること。
- 外部のアセットファイル（mp3など）を使わず、コードだけで完結する実装を目指すこと。
- エラーハンドリングよりも、まずはプロトタイプとして動くことを優先する。

これより実装を開始してください。まずはプロジェクト構造と主要なデータモデルから提案してください。
```

---

### 1-2. UI改善 - ビジュアル＆配色

> **目的**: ターゲット層（中学3年生）に合わせたポップなデザインへ変更

```markdown
# UI Refinement Request: Visuals & Aesthetics

前回の要件に加え、UIのデザインをターゲット層（中学3年生）に合わせて大幅に改善してください。
「古臭い教材ソフト」ではなく、「現代的なポップなアプリ」に見えるよう、以下の2点を変更してください。

## 1. Visual of Notes (音符の描画)
Canvas上の音符の描画ロジックを修正してください。
- 現在の「単純な円（Circle）」ではなく、**「音符の形（♩ または ♪）」**を描画してください。
- 外部画像を使わず、Compose Canvasの `drawPath` や `drawLine`, `drawOval` を組み合わせて、
  プログラマティックに「符頭（Head）」と「符幹（Stem）」を描いてください。
- 音の高さ（ピッチ）に応じて、符幹の向き（上向き/下向き）を適切に変えられるとベストです。

## 2. Color Scheme (配色テーマ)
アプリ全体の配色を「明るくポップ（Bright & Pop）」なテーマに変更してください。

- **背景色**: 真っ白ではなく、薄いクリーム色 (例: `Color(0xFFFFFDD0)`) やパステル調の明るい色。
- **五線譜**: 黒ではなく、濃いグレーまたはダークブルー。
- **音符の色**:
  - Mode A（ランダム）の音符: 少し落ち着いた色、またはバラバラな色。
  - Mode B（マルコフ連鎖）の音符: 鮮やかな「ビビッド・ピンク」や「ビビッド・オレンジ」など、強調される美しい色。
- **再生バー（Playhead）**: 目立つシアンやライムグリーンなどのネオンカラー。
- **ボタン**: マテリアルデザインの標準色ではなく、丸みを帯びた形状で、
  押したくなるようなキャンディカラー（角丸のShapeを大きめに）。

このデザイン変更を反映した `SheetMusicCanvas` コンポーザブルと、配色の定義コードを提示してください。
```

---

### 1-3. 楽曲拡張 - 30秒再生＆スクロール

> **目的**: 楽曲を30秒に拡張し、横スクロール対応

```markdown
# Logic & UI Update Request: 30s Duration & Scrolling

前回のコードをベースに、楽曲の規模拡大とスクロール機能を追加してください。

## 1. Duration Update (楽曲の長さを30秒に)
音楽生成ロジック (`MusicGenerator`) を修正し、生成される曲の長さを**約30秒**に固定してください。
- テンポ（BPM）を定義し、30秒分に必要な「小節数」または「音符の総数」を計算して生成ループを回してください。
- 例: BPM 120の場合、1拍0.5秒なので、30秒＝60拍分の音符データを生成する。

## 2. Horizontal Scrolling (五線譜の横スクロール)
30秒分の五線譜は画面幅に収まらないため、横スクロール可能なUIに変更してください。
- **Canvasの幅**: 固定幅（`fillMaxWidth`）ではなく、生成された音符の量に応じた「巨大な幅（Total Width）」を持つようにしてください。
- **スクロール実装**: `Modifier.horizontalScroll(rememberScrollState())` を使用して、
  ユーザーが手動で五線譜を左右にスワイプできるようにしてください。

## 3. Auto-Scrolling with Playhead (再生同期スクロール)
再生中、赤い棒（Playhead）が画面外に出てしまわないよう、アニメーションに合わせて**自動でスクロール**させてください。
- Playheadが画面の右側に近づいたら、自動的に右へスクロールし、
  常にPlayheadが表示領域内に収まるように `ScrollState.scrollTo()` を制御してください。
- Flashアプリの雰囲気を再現するため、「Playheadが楽譜の上を走る」感覚を維持しつつ、
  カメラ（スクロール位置）がそれを追従する挙動にしてください。

これらの変更を反映した `MusicScreen` および `SheetMusic` コンポーザブルのコードを提示してください。
```

---

### 1-4. バグ修正 - 自動スクロール

> **目的**: Playheadが画面外に消えるバグを修正

```markdown
# Bug Fix Request: Auto-Scroll Camera Follow

現状の実装では、再生中に縦棒（Playhead）だけが右へ進んでしまい、画面の外に消えてしまいます。
ユーザーが手動でスクロールしなくても、**「常に縦棒が画面内に表示され続ける（カメラが縦棒を追いかける）」**ように修正してください。

## Required Changes (修正要件)

### 1. Synchronize ScrollState with Animation
`LaunchedEffect` を使用して、再生アニメーションの値（縦棒のX座標）を監視し、
その値が変わるたびに `ScrollState` を更新するロジックを追加してください。

### 2. Centering Logic (中央追従ロジック)
縦棒が画面の端ギリギリになってからスクロールするのではなく、
**「縦棒が常に画面の中央付近に来るように」**スクロール位置を制御するのが理想です。

以下の計算ロジックを参考に実装してください：
- `targetScrollPosition = currentPlayheadX - (screenWidth / 2)`
- ※ただし、`targetScrollPosition` が 0未満になる場合や、
  最大スクロール幅を超える場合の境界値チェック（clamp）を含めてください。

### 3. Implementation Context
- `BoxWithConstraints` を使用して、現在の画面幅（`maxWidth`）を取得してください。
- `rememberScrollState()` で作成した state に対して、アニメーションのフレーム毎に `scrollTo` を呼び出してください。

## Expected Behavior
- 再生ボタンを押すと、縦棒が右へ進む。
- 縦棒が画面中央を超えたあたりから、背景の五線譜が左へ流れ始め、
  縦棒は画面中央に維持されているように見える（または、縦棒に合わせて画面がスムーズに右へスクロールしていく）。

この挙動を実現するための `MusicScreen` 内の `LaunchedEffect` 周りの修正コードを提示してください。
```

---

### 1-5. 和音対応（ポリフォニー）

> **目的**: 協和音と不協和音の対比を明確に

```markdown
# Logic & Audio Update Request: Implementing Polyphony (Chords)

中学生が聴いた瞬間に「こっちは美しい！」「こっちは変だ！」と明確に区別できるよう、
単音（Melody）だけでなく**和音（Chords/Harmony）**を実装してください。
協和音（キレイな響き）と不協和音（濁った響き）の対比を作ることで、マルコフ連鎖モデルの有効性を強調します。

## 1. Audio Engine Update (Polyphony Support)
現在の `ToneSynthesizer` は単音生成のみになっていると思われます。
これを**和音再生（多重音再生）**に対応させてください。
- 複数の周波数の正弦波（Sine Wave）を足し合わせて（Mixing）、1つの音声バッファとして生成するロジックに変更してください。
- 音割れを防ぐため、合成後の振幅が `Short.MAX_VALUE` を超えないよう正規化（Normalization）またはリミッター処理を行ってください。

## 2. Music Logic Update (Chord Progression)
`MusicGenerator` を拡張し、モードごとに和音の扱いを変えてください。

### Mode A: Random Generation (Chaos)
- **不協和音の生成**: ランダムに選ばれたルート音に対し、
  音楽理論を無視したランダムな音程で音を重ねてください（例: ド + ド# + ファ など）。
- 聴いた瞬間に「不気味」「適当」と感じられる、カオスな響きを目指してください。

### Mode B: Markov Chain (Canon Progression)
- **カノン進行の和音**: パッヘルベルのカノン（D Major）のコード進行
  （D -> A -> Bm -> F#m -> G -> D -> G -> A）に完全に従った和音を生成してください。
- **構成**:
  - **伴奏**: 各小節の頭で、その小節のコード（3和音）をジャン！と鳴らす、またはアルペジオにする。
  - **メロディ**: 既存のマルコフ連鎖ロジックで生成された単音メロディを重ねる。
- これにより、「音楽の授業で聴いたような美しい響き」を実現してください。

## 3. Visual Update (Stacked Notes)
五線譜（Canvas）上でも和音が視認できるようにしてください。
- 同じタイミング（X座標）に複数の音符がある場合、それらを縦に並べて描画してください。
- 和音の場合、棒（Stem）を連結して描画できるとベストですが、
  まずは単純に「おたまじゃくしが縦に並んでいる」状態でも構いません。

これらの変更を反映した `ToneSynthesizer`, `MusicGenerator`, および `SheetMusicCanvas` の修正コードを提示してください。
```

---

### 1-6. UI簡略化 - メロディのみ表示

> **目的**: 視覚的な情報過多を防ぎ、コード名を可視化

```markdown
# UI Update Request: Melody-Only Staff & Chord Labels

視覚的な情報過多を防ぎつつ、背後にある数学的モデル（コード進行）を意識させるため、
UIの表示ロジックを修正してください。

## 1. Simplify Sheet Music (五線譜はメロディのみ)
五線譜（Canvas）上の描画を**「単音（メロディ）」のみ**に戻してください。
- 音声合成（Audio）では引き続き和音を鳴らしますが、**画面上の五線譜には和音の構成音を描画しないでください。**
- これにより、生徒は「メロディの動き」に集中しやすくなります。

## 2. Display Chord Names (コード名の可視化)
五線譜のすぐ下に、現在鳴っている**コード名（Chord Name）**を表示するエリアを追加してください。
- **配置**: 五線譜の下部（Canvas内のY座標下の方、または五線譜の下の余白）。
- **スクロール**: 音符と一緒にコード名も左へ流れる（スクロールする）ように、Canvas上に直接テキストとして描画してください。
- **表示内容**:
  - **Mode B (マルコフ連鎖)**: カノン進行に合わせて「D」「A」「Bm」「F#m」「G」「D」「G」「A」を小節ごとに表示してください。
  - **Mode A (ランダム)**: コード理論がないため、「?」や「Random」、あるいは非表示にしてください。

## 3. Implementation Details
- `drawText` を使用するために、`nativeCanvas` (Android native Paint) を利用するか、
  Composeの `drawText` (もし利用可能なら) を使用して実装してください。
- データ構造 `Note` または `Measure` に `chordName: String?` プロパティを追加し、生成時にセットするようにしてください。

これにより、「画面上はシンプルなメロディラインだが、下を見るとコード進行（ルール）が表示されており、
耳ではその調和が聴こえる」という構成を実現してください。
```

---

### 1-7. レイアウト改善 - 操作パネル配置

> **目的**: 操作系を画面下部に集約

```markdown
# UI Layout Update Request: Control Panel at Bottom

画面レイアウトを変更し、操作系を画面下部に集約してください。
五線譜の表示領域を最大化し、指が届きやすい位置にボタンを配置します。

## 1. Main Layout Structure (全体構成)
`MusicScreen` のルートレイアウトを `Column` で構成し、以下の2つのセクションに分けてください。

1. **Top Section: Sheet Music Display (五線譜エリア)**
   - `Modifier.weight(1f)` を使用して、画面の上部（残り全ての領域）を占有するようにしてください。
   - ここにスクロール可能な `SheetMusicCanvas` を配置します。

2. **Bottom Section: Control Panel (操作パネル)**
   - 五線譜の下に固定配置される領域です。
   - 背景色を少し変えるか、影（Elevation）をつけて、五線譜エリアと区別してください。
   - 以下のボタンを `Row` を使って横並びに配置してください：
     - **[ランダム生成 (Mode A)]**
     - **[マルコフ連鎖生成 (Mode B)]**
     - **[再生 / 停止 (Play/Stop)]**

## 2. Button Styling & Arrangement
- ボタンは `Arrangement.SpaceEvenly` または `Center` で等間隔に配置してください。
- 前回の「ポップな配色」指定を引き継ぎ、大きく押しやすいボタン（丸みを帯びた形状）にしてください。

## 3. Important: Scroll Separation
- **重要**: 五線譜は横スクロールしますが、**下の操作ボタンはスクロールせず、常に画面下に固定**されている必要があります。

このレイアウト変更を反映した `MusicScreen` の修正コードを提示してください。
```

---

### 1-8. レイアウト改善 - 五線譜の中央配置

> **目的**: 五線譜を画面の上下中央に配置

```markdown
# UI Layout Refinement: Vertical Centering of Sheet Music

現在のレイアウトでは五線譜が画面の上部に寄ってしまっている可能性があります。
五線譜の表示領域（Canvas）を、操作ボタンより上の「空いているスペース」の**上下中央（Vertical Center）**に配置されるように修正してください。

## Required Changes

### 1. Layout Container Update
`MusicScreen` の上部セクション（`weight(1f)` を指定した部分）の構造を以下のように変更してください。

- **親コンテナ**: `Box` を使用し、`contentAlignment = Alignment.Center` を設定してください。
  これにより、中身が常に画面中央に配置されます。
- **背景色**: この `Box` 全体にアプリの背景色（クリーム色など）を適用し、画面全体が色付くようにしてください。
- **子要素 (五線譜)**: `Box` の中に、横スクロール可能な `Row` または `Canvas` を配置してください。

### 2. Canvas Height Restriction
- 五線譜の Canvas の高さ（`height`）を、画面いっぱいではなく、
  **五線譜が綺麗に収まる適切な固定の高さ**（例: `300.dp` 程度）に制限してください。
- これにより、「広い背景の中央に、300dp幅の五線譜の帯が横たわっている」ような、
  映画の字幕のような見やすい配置にしてください。

### Summary of Visual
- 上部：余白（背景色）
- 中央：**五線譜と音符が流れるエリア**
- 下部：余白（背景色）
- 最下部：操作パネル（固定）

このレイアウト変更を反映した `MusicScreen` の修正コードを提示してください。
```

---

### 1-9. タイトル追加

> **目的**: 五線譜の上にアプリタイトルを表示

```markdown
# UI Update Request: App Title Placement

画面の構成を微調整し、五線譜のすぐ上にアプリのタイトルを目立つように表示してください。

## 1. Add Title Text (タイトルの追加)
五線譜（Canvas）の上部に、以下のタイトルを表示してください。

- **Main Title**: "Melody Math"
- **Sub Title**: "〜 確率と音楽 〜"

## 2. Layout Adjustment (レイアウト調整)
前回の「画面中央に配置」という要件を維持しつつ、タイトルと五線譜を縦に並べます。

- 画面上部セクション（`Box`）の中身を、`Column` に変更してください。
- **Columnの配置**: `horizontalAlignment = Alignment.CenterHorizontally`,
  `verticalArrangement = Arrangement.Center` を設定し、
  タイトルと五線譜のセットが画面のど真ん中に来るようにしてください。
- **構成順序**:
  1. **タイトルテキスト**:
     - フォントサイズを大きく（例: `32.sp`）、太字（`FontWeight.Bold`）に。
     - 色は視認性の高いダークグレーやネイビー、またはポップなアクセントカラー。
     - サブタイトルは少し小さめに。
  2. **スペーサー**: 少し余白（`Spacer(height = 16.dp)`）を入れる。
  3. **五線譜エリア**: 横スクロールするCanvas（前回作成したもの）。
  4. **コード名表示エリア**: 五線譜の下のコード名。

## 3. Important
タイトルは五線譜と一緒に**スクロールしてはいけません**。
タイトルは常に画面中央上部に固定され、その下の五線譜だけが横に流れるように実装してください。

この変更を反映した `MusicScreen` の修正コードを提示してください。
```

---

### 1-10. 操作パネル最適化

> **目的**: ボタンの2段レイアウト化と再作成機能追加

```markdown
# UI & Feature Update: Control Panel Optimization

操作パネルのボタンが増え、テキストが潰れて読めなくなるのを防ぐため、
レイアウトを2段構成に変更し、新機能を追加してください。

## 1. Grid Layout for Buttons (2段レイアウト)
ボタンの視認性を確保するため、操作パネル（Bottom Section）を `Column` を使って縦2段に分けてください。

- **1段目 (生成モード選択)**:
  - 左: **[ランダム生成]** ボタン
  - 右: **[マルコフ生成]** ボタン
  - ※ `Row` 内で `Modifier.weight(1f)` を使用し、画面幅を2等分して配置してください。

- **2段目 (再生・操作)**:
  - 左: **[再生 / 停止]** ボタン
  - 右: **[再作成]** ボタン (新規追加)
  - ※ こちらも幅を2等分して配置してください。

## 2. Add "Re-create" Feature (再作成機能)
「再作成」ボタンのロジックを実装してください。
- **動作**: 最後に選択されたモード（Mode A または Mode B）を記憶しておき、
  そのモードでもう一度 `MusicGenerator` を実行して新しい曲を生成します。
- **初期状態**: アプリ起動時など、モードが未選択の場合は無効化（Disable）するか、
  デフォルトでMode Bを実行してください。

## 3. Text Visibility (文字表示の改善)
- 各ボタンのテキストがはみ出さないよう、`Text` コンポーザブルに以下を適用してください：
  - `textAlign = TextAlign.Center`
  - 必要であれば `maxLines = 1` と `overflow = TextOverflow.Ellipsis` を設定しつつ、
    文字サイズを少し調整（`fontSize = 14.sp` 程度）してください。
  - ボタン自体の高さ（`minHeight`）を十分に確保し、押しやすくしてください。

このレイアウト変更とロジック追加を反映した `MusicScreen` (特に操作パネル部分) の修正コードを提示してください。
```

---

### 1-11. バグ修正 - 音符のY座標

> **目的**: 音符が五線譜から浮いているバグを修正

```markdown
# Visual Bug Fix: Note Y-Positioning Alignment

現在の描画ロジックでは、音符（Notes）が五線譜（Staff lines）に対して上過ぎる位置に表示され、
浮いてしまっています。
音の高さ（Pitch）とY座標のマッピングを修正し、音符が正しく五線譜の線上、または線間に収まるようにしてください。

## 1. Adjust Pitch-to-Y Mapping (座標計算の修正)
`SheetMusicCanvas` 内の、音符のY座標を計算するロジックを以下のように調整してください。

- **基準点の再定義**:
  - 五線譜の「第3線（真ん中の線）」を、特定の音程（例: ト音記号なら B4 / シ）に対応させてください。
  - 音程が1つ上がるごとに、Y座標を「線と線の間隔の半分（half step）」だけ減らす（上に移動する）計算にしてください。

- **オフセット修正**:
  - 現在の計算式に `+ verticalOffset` を追加し、全体的に音符を下に移動させて、五線譜の範囲内に収めてください。

## 2. Visual Check
- **高い音**: 五線譜の上の方（または少しはみ出る）。
- **低い音**: 五線譜の下の方。
- **中心**: 生成されるメロディの平均的な高さが、五線譜の中央（第3線付近）に来るようにオフセットを調整してください。

これにより、「音符が五線譜の上空に浮遊している」状態を直し、正しく楽譜として読める位置関係に修正したコードを提示してください。
```

---

### 1-12. AI解説機能（Vertex AI）

> **目的**: Cloud Functions経由でVertex AIを活用した解説機能

```markdown
# Role
あなたはGoogle Cloud Professional Cloud ArchitectおよびAndroidのエキスパートです。

# Task
既存の「Melody Math」アプリに、Google Cloud Vertex AI (Gemini 1.5 Flash) を活用した
「AI解説機能」を追加するアーキテクチャとクライアントコードを提案してください。

# Architecture Requirement
Androidアプリから直接APIキーを使用せず、**Firebase Cloud Functions (2nd Gen)** を経由して
Vertex AIを呼び出すBFFパターンを採用します。

1. **Backend (Cloud Functions)**:
   - Language: TypeScript or Kotlin.
   - Trigger: `onCall` (Callable function).
   - Logic:
     - クライアントから「生成された音符のリスト（Noteの配列）」と「コード進行」を受け取る。
     - Vertex AI API (Gemini 1.5 Flash) を呼び出す。
     - Prompt: 「以下の音符列は[コード進行]に基づいて生成されました。
       中学生に向けて、このメロディの特徴や数学的な面白さを、先生のような口調で100文字以内で解説し、
       曲のタイトルを提案してください。」
     - 結果をJSONで返す `{ "comment": "...", "title": "..." }`。

2. **Frontend (Android/Kotlin)**:
   - `MusicScreen` に「AI解説を聞く（Ask AI Teacher）」ボタンを追加（生成完了後のみ有効）。
   - ボタン押下時、`FirebaseFunctions` SDKを使って上記関数を呼び出す。
   - 返ってきた解説とタイトルを、ダイアログまたは画面下部のカードでリッチに表示する。
   - 通信中はローディングインジケータ（`CircularProgressIndicator`）を表示する。

# Deliverables
- Cloud Functionsのコードスニペット（Vertex AI SDKの使用箇所）。
- Android側の `AITeacherRepository` (suspend functionでAPIを叩く) の実装。
- UIの変更点（解説表示エリアの追加）。

なお、Vertex AIの初期化やIAM設定についてはコード化不要ですが、
必要な権限（`roles/aiplatform.user` 等）についてはコメントで補足してください。
```

---

### 1-13. Gemini AI作曲モード

> **目的**: Mode C（AI作曲）の実装

```markdown
# Feature Request: Integration of Generative AI (Mode C)

対象ユーザー（3名限定）に対し、ニューラルネットワークの優位性を示すため、
既存の2つのモードに加え、**「Mode C: AI Composer (Gemini)」**を実装してください。
セキュリティ要件は低いため、Android標準のGoogle AI Client SDKを使用し、
APIキーは `local.properties` または `BuildConfig` から読み込む簡易実装で構いません。

## 1. Dependency & Setup
- `com.google.ai.client.generativeai` ライブラリを追加してください。
- `GeminiMelodyGenerator` クラスを作成し、`GenerativeModel` (Model: `gemini-1.5-flash`) のインスタンス化ロジックを実装してください。
- **重要**: レスポンスを確実にパースするため、`generationConfig` で `responseMimeType = "application/json"` を指定してください。

## 2. Prompt Engineering (Domain Logic)
Geminiに対して、以下の要件を満たすJSONを生成させるプロンプトを構築してください。

- **Role**: "あなたは天才的な作曲家です。"
- **Task**: "パッヘルベルのカノン（D Major）のコード進行 [D, A, Bm, F#m, G, D, G, A] に
  完璧にマッチする、美しく感動的なメロディを30秒分生成してください。"
- **Output Format**:
  JSON配列形式で出力すること。
  例: `[{"pitch": "D5", "duration": 0.5}, {"pitch": "C#5", "duration": 0.5}, ...]`
  - `pitch`: 音高（例: C4, D#5）。アプリの既存の音階定義に合わせること。
  - `duration`: 音価（1.0 = 4分音符, 0.5 = 8分音符など）。

## 3. UI/UX Updates
操作パネルを修正し、第3の選択肢を追加してください。

- **Button Layout**:
  - 1段目: [ランダム (Mode A)] [マルコフ (Mode B)]
  - 2段目: **[AI生成 - Gemini (Mode C)]** (新規追加)
  - 3段目: [再生/停止] [再作成]

- **Loading State**:
  - Geminiの生成には数秒かかるため、生成中は画面中央に `CircularProgressIndicator` を表示し、
    UI操作をブロックしてください。
  - 完了後、返ってきたJSONをパースして `Note` オブジェクトのリストに変換し、五線譜に描画してください。

## 4. Implementation Steps
1. `libs.versions.toml` (または build.gradle) への依存関係追加。
2. JSONパース用のデータクラス (`Serializable`) の定義。
3. `GeminiMelodyGenerator` の `generateMelody(): List<Note>` 実装。
4. `MusicScreen` への統合（ViewModelでの非同期呼び出し処理）。

この機能追加を行うためのコードを提示してください。
```

---

## Phase 2: アーキテクチャ設計

**Goal**: Android/iOS両対応のためのKMP化とVertex AI統合

---

### 2-1. KMP化（iOS対応）

> **目的**: Kotlin Multiplatform + Compose Multiplatformでのクロスプラットフォーム化

```markdown
# Project Migration: Support iOS (Kotlin Multiplatform)

既存のAndroidアプリ（Jetpack Compose）を拡張し、iPhone (iOS) でも動作するように
**Kotlin Multiplatform (KMP) + Compose Multiplatform** 化を行います。
ターゲットは3名のデモ用なので、厳密なリリース要件よりも「動くこと」を優先します。

## 1. Project Restructuring (KMP Setup)
プロジェクトをKMP構成に変更してください。
- **Source Sets**:
  - `commonMain`: 共通ロジック (`MusicGenerator`, `Note`, `MarkovChain`) と共通UI (`MusicScreen`, `SheetMusicCanvas`) を移動。
  - `androidMain`: Android固有の実装（既存のActivityなど）。
  - `iosMain`: iOS固有の実装。
- **Build Script**: `build.gradle.kts` に `cocoapods` または `packForXcode` タスク、
  およびiOSターゲット設定を追加してください。

## 2. Abstracting Platform Specifics (expect/actual)
以下の2つの機能を `expect` インターフェースとして定義し、iOS側の `actual` 実装を追加してください。

### A. Audio Engine (`ToneSynthesizer`)
Androidの `AudioTrack` はiOSで動きません。
- `commonMain`: `interface AudioPlayer { fun playNote(frequency: Double, duration: Double) }` を定義。
- `iosMain`: **AVFoundation (`AVAudioEngine` または `AVAudioPlayer`)** を使用して、
  指定された周波数の正弦波（Sine Wave）を生成・再生する実装を行ってください。
  - ※複雑なPCM合成が難しければ、単純なビープ音やシステムサウンドで代用しても構いませんが、
    可能ならCoreAudio/AVAudioEngineでの実装を試みてください。

### B. Gemini API Access (Network Layer)
Android専用のSDK (`google-ai-client`) はiOSネイティブでは動作しない可能性があります。
- 共通ロジック (`commonMain`) で **Ktor Client** を使用して、
  Google AI Studio (Gemini API) のRESTエンドポイントを直接叩く実装に変更してください。
- これにより、OSを問わず `Mode C` が動作するようにします。

## 3. UI Compatibility Checks
- **Canvas Text**: Androidの `nativeCanvas.drawText` はiOSで動きません。
  Compose Multiplatformの `drawText` (ui-graphics) を使用するか、
  プラットフォームごとの描画処理に分岐させてください。
- **Resources**: フォントや画像などのリソース管理を `compose.resources` (KMP) に準拠させてください。

## 4. Entry Point (iOS App)
- iOSアプリのエントリーポイントとなる `MainViewController` (ComposeUIViewController) を `iosMain` に作成してください。
- Xcodeプロジェクト側から呼び出すための最低限のSwiftコード（`ContentView.swift`）も提示してください。

## Goals
- Android Studio上で `iosApp` の実行構成を作成し、iOS Simulatorでアプリが起動すること。
- Androidと同じく、Mode A/B/C 全てがiPhone上で動作し、音が鳴ること。

この移行作業に必要な主要なコード変更と、Ktorを使ったGemini APIクライアントの実装を提示してください。
```

---

### 2-2. README作成

> **目的**: GitHub公開用のREADME.md作成

```markdown
# Documentation Request: Generate GitHub README.md

このプロジェクトのGitHubリポジトリ用に、日本人の開発者や学習者が見てすぐに理解でき、
かつ技術的な興味を惹く `README.md` を作成してください。

## 1. Project Overview & Story
タイトル: **Melody Math - 確率と音楽 -**
サブタイトル: *From 2005 Thesis to 2025 AI Tech*

**背景ストーリー**:
- 2005年、開発者が大学生時代に「確率（マルコフ連鎖）を音楽で体感する」ために作成した
  卒業研究（Excel VBA + Flash）を、20年の時を経て現代の技術で完全リバイバル。
- **目的**: 中学3年生に「ランダム」「確率モデル」「生成AI（ニューラルネットワーク）」の違いを
  聴覚と視覚で体験させ、数学と情報の面白さを伝えること。

## 2. Features (The 3 Modes)
以下の3つのモードを表形式などで分かりやすく比較してください。
1. **Mode A: Random (Chaos)**
   - 単なるランダム生成。不協和音。カオス。
2. **Mode B: Markov Chain (Probability)**
   - 2005年のロジックをKotlinで再現。
   - パッヘルベルのカノン進行に基づき、確率行列を用いて「美しい調和」を生成。
3. **Mode C: Gemini AI (Intelligence)**
   - Google Gemini 1.5 Flashを使用。
   - コンテキスト（楽曲全体の構成）を理解し、JSONモードで構造化されたメロディを生成。
   - 「確率」を超えた「知能」による作曲。

## 3. Tech Stack
技術的な見どころを箇条書きで記載してください。
- **Core**: Kotlin Multiplatform (KMP) - Android & iOS fully supported.
- **UI**: Jetpack Compose Multiplatform (Canvas drawing for Sheet Music).
- **AI**: Google AI Client SDK (Gemini 1.5 Flash).
- **Audio**:
  - Android: `AudioTrack`
  - iOS: `AVFoundation` (via Kotlin Native interop)
- **Architecture**:
  - Clean Architecture styling.
  - JSON Mode for strict structured output from LLM.

## 4. Getting Started (Setup)
開発者がクローンしてすぐに動かせるよう、手順を書いてください。
1. Clone repository.
2. Get API Key from Google AI Studio.
3. Set API Key in `local.properties` (e.g., `GEMINI_API_KEY=...`).
4. Run on Android Emulator or iOS Simulator.

## 5. Style & Tone
- 言語: **日本語**
- 雰囲気: アカデミックだが堅苦しくない、教育×テックのワクワク感。
- 装飾:
  - 冒頭にプロジェクトのロゴやスクショを貼るためのプレースホルダー `![Demo Image](path/to/image.png)` を配置。
  - バッジ（Kotlin, Compose, Gemini, License MITなど）を使用。
  - 絵文字（🎵, 🎲, 🤖）を適度に使用して視認性を高める。

この要件に基づき、マークダウン形式で `README.md` の全文を出力してください。
```

---

### 2-3. セキュリティ監査

> **目的**: GitHub公開前のセキュリティチェック

```markdown
# Security Audit Request: Pre-Publication Check

このプロジェクトをGitHubでPublic公開する準備をしています。
コードベースをスキャンし、セキュリティリスクとなる情報が含まれていないか監査し、修正案を提示してください。

## 1. API Key & Secrets Check
コード内（特に `MusicGenerator`, `GeminiMelodyGenerator`, `App` クラスなど）に、
APIキーなどの機密情報がハードコーディング（直書き）されていないか確認してください。
- **リスク**: "AIza" で始まる文字列や、ハードコードされた認証トークン。
- **修正案**: もし見つかった場合、それを `local.properties` に移動し、
  `BuildConfig` 経由で参照するようにコードを書き換える手順を示してください。

## 2. .gitignore Validation
現在の `.gitignore` ファイルを確認（または新規作成）し、以下のファイルが確実に除外設定されているかチェックしてください。
- `local.properties` (Android/KMP secrets)
- `*.jks`, `*.keystore` (Signing keys)
- `build/`, `.gradle/`, `.idea/` (Build & IDE files)
- `iosApp/iosApp.xcodeproj/project.xcworkspace/` (User data)
- `*.DS_Store`

## 3. Implementation of Secure API Key Access
もし現状がハードコーディングになっている場合、以下の実装に変更するためのコードを提示してください。
1. **local.properties**: `GEMINI_API_KEY=your_key_here` を追記する指示。
2. **build.gradle.kts**: `buildConfigField` を使って、Gradleが `local.properties` からキーを読み込み、
   Android/Commonコードに定数として生成する設定。
3. **Kotlin Code**: 生成された `BuildConfig.GEMINI_API_KEY` をコード内で使用する修正。

この監査を行い、安全に公開できる状態にするための修正ステップを教えてください。
```

---

### 2-4. APIキー設定

> **目的**: APIキーを安全にプロジェクトに設定

```markdown
# Configuration Task: Secure API Key Setup for KMP

新しいAPIキーを作成しました。これをソースコードに直書きせず、
`local.properties` から読み込んで安全に使用できるようにプロジェクトを設定してください。

## 1. Local Properties Setup
- プロジェクトルートの `local.properties` ファイルから `GEMINI_API_KEY` というプロパティを
  読み込むロジックを `composeApp/build.gradle.kts` (または `app/build.gradle.kts`) に追加してください。
- **注意**: `local.properties` が存在しない場合のフォールバック（空文字など）も考慮してください。

## 2. BuildConfig Generation (KMP / Android)
- **Android側**:
  - `buildConfigField` を使用して、読み込んだキーを `BuildConfig.GEMINI_API_KEY` として生成してください。
  - `buildFeatures { buildConfig = true }` が有効になっているか確認してください。

## 3. Passing Key to Shared Logic (CommonMain)
- KMPの `commonMain` は直接 Androidの `BuildConfig` を参照できない場合があります。
- 以下のいずれかの方法で、共通コードがAPIキーを受け取れるように修正してください：
  - **方法A (推奨)**: `GeminiMelodyGenerator` クラスのコンストラクタで `apiKey: String` を受け取るようにし、
    Android側の呼び出し元（MainActivity等）で `BuildConfig.GEMINI_API_KEY` を渡す。
  - **方法B**: `expect object AppConfig { val apiKey: String }` を定義し、platform側で実装する。

## 4. Verification
- `.gitignore` に `local.properties` が含まれていることを再確認する手順を含めてください。

この設定を行うための Gradle ファイルと Kotlin コードの修正案を提示してください。
```

---

### 2-5. デモ画像設定

> **目的**: README.mdにデモスクリーンショットを表示

```markdown
# Documentation Update: Setup Demo Screenshot

GitHubのREADME.mdに、アプリのデモ画面（スクリーンショット）を表示させるための準備を行ってください。

## 1. Create Directory
- プロジェクトルートに、画像を格納するためのディレクトリ `docs/images/` を作成してください。

## 2. Update README.md
- `README.md` ファイル内の画像リンク部分（プレースホルダー）を検索し、以下のHTMLタグに置き換えてください。
- Markdown記法ではなくHTMLタグを使用することで、画像が巨大になりすぎないようサイズ調整を行います。

```html
<p align="center">
  <img src="docs/images/demo.png" width="300" alt="Melody Math Demo Screen">
</p>
```

---

## 補足: この後の手順

Claude Codeが処理を終えたら、以下の3ステップで画像が表示されるようになります。

1. Android Studioのエミュレータや実機でアプリを動かし、**スクリーンショットを撮影**します。
2. その画像ファイルの名前を **`demo.png`** に変更します。
3. プロジェクト内の **`docs/images/`** フォルダの中に、その画像を入れます。

その後、以下のコマンドでGithubに反映させてください。

```bash
git add docs/images/demo.png README.md
git commit -m "Add demo screenshot"
git push origin main
```
