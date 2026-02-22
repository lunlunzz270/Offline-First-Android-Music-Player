# Offline-First Android Music Player

A modern music streaming and offline-playback Android application built with Kotlin, Jetpack Compose, ExoPlayer, and Room, supported by a companion Kotlin Ktor streaming backend.

---

## Architecture

The project adopts the standard Android Modern App Architecture (MVVM + Repository Pattern) with unidirectional data flow:

```text
[Android Client] (android/)
  ├── UI Layer (Jetpack Compose)
  │     ├── HomeScreen (Feed, curated sections, album covers)
  │     ├── PlaylistScreen (Tracklist browsing & playback selection)
  │     ├── FavoriteScreen (Offline saved albums)
  │     └── Global PlayerBar (Persistent audio controls across tabs)
  │
  ├── ViewModel Layer (MVVM)
  │     ├── Dagger Hilt dependency injection
  │     ├── StateFlow & SharedFlow for reactive UI state
  │     └── Kotlin Coroutines for off-main-thread I/O
  │
  ├── Repository Layer (Single Source of Truth)
  │     ├── Network DataSource (Retrofit 2 + OkHttp)
  │     └── Local DataSource (Room SQLite Database)
  │
  └── Media Player Engine
        └── Google ExoPlayer (MediaSource streaming & background playback)
              │
              ▼ HTTP / REST (http://10.0.2.2:8080)
[Backend Media Server] (backend/)
  └── Kotlin Ktor / Netty Server
        ├── /feed           (Curated album sections)
        ├── /playlists/{id} (Song lists and metadata)
        └── /songs/{file}   (Static MP3 audio streaming)
```

---

## Features

- **Jetpack Compose UI**: Entirely built with declarative Jetpack Compose UI components, custom theme colors, smooth state animations, and bottom tab navigation.
- **Offline-First Persistence (Room)**: Uses Room database with Coroutine Flow support. Favorite albums and playlists are stored locally, enabling instant offline access even without network connectivity.
- **Global Player Bar & ExoPlayer**: Integrated Google ExoPlayer engine providing audio streaming with buffering, playback control (play/pause/next), and a persistent bottom player bar that stays synchronized across all screens.
- **MVVM & Reactive StateFlow**: Clean separation of concerns with Android Architecture Components. UI observes immutable StateFlow states emitted from ViewModels.
- **Dependency Injection with Dagger Hilt**: Modular architecture with `@InstallIn(SingletonComponent::class)` scoping for Database, Network, and Player modules.
- **Companion Ktor Streaming Backend**: Lightweight Kotlin Ktor microservice that serves JSON metadata and streams high-quality MP3 audio tracks directly to the emulator.

---

## Tech Stack

| Component | Technologies |
| :--- | :--- |
| **Android Client** | Kotlin, Jetpack Compose, Android Jetpack, Material Design |
| **Architecture** | MVVM, Unidirectional Data Flow (UDF), Repository Pattern |
| **Media Player** | Google ExoPlayer (Media3 / ExoPlayer core) |
| **Local Persistence** | Room Database, SQLite |
| **Networking** | Retrofit 2, OkHttp 3, Moshi / Gson |
| **Async & Reactive** | Kotlin Coroutines, StateFlow, Flow |
| **Dependency Injection**| Dagger Hilt |
| **Image Loading** | Coil (Compose image loading) |
| **Backend Service** | Kotlin, Ktor, Netty, Kotlinx Serialization |

---

## Project Structure

```text
Offline-First-Android-Music-Player/
├── android/                         # Android Studio Application Project
│   ├── app/
│   │   ├── src/main/java/com/laioffer/spotify/
│   │   │   ├── database/            # Room Database, DAO & Hilt DatabaseModule
│   │   │   ├── datamodel/           # Album, Playlist, Song, Section data models
│   │   │   ├── network/             # Retrofit NetworkApi & NetworkModule
│   │   │   ├── player/              # PlayerViewModel, PlayerModule & PlayerBar UI
│   │   │   ├── repository/          # Home, Playlist, Favorite repositories
│   │   │   └── ui/                  # Compose screens (Home, Playlist, Favorite)
│   │   └── build.gradle             # App-level dependencies & plugins
│   ├── build.gradle                 # Project-level Gradle build script
│   └── settings.gradle
│
├── backend/                         # Kotlin Ktor Media Streaming Server
│   ├── src/main/
│   │   ├── kotlin/com/laioffer/     # Application.kt (Routing & Netty setup)
│   │   └── resources/
│   │       ├── feed.json            # Home discovery feed data
│   │       ├── playlists.json       # Playlist and track metadata
│   │       └── static/songs/        # MP3 audio streaming files
│   └── build.gradle.kts             # Ktor dependencies & plugins
│
├── .gitignore                       # Clean Git filter for Android & Gradle builds
└── README.md
```

---

## Getting Started

### 1. Start the Streaming Backend

Make sure you have JDK 11+ installed. Run the embedded Ktor server:

```bash
cd backend
./gradlew run
```

The media server starts on `http://0.0.0.0:8080/`. You can verify by opening:
- `http://localhost:8080/feed` (Returns discovery JSON)
- `http://localhost:8080/playlists/1` (Returns playlist tracks)

### 2. Run the Android App

1. Open **Android Studio**.
2. Select **Open an Existing Project** and choose the `android/` directory.
3. Allow Gradle to sync dependencies.
4. Launch an Android Emulator (API 26+) or connect a physical device.
   > **Note**: Android emulators automatically route `http://10.0.2.2:8080` to your host machine's `localhost:8080`.
5. Click **Run** (`Shift + F10`) to build and start the app.
