# Contributing to ytinmc

Thank you for your interest in contributing to **ytinmc**!

## 🔨 Development Setup

1. **Prerequisites**:
   - Java Development Kit (JDK) 21 or 25
   - Git

2. **Clone & Build**:
   ```bash
   git clone https://github.com/Gugugaga007/ytinmc.git
   cd ytinmc
   ./gradlew build
   ```

3. **Running the Development Client**:
   ```bash
   ./gradlew runClient
   ```

---

## 📁 Code Organization

- `com.ytinmc.client` — Client GUI screens (`YoutubeScreen`, `PipEditScreen`), HUD overlays, and MCEF texture rendering.
- `com.ytinmc.hologram` — In-world 3D floating hologram screens, raycasting, and 3D spatial audio math.
- `com.ytinmc.network` — Custom Fabric packet payloads and multiplayer WatchParty synchronization.
- `com.ytinmc.command` — In-game player chat commands.

---

## 💡 Submitting Changes

1. Fork the repository and create your branch from `main`.
2. Ensure the project builds cleanly without errors.
3. Open a Pull Request with a clear summary of your changes.
