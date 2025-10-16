# Odd/Even 5x5 Multiplayer (Android + WebSocket)

This app implements a real-time 5x5 Odd/Even tap game using server-authoritative WebSockets.

Key flows:
- Client sends operations: `{ type: 'INCREMENT', square: N }`.
- Server applies them in order and broadcasts updates: `{ type: 'UPDATE', square, value }`.
- Server assigns players on connect: `{ type: 'PLAYER_ASSIGNED', player: 'ODD'|'EVEN', board: [...] }`.
- On win or disconnect, server broadcasts `{ type: 'GAME_OVER', winner, winningLine }`.

Quick start server (local):
- Prerequisites: Node.js 18+
- Commands:
  - `cd server`
  - `npm install`
  - `npm start` (listens on `ws://localhost:8080/ws`)

Android client config:
- Default URL is `ws://10.0.2.2:8080/ws` (emulator to localhost).
- To change it, edit `app/src/main/java/com/kotlin/tictactoe/network/GameWebSocketRepository.kt:103`.

Client UI shows:
- Player role (Odd/Even), connection status, waiting state.
- 5x5 grid of numbers (tap to request increment; UI updates only after server `UPDATE`).
- Game Over banner when server announces a winner.

---

## 🚀 Features
- Built with **Kotlin** and **Jetpack Compose**
- Clean **MVVM**-ready structure
- Simple **game board UI**
- Placeholder logic for extending with AI or multiplayer

---

## 📂 Project Structure
```text
app/
├── manifests/ # AndroidManifest.xml
├── java/ # Kotlin source code
│ └── com.example.tictactoe
│ ├── MainActivity.kt # Entry point
│ └── ui/ # UI components
└── res/
├── drawable/ # App icons, images
├── layout/ # (Compose usually uses Composables instead of XML)
└── values/ # colors.xml, themes.xml, strings.xml
```
---

## 🛠️ Tech Stack
- **Kotlin** – Primary language
- **Jetpack Compose** – Declarative UI
- **Android Studio** – Development environment
- **Gradle (KTS)** – Build system

---

## 📦 Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/Naver-Kotlin-Training/starter-tictactoe-kotlin.git
   cd /starter-tictactoe-kotlin
Open in Android Studio.

Select a device or emulator.

Press Run ▶️.

## 📲 APK Release
Download the latest APK from the Releases section.

## 📝 Roadmap
 Implement full game logic

 Add score tracking

 Add restart / reset button

 Improve UI with animations

 Optional AI opponent

## 📜 License
This project is licensed under the MIT License – feel free to use and modify it.
