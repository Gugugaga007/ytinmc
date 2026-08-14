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
            String autoPlayJs = "(function() { var interval = setInterval(function() { var v = document.querySelector('video'); if (v && v.paused) { v.play().catch(function(e){}); } var btn = document.querySelector('.ytp-large-play-button'); if (btn) btn.click(); }, 500); setTimeout(function() { clearInterval(interval); }, 8000); })();";
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

        if (url.contains("youtube.com/watch") || url.contains("youtu.be/") || url.contains("youtube.com/shorts/")) {
            String videoId = null;
            if (url.contains("youtube.com/watch")) {
                int vIdx = url.indexOf("v=");
                if (vIdx != -1) {
                    int endIdx = url.indexOf("&", vIdx);
                    videoId = endIdx != -1 ? url.substring(vIdx + 2, endIdx) : url.substring(vIdx + 2);
                }
            } else if (url.contains("youtu.be/")) {
                int slashIdx = url.indexOf("youtu.be/");
                String path = url.substring(slashIdx + 9);
                int qIdx = path.indexOf("?");
                videoId = qIdx != -1 ? path.substring(0, qIdx) : path;
            } else if (url.contains("youtube.com/shorts/")) {
                int slashIdx = url.indexOf("shorts/");
                String path = url.substring(slashIdx + 7);
                int qIdx = path.indexOf("?");
                videoId = qIdx != -1 ? path.substring(0, qIdx) : path;
            }

            if (videoId != null && !videoId.isEmpty()) {
                return "https://www.youtube.com/embed/" + videoId + "?autoplay=1&enablejsapi=1&controls=1&rel=0";
            }
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
        double yawRad = Math.toRadians(-this.yaw);
        double pitchRad = Math.toRadians(-this.pitch);

        Vec3 right = new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
        Vec3 forward = new Vec3(-Math.sin(yawRad) * Math.cos(pitchRad), Math.sin(pitchRad), Math.cos(yawRad) * Math.cos(pitchRad));
        Vec3 up = right.cross(forward);

        double denom = dir.dot(forward);
        if (Math.abs(denom) < 1e-6) {
            return null;
        }

        double t = center.subtract(origin).dot(forward) / denom;
        if (t < 0 || t > maxDist) {
            return null;
        }

        Vec3 hitPos = origin.add(dir.scale(t));
        Vec3 toHit = hitPos.subtract(center);

        double localX = toHit.dot(right);
        double localY = toHit.dot(up);

        double halfW = this.width / 2.0;
        double halfH = this.height / 2.0;

        if (Math.abs(localX) <= halfW && Math.abs(localY) <= halfH) {
            double u = (localX / this.width) + 0.5;
            double v = 0.5 - (localY / this.height);
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

