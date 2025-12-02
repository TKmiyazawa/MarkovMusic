# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Melody Math** (MarkovMusic) is a Kotlin Multiplatform educational app demonstrating three melody generation modes: random, Markov chain, and AI (Gemini). Based on a 2005 thesis project, rebuilt with modern tech stack.

## Build Commands

```bash
# Android
./gradlew :composeApp:assembleDebug          # Debug APK
./gradlew :composeApp:assembleRelease        # Release APK
./gradlew :composeApp:testDebugUnitTest      # Run tests
./gradlew clean                               # Clean build

# iOS API Key sync (reads from local.properties)
./gradlew updateIosGeminiApiKey
```

iOS builds: Open `iosApp/iosApp.xcodeproj` in Xcode.

## API Key Configuration

1. Add `GEMINI_API_KEY=your_key` to `local.properties` (Android reads via BuildConfig)
2. For iOS: `cp iosApp/Configuration/Config.xcconfig.template iosApp/Configuration/Config.xcconfig`, then run `./gradlew updateIosGeminiApiKey`

## Architecture

### Multiplatform Structure
```
composeApp/src/
├── commonMain/kotlin/com/example/markovmusic/
│   ├── App.kt                    # Main Compose UI entry point
│   ├── model/                    # Data models (Note, Pitch, Chord, Score)
│   ├── generator/                # Music generation (MusicGenerator interface)
│   │   ├── RandomGenerator.kt    # Uniform random note selection
│   │   ├── MarkovChainGenerator.kt # Weighted probability transitions
│   │   └── GeminiMelodyGenerator.kt # AI-powered via Ktor client
│   ├── network/                  # GeminiApiClient (Ktor-based)
│   ├── audio/                    # ToneSynthesizer (expect declaration)
│   └── ui/                       # StaffNotation, PlaybackController, components
├── androidMain/                  # actual implementations (AudioTrack, BuildConfig access)
├── iosMain/                      # actual implementations (AVAudioEngine, Info.plist access)
└── commonTest/                   # Shared tests
```

### Key expect/actual Declarations
- `ToneSynthesizer` - Audio synthesis: Android uses `AudioTrack`, iOS uses `AVAudioEngine`
- `getGeminiApiKey()` - API key retrieval: Android reads `BuildConfig`, iOS reads `Info.plist`
- `TextRenderer` - Canvas text measurement (platform-specific)

### Music Domain Model
- `Pitch` - Enum of musical pitches (C4-B5) with MIDI numbers and frequencies
- `Chord` - Chord definitions with `CANON_PROGRESSION` (Pachelbel's Canon: D→A→Bm→F#m→G→D→G→A)
- `Note` - Contains pitches list, start time, duration, and optional chord name
- `Score` - Collection of notes with BPM and total beats

### Generation Modes
1. **Random** - Uniform distribution across all pitches
2. **Markov Chain** - Weighted transitions based on current chord's probability matrix
3. **Gemini AI** - Prompts Gemini 1.5 Flash via Ktor HTTP client with JSON mode

## Key Configuration Files
- `gradle/libs.versions.toml` - Version catalog for all dependencies
- `composeApp/build.gradle.kts` - Module config with `updateIosGeminiApiKey` Gradle task
- `iosApp/Configuration/Config.xcconfig` - iOS environment variables (gitignored)
