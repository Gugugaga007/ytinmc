# 📺 ytinmc (YouTube in Minecraft)

**ytinmc** is an advanced in-game web browser and multimedia mod for **Minecraft Fabric 26.1.2** powered by the **Minecraft Chromium Embedded Framework (MCEF)**. Watch YouTube videos, browse the web, create customizable in-world holograms, and sync playback with friends in multiplayer.

---

## ✨ Features

- 🌐 **In-Game Web Browser GUI**: Full Chromium CEF integration with navigation controls (Back, Forward, Refresh), direct search & URL bar, and full interactivity.
- 🔍 **Dynamic Zoom & High-DPI Rendering**: Crystal-clear Retina/High-DPI text and thumbnail scaling with interactive `Ctrl + Mouse Wheel` dynamic zoom.
- 🪟 **Picture-in-Picture (PiP) Mode**: Keep videos playing in a movable, resizable overlay while walking around and playing Minecraft.
- 🌌 **In-World 3D Holograms**: Place floating video displays anywhere in your Minecraft world using simple commands.
- 👥 **Watch Party Synchronization**: Sync video playback and navigation across client and server with multiplayer support.

---

## 🎮 Controls & Keybindings

| Key / Action | Description |
|---|---|
| **`Y`** (Default) | Open the YouTube Web Browser Screen |
| **`P`** | Toggle Picture-in-Picture (PiP) mode |
| **`Ctrl + Mouse Wheel`** | Zoom In / Zoom Out the browser dynamically |
| **`F11`** | Toggle Fullscreen Browser mode |
| **`Esc`** | Close the Browser / Pause video |

---

## 🛠️ Hologram Commands

| Command | Description |
|---|---|
| `/hologram create <url> <width> <height>` | Create an in-world screen facing your cursor |
| `/hologram delete <id>` | Remove an existing hologram screen |
| `/hologram list` | List all active holograms and coordinates |

---

## 📦 Requirements & Dependencies

- **Minecraft**: `26.1.2`
- **Fabric Loader**: `>= 0.19.0`
- **Fabric API**
- **MCEF (Minecraft Chromium Embedded Framework)** for `26.1.2` (included in `/libs/`)

---

## 🔨 Building from Source

```bash
# Clone the repository
git clone https://github.com/Gugugaga007/ytinmc.git

# Build with Gradle
./gradlew build
```

---

## 📄 License

Licensed under the [MIT License](LICENSE).

