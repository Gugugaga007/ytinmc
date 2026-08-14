/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package com.ytinmc.hologram;

import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ytinmc.hologram.HologramData;
import com.ytinmc.hologram.HologramManager;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

public class HologramWorldRenderer {
    private static final Identifier AIM_DOT_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/gui/sprites/hud/crosshair.png");

    public static void render(LevelRenderContext context) {
        if (HologramManager.getActiveHolograms().isEmpty()) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }
        Camera camera = client.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.position();
        HologramData.RayHit currentAim = HologramManager.getCurrentAimTarget();

        for (HologramData holo : HologramManager.getActiveHolograms()) {
            Identifier textureId;
            MCEFBrowser browser = holo.getBrowser();
            if (browser == null || browser.getRenderer() == null || (textureId = browser.getRenderer().getTextureId()) == null) continue;
            PoseStack localMatrices = new PoseStack();
            localMatrices.translate(holo.x - cameraPos.x, holo.y - cameraPos.y, holo.z - cameraPos.z);
            localMatrices.mulPose((Quaternionfc)Axis.YP.rotationDegrees(holo.yaw));
            localMatrices.mulPose((Quaternionfc)Axis.XP.rotationDegrees(holo.pitch));
            PoseStack.Pose pose = localMatrices.last();
            Matrix4f matrix4f = pose.pose();
            VertexConsumer builder = context.bufferSource().getBuffer(RenderTypes.entityCutout((Identifier)textureId));
            float scaleX = holo.width;
            float scaleY = holo.height;
            float halfW = scaleX / 2.0f;
            float halfH = scaleY / 2.0f;
            int overlay = 655360;
            int light = 0xF000F0;
            builder.addVertex((Matrix4fc)matrix4f, -halfW, -halfH, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
            builder.addVertex((Matrix4fc)matrix4f, halfW, -halfH, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
            builder.addVertex((Matrix4fc)matrix4f, halfW, halfH, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
            builder.addVertex((Matrix4fc)matrix4f, -halfW, halfH, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
            builder.addVertex((Matrix4fc)matrix4f, halfW, -halfH, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, -1.0f);
            builder.addVertex((Matrix4fc)matrix4f, -halfW, -halfH, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, -1.0f);
            builder.addVertex((Matrix4fc)matrix4f, -halfW, halfH, 0.0f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, -1.0f);
            builder.addVertex((Matrix4fc)matrix4f, halfW, halfH, 0.0f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, -1.0f);

            if (currentAim != null && currentAim.hologram == holo) {
                float hitLocalX = (float)((currentAim.u - 0.5) * (double)holo.width);
                float hitLocalY = (float)((0.5 - currentAim.v) * (double)holo.height);
                float dotSize = 0.06f;

                VertexConsumer dotBuilder = context.bufferSource().getBuffer(RenderTypes.entityCutout(AIM_DOT_TEXTURE));
                dotBuilder.addVertex((Matrix4fc)matrix4f, hitLocalX - dotSize, hitLocalY - dotSize, 0.01f).setColor(255, 60, 60, 255).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
                dotBuilder.addVertex((Matrix4fc)matrix4f, hitLocalX + dotSize, hitLocalY - dotSize, 0.01f).setColor(255, 60, 60, 255).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
                dotBuilder.addVertex((Matrix4fc)matrix4f, hitLocalX + dotSize, hitLocalY + dotSize, 0.01f).setColor(255, 60, 60, 255).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
                dotBuilder.addVertex((Matrix4fc)matrix4f, hitLocalX - dotSize, hitLocalY + dotSize, 0.01f).setColor(255, 60, 60, 255).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(light).setNormal(pose, 0.0f, 0.0f, 1.0f);
            }
        }
    }
}

