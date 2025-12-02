# Development Log & Prompt Engineering

## Phase 1: 概念実証 (MVP)
**Goal**: Flash時代のアプリをJetpack Composeで再現する。
**Prompt**: # Role
あなたは熟練したAndroidエンジニアであり、Jetpack Compose Multiplatform (KMP) のエキスパートです。
以下の要件に基づき、教育用音楽生成アプリのAndroidプロジェクトを実装してください。

# Project Goal
2005年に作成された卒業研究アプリ「マルコフ連鎖で学ぶ音楽生成」を現代の技術でリバイバルします。
ターゲットは中学3年生。目的は「適当なランダム生成」と「マルコフ連鎖によるカノン進行」を聴き比べ、確率モデルを通じた音楽の美しさと数学の面白さを体験させることです。

# Tech Stack
- Language: Kotlin (Standard Library only for logic. No PyTorch/TensorFlow).
- Framework: Jetpack Compose Multiplatform (Targeting Android initially).
- UI: Compose Canvas for drawing sheet music (Note: Replicating Adobe Flash style).
- Audio: Android `AudioTrack` or `SoundPool` for generative playback (Synthesize sine/triangle waves or simple PCM to avoid external assets).

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

**Prompt**:# UI Refinement Request: Visuals & Aesthetics

前回の要件に加え、UIのデザインをターゲット層（中学3年生）に合わせて大幅に改善してください。
「古臭い教材ソフト」ではなく、「現代的なポップなアプリ」に見えるよう、以下の2点を変更してください。

## 1. Visual of Notes (音符の描画)
Canvas上の音符の描画ロジックを修正してください。
- 現在の「単純な円（Circle）」ではなく、**「音符の形（♩ または ♪）」**を描画してください。
- 外部画像を使わず、Compose Canvasの `drawPath` や `drawLine`, `drawOval` を組み合わせて、プログラマティックに「符頭（Head）」と「符幹（Stem）」を描いてください。
- 音の高さ（ピッチ）に応じて、符幹の向き（上向き/下向き）を適切に変えられるとベストです。

## 2. Color Scheme (配色テーマ)
アプリ全体の配色を「明るくポップ（Bright & Pop）」なテーマに変更してください。
- **背景色**: 真っ白ではなく、薄いクリーム色 (例: `Color(0xFFFFFDD0)`) やパステル調の明るい色。
- **五線譜**: 黒ではなく、濃いグレーまたはダークブルー。
- **音符の色**:
  - Mode A（ランダム）の音符: 少し落ち着いた色、またはバラバラな色。
  - Mode B（マルコフ連鎖）の音符: 鮮やかな「ビビッド・ピンク」や「ビビッド・オレンジ」など、強調される美しい色。
- **再生バー（Playhead）**: 目立つシアンやライムグリーンなどのネオンカラー。
- **ボタン**: マテリアルデザインの標準色ではなく、丸みを帯びた形状で、押したくなるようなキャンディカラー（角丸のShapeを大きめに）。

このデザイン変更を反映した `SheetMusicCanvas` コンポーザブルと、配色の定義コードを提示してください。

**Prompt**:# Logic & UI Update Request: 30s Duration & Scrolling

前回のコードをベースに、楽曲の規模拡大とスクロール機能を追加してください。

## 1. Duration Update (楽曲の長さを30秒に)
音楽生成ロジック (`MusicGenerator`) を修正し、生成される曲の長さを**約30秒**に固定してください。
- テンポ（BPM）を定義し、30秒分に必要な「小節数」または「音符の総数」を計算して生成ループを回してください。
- 例: BPM 120の場合、1拍0.5秒なので、30秒＝60拍分の音符データを生成する。

## 2. Horizontal Scrolling (五線譜の横スクロール)
30秒分の五線譜は画面幅に収まらないため、横スクロール可能なUIに変更してください。
- **Canvasの幅**: 固定幅（`fillMaxWidth`）ではなく、生成された音符の量に応じた「巨大な幅（Total Width）」を持つようにしてください。
- **スクロール実装**: `Modifier.horizontalScroll(rememberScrollState())` を使用して、ユーザーが手動で五線譜を左右にスワイプできるようにしてください。

## 3. Auto-Scrolling with Playhead (再生同期スクロール)
再生中、赤い棒（Playhead）が画面外に出てしまわないよう、アニメーションに合わせて**自動でスクロール**させてください。
- Playheadが画面の右側に近づいたら、自動的に右へスクロールし、常にPlayheadが表示領域内に収まるように `ScrollState.scrollTo()` を制御してください。
- Flashアプリの雰囲気を再現するため、「Playheadが楽譜の上を走る」感覚を維持しつつ、カメラ（スクロール位置）がそれを追従する挙動にしてください。

これらの変更を反映した `MusicScreen` および `SheetMusic` コンポーザブルのコードを提示してください。

**Prompt**:# Bug Fix Request: Auto-Scroll Camera Follow

