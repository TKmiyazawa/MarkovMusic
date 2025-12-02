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

## Phase 2: アーキテクチャ設計
**Goal**: Android/iOS両対応のためのKMP化とVertex AI統合。
**Prompt**: (KMP移行のプロンプトをここに貼り付け)
