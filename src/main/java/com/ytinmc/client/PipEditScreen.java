/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package com.ytinmc.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PipEditScreen
extends Screen {
    public PipEditScreen() {
        super((Component)Component.literal((String)"PiP Editor"));
    }

    public void extractRenderState(GuiGraphicsExtractor context, int i, int j, float f) {
        super.extractRenderState(context, i, j, f);
        int textX = this.width / 2;
        int textY = this.height / 2;
        context.centeredText(this.minecraft.font, "PiP Editor Mode", textX, textY - 10, 0xFFFFFF);
        context.centeredText(this.minecraft.font, "Drag the video to move.", textX, textY + 10, 0xAAAAAA);
        context.centeredText(this.minecraft.font, "Drag bottom-right corner to resize.", textX, textY + 25, 0xAAAAAA);
        context.centeredText(this.minecraft.font, "Press ESC or Right Shift to exit.", textX, textY + 40, 0xAAAAAA);
    }
}