現状の実装では、再生中に縦棒（Playhead）だけが右へ進んでしまい、画面の外に消えてしまいます。
ユーザーが手動でスクロールしなくても、**「常に縦棒が画面内に表示され続ける（カメラが縦棒を追いかける）」**ように修正してください。

## Required Changes (修正要件)

### 1. Synchronize ScrollState with Animation
`LaunchedEffect` を使用して、再生アニメーションの値（縦棒のX座標）を監視し、その値が変わるたびに `ScrollState` を更新するロジックを追加してください。

### 2. Centering Logic (中央追従ロジック)
縦棒が画面の端ギリギリになってからスクロールするのではなく、**「縦棒が常に画面の中央付近に来るように」**スクロール位置を制御するのが理想です。

以下の計算ロジックを参考に実装してください：
- `targetScrollPosition = currentPlayheadX - (screenWidth / 2)`
- ※ただし、`targetScrollPosition` が 0未満になる場合や、最大スクロール幅を超える場合の境界値チェック（clamp）を含めてください。

### 3. Implementation Context
- `BoxWithConstraints` を使用して、現在の画面幅（`maxWidth`）を取得してください。
- `rememberScrollState()` で作成した state に対して、アニメーションのフレーム毎に `scrollTo` を呼び出してください。

## Expected Behavior
- 再生ボタンを押すと、縦棒が右へ進む。
- 縦棒が画面中央を超えたあたりから、背景の五線譜が左へ流れ始め、縦棒は画面中央に維持されているように見える（または、縦棒に合わせて画面がスムーズに右へスクロールしていく）。

この挙動を実現するための `MusicScreen` 内の `LaunchedEffect` 周りの修正コードを提示してください。


**Prompt**:# Logic & Audio Update Request: Implementing Polyphony (Chords)

中学生が聴いた瞬間に「こっちは美しい！」「こっちは変だ！」と明確に区別できるよう、単音（Melody）だけでなく**和音（Chords/Harmony）**を実装してください。
協和音（キレイな響き）と不協和音（濁った響き）の対比を作ることで、マルコフ連鎖モデルの有効性を強調します。

## 1. Audio Engine Update (Polyphony Support)
現在の `ToneSynthesizer` は単音生成のみになっていると思われます。これを**和音再生（多重音再生）**に対応させてください。
- 複数の周波数の正弦波（Sine Wave）を足し合わせて（Mixing）、1つの音声バッファとして生成するロジックに変更してください。
- 音割れを防ぐため、合成後の振幅が `Short.MAX_VALUE` を超えないよう正規化（Normalization）またはリミッター処理を行ってください。

## 2. Music Logic Update (Chord Progression)
`MusicGenerator` を拡張し、モードごとに和音の扱いを変えてください。

### Mode A: Random Generation (Chaos)
- **不協和音の生成**: ランダムに選ばれたルート音に対し、音楽理論を無視したランダムな音程で音を重ねてください（例: ド + ド# + ファ など）。
- 聴いた瞬間に「不気味」「適当」と感じられる、カオスな響きを目指してください。

### Mode B: Markov Chain (Canon Progression)
- **カノン進行の和音**: パッヘルベルのカノン（D Major）のコード進行（D -> A -> Bm -> F#m -> G -> D -> G -> A）に完全に従った和音を生成してください。
- **構成**:
  - **伴奏**: 各小節の頭で、その小節のコード（3和音）をジャン！と鳴らす、またはアルペジオにする。
  - **メロディ**: 既存のマルコフ連鎖ロジックで生成された単音メロディを重ねる。
- これにより、「音楽の授業で聴いたような美しい響き」を実現してください。

## 3. Visual Update (Stacked Notes)
五線譜（Canvas）上でも和音が視認できるようにしてください。
- 同じタイミング（X座標）に複数の音符がある場合、それらを縦に並べて描画してください。
- 和音の場合、棒（Stem）を連結して描画できるとベストですが、まずは単純に「おたまじゃくしが縦に並んでいる」状態でも構いません。

これらの変更を反映した `ToneSynthesizer`, `MusicGenerator`, および `SheetMusicCanvas` の修正コードを提示してください。

**Prompt**:# UI Update Request: Melody-Only Staff & Chord Labels

視覚的な情報過多を防ぎつつ、背後にある数学的モデル（コード進行）を意識させるため、UIの表示ロジックを修正してください。

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
- `drawText` を使用するために、`nativeCanvas` (Android native Paint) を利用するか、Composeの `drawText` (もし利用可能なら) を使用して実装してください。
- データ構造 `Note` または `Measure` に `chordName: String?` プロパティを追加し、生成時にセットするようにしてください。

これにより、「画面上はシンプルなメロディラインだが、下を見るとコード進行（ルール）が表示されており、耳ではその調和が聴こえる」という構成を実現してください。




## Phase 2: アーキテクチャ設計
**Goal**: Android/iOS両対応のためのKMP化とVertex AI統合。
**Prompt**: (KMP移行のプロンプトをここに貼り付け)
