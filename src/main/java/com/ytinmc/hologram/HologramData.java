/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cinemamod.mcef.MCEF
 *  net.minecraft.world.phys.Vec3
 */
package com.ytinmc.hologram;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import net.minecraft.world.phys.Vec3;

public class HologramData {
    public final String id;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public float width;
    public float height;
    public String url;
    private MCEFBrowser browser;

    public HologramData(String id, Vec3 pos, float yaw, float pitch, float width, float height, String url) {
        this.id = id;
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.width = width;
        this.height = height;
        this.url = url;
    }

    public void injectAutoPlay() {
        if (this.browser != null) {
            String autoPlayJs = "let apInterval = setInterval(() => {   let v = document.querySelector('video');   if (v) {     if (v.paused && !window.hasAutoPlayed) {       v.play();       window.hasAutoPlayed = true;     }     if (window.hasAutoPlayed) clearInterval(apInterval);   } }, 1000);";
            this.browser.executeJavaScript(autoPlayJs, "", 0);
        }
    }

    public MCEFBrowser getBrowser() {
        if (this.browser == null) {
            String processedUrl = HologramData.processUrl(this.url);
            this.browser = MCEF.createBrowser((String)processedUrl, (boolean)false);
            this.browser.resize(1920, 1080);
            this.injectAutoPlay();
        }
        return this.browser;
    }

    public void close() {
        if (this.browser != null) {
            this.browser.close(false);
            this.browser = null;
        }
    }

    public static String processUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isEmpty()) {
            return "https://www.youtube.com";
        }
        String url = rawUrl;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }
}

