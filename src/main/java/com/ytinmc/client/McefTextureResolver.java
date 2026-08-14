/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.Identifier
 */
package com.ytinmc.client;

import com.cinemamod.mcef.MCEFRenderer;
import net.minecraft.resources.Identifier;

public class McefTextureResolver {
    public static boolean bindTexture(MCEFRenderer renderer) {
        if (renderer == null) {
            return false;
        }
        Identifier id = renderer.getTextureId();
        return id != null;
    }
}

