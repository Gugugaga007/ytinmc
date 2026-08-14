/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cinemamod.mcef.MCEF
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.KeyMapping$Category
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 */
package com.ytinmc.client;

import com.cinemamod.mcef.MCEF;
import com.mojang.blaze3d.platform.InputConstants;
import com.ytinmc.client.PipEditScreen;
import com.ytinmc.client.YoutubeScreen;
import com.ytinmc.hologram.HologramWorldRenderer;
import com.ytinmc.network.WatchPartyNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class YtinmcModClient
implements ClientModInitializer {
    private static KeyMapping openYoutubeKey;
    private static KeyMapping togglePipKey;
    private static KeyMapping editPipKey;
    public static int pipX;
    public static int pipY;
    public static int pipWidth;
    public static int pipHeight;
    private static boolean isDraggingPip;
    private static boolean isResizingPip;
    private static double dragOffsetX;
    private static double dragOffsetY;
    private static double initialMouseX;
    private static double initialMouseY;
    private static int initialWidth;
    private static int initialHeight;

    public void onInitializeClient() {
        openYoutubeKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.ytinmc.open", InputConstants.Type.KEYSYM, 89, KeyMapping.Category.MISC));
        togglePipKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.ytinmc.toggle_pip", InputConstants.Type.KEYSYM, 80, KeyMapping.Category.MISC));
        editPipKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.ytinmc.edit_pip", InputConstants.Type.KEYSYM, 344, KeyMapping.Category.MISC));
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(context -> HologramWorldRenderer.render(context));
        WatchPartyNetwork.registerClient();
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof YoutubeScreen) {
                return;
            }
            ScreenMouseEvents.allowMouseClick((Screen)screen).register((s, event) -> {
                if (YoutubeScreen.isPipMode && YoutubeScreen.pipBrowser != null) {
                    double mouseX = event.x();
                    double mouseY = event.y();
                    int screenW = client.getWindow().getGuiScaledWidth();
                    int pX = pipX == -1 ? screenW - pipWidth - 10 : pipX;
                    int pY = pipY;
                    if (mouseX >= (double)(pX + pipWidth - 16) && mouseX <= (double)(pX + pipWidth + 4) && mouseY >= (double)(pY + pipHeight - 16) && mouseY <= (double)(pY + pipHeight + 4)) {
                        isResizingPip = true;
                        initialMouseX = mouseX;
                        initialMouseY = mouseY;
                        initialWidth = pipWidth;
                        initialHeight = pipHeight;
                        pipX = pX;
                        return false;
                    }
                    if (mouseX >= (double)pX && mouseX <= (double)(pX + pipWidth) && mouseY >= (double)pY && mouseY <= (double)(pY + pipHeight)) {
                        isDraggingPip = true;
                        dragOffsetX = mouseX - (double)pX;
                        dragOffsetY = mouseY - (double)pY;
                        pipX = pX;
                        return false;
                    }
                }
                return true;
            });
            ScreenMouseEvents.afterMouseRelease((Screen)screen).register((s, event, handled) -> {
                isDraggingPip = false;
                isResizingPip = false;
                return handled;
            });
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            com.ytinmc.hologram.HologramManager.tick(client);
            if (client.screen == null && com.ytinmc.hologram.HologramManager.getCurrentAimTarget() != null) {
                while (client.options.keyUse.consumeClick()) {
                    com.ytinmc.hologram.HologramManager.onPlayerInteract(0);
                }
                while (client.options.keyAttack.consumeClick()) {
                    com.ytinmc.hologram.HologramManager.onPlayerInteract(0);
                }
            }
            while (openYoutubeKey.consumeClick()) {
                if (client.screen != null) continue;
                client.setScreen((Screen)new YoutubeScreen());
            }
            while (editPipKey.consumeClick()) {
                if (client.screen != null) continue;
                client.setScreen((Screen)new PipEditScreen());
            }
            while (togglePipKey.consumeClick()) {
                boolean bl = YoutubeScreen.isPipMode = !YoutubeScreen.isPipMode;
                if (client.player != null) {
                    client.player.sendSystemMessage((Component)Component.literal((String)("PiP Mode is now " + (YoutubeScreen.isPipMode ? "ON" : "OFF"))));
                }
                if (YoutubeScreen.pipBrowser == null && YoutubeScreen.isPipMode) {
                    YoutubeScreen.pipBrowser = MCEF.createBrowser((String)"https://www.youtube.com", (boolean)false);
                }
                if (YoutubeScreen.pipBrowser == null) continue;
                if (YoutubeScreen.isPipMode) {
                    double scale = client.getWindow().getGuiScale();
                    YoutubeScreen.pipBrowser.resize((int)((double)pipWidth * scale), (int)((double)pipHeight * scale));
                    continue;
                }
                YoutubeScreen.pipBrowser.executeJavaScript("document.querySelectorAll('video').forEach(v => v.pause());", "", 0);
            }
            if (YoutubeScreen.isPipMode && YoutubeScreen.pipBrowser != null && client.screen != null) {
                double mouseX = client.mouseHandler.xpos() * (double)client.getWindow().getGuiScaledWidth() / (double)client.getWindow().getScreenWidth();
                double mouseY = client.mouseHandler.ypos() * (double)client.getWindow().getGuiScaledHeight() / (double)client.getWindow().getScreenHeight();
                if (isDraggingPip) {
                    int screenWidth = client.getWindow().getGuiScaledWidth();
                    int screenHeight = client.getWindow().getGuiScaledHeight();
                    pipX = Math.max(0, Math.min(screenWidth - pipWidth, (int)(mouseX - dragOffsetX)));
                    pipY = Math.max(0, Math.min(screenHeight - pipHeight, (int)(mouseY - dragOffsetY)));
                } else if (isResizingPip) {
                    int deltaX = (int)(mouseX - initialMouseX);
                    int newWidth = Math.max(160, Math.min(900, initialWidth + deltaX));
                    int newHeight = Math.max(90, Math.min(600, (int)((double)newWidth * 9.0 / 16.0)));
                    pipWidth = newWidth;
                    pipHeight = newHeight;
                    double scale = client.getWindow().getGuiScale();
                    YoutubeScreen.pipBrowser.resize((int)((double)pipWidth * scale), (int)((double)pipHeight * scale));
                }
            }
        });
        HudElementRegistry.addLast((Identifier)Identifier.fromNamespaceAndPath((String)"ytinmc", (String)"pip_hud"), (context, deltaTracker) -> {
            if (YoutubeScreen.isPipMode && YoutubeScreen.pipBrowser != null) {
                Minecraft client = Minecraft.getInstance();
                if (client.screen instanceof YoutubeScreen) {
                    return;
                }
                int screenWidth = client.getWindow().getGuiScaledWidth();
                int x = pipX == -1 ? screenWidth - pipWidth - 10 : pipX;
                int y = pipY;
                context.fill(x - 2, y - 2, x + pipWidth + 2, y + pipHeight + 2, -16777216);
                YoutubeScreen.drawBrowser(x, y, pipWidth, pipHeight, YoutubeScreen.pipBrowser, context);
                if (client.screen != null) {
                    context.fill(x + pipWidth - 8, y + pipHeight - 8, x + pipWidth, y + pipHeight, -855651328);
                }
            }
        });
    }

    static {
        pipX = -1;
        pipY = 10;
        pipWidth = 320;
        pipHeight = 180;
        isDraggingPip = false;
        isResizingPip = false;
        dragOffsetX = 0.0;
        dragOffsetY = 0.0;
        initialMouseX = 0.0;
        initialMouseY = 0.0;
        initialWidth = 320;
        initialHeight = 180;
    }
}

