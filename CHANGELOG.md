# Changelog

All notable changes to **ytinmc** are documented in this file.

---

## [1.0.9] - 2026-08-14

### ✨ Features
- **3D Spatial Audio**: Added real-time exponential distance falloff ($d_{\max} = 32$ blocks) for in-world hologram cinema screens.
- **In-World Crosshair Interaction**: Added camera raycasting and in-world cursor clicking (`sendMouseMove`, `sendMousePress`, `sendMouseRelease`) allowing players to click directly on floating hologram screens without opening a GUI.
- **In-World Aim Reticle**: Renders a dynamic crosshair dot on in-world screens when aiming at them.
- **High-DPI Retina Rendering**: Implemented physical pixel rendering (`getDpiScale()`) for crisp subpixel text and thumbnail scaling in the in-game browser.
- **Dynamic Zoom**: Added `Ctrl + Mouse Wheel` dynamic zooming support and automatic CSS dark theme injection (`#0f0f0f`).

### 🐛 Bug Fixes
- Fixed MCEF 2D buffer stride offset calculation preventing scanline and texture tearing artifacts.
- Fixed unpainted letterbox gaps at the bottom of the browser window.
- Fixed mouse hover and click coordinate alignment across high-resolution displays.

---

## [1.0.0] - Initial Release

- Initial release of **ytinmc** for Minecraft Fabric 26.1.2.
- Full in-game YouTube web browser with Chromium Embedded Framework (MCEF).
- Picture-in-Picture (PiP) mode and basic in-world 3D hologram screens.
