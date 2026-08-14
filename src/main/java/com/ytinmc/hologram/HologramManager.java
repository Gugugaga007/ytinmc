/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.ytinmc.hologram;

import com.ytinmc.hologram.HologramData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.phys.Vec3;

public class HologramManager {
    private static final Map<String, HologramData> holograms = new HashMap<String, HologramData>();

    public static void setHologram(String id, Vec3 pos, float yaw, float pitch, float width, float height, String url) {
        if (holograms.containsKey(id)) {
            HologramManager.removeHologram(id);
        }
        HologramData data = new HologramData(id, pos, yaw, pitch, width, height, url);
        holograms.put(id, data);
        data.getBrowser();
    }

    public static HologramData getHologram(String id) {
        return holograms.get(id);
    }

    public static Collection<HologramData> getActiveHolograms() {
        return holograms.values();
    }

    public static void removeHologram(String id) {
        HologramData data = holograms.remove(id);
        if (data != null) {
            data.close();
        }
    }

    public static void updateHologramUrl(String id, String newUrl) {
        HologramData data = holograms.get(id);
        if (data != null) {
            data.url = newUrl;
            if (data.getBrowser() != null) {
                data.getBrowser().loadURL(HologramData.processUrl(newUrl));
                data.injectAutoPlay();
            }
        }
    }

    public static void resizeHologram(String id, float newWidth, float newHeight) {
        HologramData data = holograms.get(id);
        if (data != null) {
            data.width = newWidth;
            data.height = newHeight;
        }
    }

    public static void clearAll() {
        for (HologramData data : holograms.values()) {
            data.close();
        }
        holograms.clear();
    }
}

