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

    private int injectCountdown = 20;

    public void injectAutoPlay() {
        if (this.browser != null) {
            String autoPlayJs = "(function() { function applyCinema() { var css = '#masthead-container, #masthead, #secondary, #below, #comments, #chat, #actions, ytd-watch-metadata, #info, tp-yt-app-drawer, ytd-miniplayer, #guide-wrapper { display: none !important; } html, body, ytd-app, #content, #page-manager, ytd-watch-flexy, #columns, #primary, #primary-inner { margin: 0 !important; padding: 0 !important; overflow: hidden !important; background: #000 !important; width: 100vw !important; height: 100vh !important; max-width: 100vw !important; max-height: 100vh !important; } #player, #player-container, #player-container-outer, #player-container-inner, #ytd-player, .html5-video-player, video { position: fixed !important; top: 0 !important; left: 0 !important; width: 100vw !important; height: 100vh !important; max-width: 100vw !important; max-height: 100vh !important; z-index: 999999 !important; margin: 0 !important; padding: 0 !important; background: #000 !important; object-fit: contain !important; }'; var s = document.getElementById('holo-style'); if (!s) { s = document.createElement('style'); s.id = 'holo-style'; (document.head || document.documentElement).appendChild(s); } if (s.textContent !== css) { s.textContent = css; } var v = document.querySelector('video'); if (v) { if (v.paused) { v.play().catch(function(e){}); } if (v.muted) { v.muted = false; } } var btn = document.querySelector('.ytp-large-play-button') || document.querySelector('.ytp-play-button'); if (btn && v && v.paused) { btn.click(); } var unmute = document.querySelector('.ytp-unmute-button'); if (unmute) { unmute.click(); } } applyCinema(); if (!window.__holo_timer) { window.__holo_timer = setInterval(applyCinema, 500); } })();";
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
        String url = rawUrl.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        if (url.contains("youtu.be/")) {
            int slashIdx = url.indexOf("youtu.be/");
            String path = url.substring(slashIdx + 9);
            int qIdx = path.indexOf("?");
            String videoId = qIdx != -1 ? path.substring(0, qIdx) : path;
            return "https://www.youtube.com/watch?v=" + videoId;
        } else if (url.contains("youtube.com/shorts/")) {
            int slashIdx = url.indexOf("shorts/");
            String path = url.substring(slashIdx + 7);
            int qIdx = path.indexOf("?");
            String videoId = qIdx != -1 ? path.substring(0, qIdx) : path;
            return "https://www.youtube.com/watch?v=" + videoId;
        }

        return url;
    }

    private double lastVolume = -1.0;

    public static class RayHit {
        public final HologramData hologram;
        public final double distance;
        public final double u;
        public final double v;
        public final Vec3 hitPos;

        public RayHit(HologramData hologram, double distance, double u, double v, Vec3 hitPos) {
            this.hologram = hologram;
            this.distance = distance;
            this.u = u;
            this.v = v;
            this.hitPos = hitPos;
        }
    }

    public void updateSpatialAudio(Vec3 playerPos) {
        if (this.browser == null) return;
        if (injectCountdown > 0) {
            injectCountdown--;
            this.injectAutoPlay();
        }
        Vec3 holoPos = new Vec3(this.x, this.y, this.z);
        double dist = playerPos.distanceTo(holoPos);
        double maxDist = 32.0;
        double vol = 0.0;
        if (dist < maxDist) {
            vol = Math.max(0.0, 1.0 - (dist / maxDist));
            vol = Math.pow(vol, 1.3);
        }
        vol = Math.round(vol * 100.0) / 100.0;
        if (Math.abs(vol - lastVolume) >= 0.02) {
            lastVolume = vol;
            this.browser.executeJavaScript("document.querySelectorAll('video, audio').forEach(v => { v.volume = " + vol + "; });", "", 0);
        }
    }

    public RayHit raycast(Vec3 origin, Vec3 dir, double maxDist) {
        Vec3 center = new Vec3(this.x, this.y, this.z);

        org.joml.Quaternionf q = new org.joml.Quaternionf();
        q.rotateY((float)Math.toRadians(this.yaw));
        q.rotateX((float)Math.toRadians(this.pitch));

        org.joml.Vector3f r = new org.joml.Vector3f(1.0f, 0.0f, 0.0f).rotate(q);
        org.joml.Vector3f uVec = new org.joml.Vector3f(0.0f, 1.0f, 0.0f).rotate(q);
        org.joml.Vector3f f = new org.joml.Vector3f(0.0f, 0.0f, 1.0f).rotate(q);

        Vec3 right = new Vec3((double)r.x(), (double)r.y(), (double)r.z());
        Vec3 up = new Vec3((double)uVec.x(), (double)uVec.y(), (double)uVec.z());
        Vec3 normal = new Vec3((double)f.x(), (double)f.y(), (double)f.z());

        double denom = dir.dot(normal);
        if (Math.abs(denom) < 1e-6) {
            return null;
        }

        double t = center.subtract(origin).dot(normal) / denom;
        if (t < 0.0 || t > maxDist) {
            return null;
        }

        Vec3 hitPos = origin.add(dir.scale(t));
        Vec3 toHit = hitPos.subtract(center);

        double localX = toHit.dot(right);
        double localY = toHit.dot(up);

        double halfW = (double)this.width / 2.0;
        double halfH = (double)this.height / 2.0;

        if (Math.abs(localX) <= halfW && Math.abs(localY) <= halfH) {
            double u = (localX / (double)this.width) + 0.5;
            double v = 0.5 - (localY / (double)this.height);
            return new RayHit(this, t, u, v, hitPos);
        }

        return null;
    }

    public void clickAt(double u, double v, int button) {
        if (this.browser == null) return;
        int pixelX = (int)(Math.max(0.0, Math.min(1.0, u)) * 1920.0);
        int pixelY = (int)(Math.max(0.0, Math.min(1.0, v)) * 1080.0);
        this.browser.sendMouseMove(pixelX, pixelY);
        this.browser.sendMousePress(pixelX, pixelY, button);
        this.browser.sendMouseRelease(pixelX, pixelY, button);
    }
}

