/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cinemamod.mcef.MCEF
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.phys.Vec3
 */
package com.ytinmc.network;

import com.cinemamod.mcef.MCEF;
import com.ytinmc.client.YoutubeScreen;
import com.ytinmc.client.YtinmcModClient;
import com.ytinmc.hologram.HologramData;
import com.ytinmc.hologram.HologramManager;
import com.ytinmc.network.HologramPayload;
import com.ytinmc.network.PlayVideoPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class WatchPartyNetwork {
    public static void registerServer() {
        PayloadTypeRegistry.serverboundPlay().register(PlayVideoPayload.TYPE, PlayVideoPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlayVideoPayload.TYPE, PlayVideoPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(HologramPayload.TYPE, HologramPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HologramPayload.TYPE, HologramPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PlayVideoPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            String senderName = player.getName().getString();
            PlayVideoPayload outPayload = new PlayVideoPayload(senderName, payload.url);
            context.server().execute(() -> {
                for (ServerPlayer target : context.server().getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send((ServerPlayer)target, (CustomPacketPayload)outPayload);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(HologramPayload.TYPE, (payload, context) -> context.server().execute(() -> {
            for (ServerPlayer target : context.server().getPlayerList().getPlayers()) {
                ServerPlayNetworking.send((ServerPlayer)target, (CustomPacketPayload)payload);
            }
        }));
    }

    @Environment(value=EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(PlayVideoPayload.TYPE, (payload, context) -> {
            String senderName = payload.senderName;
            String url = payload.url;
            context.client().execute(() -> {
                if (context.client().player != null) {
                    context.client().player.sendSystemMessage((Component)Component.literal((String)("[WatchParty] " + senderName + " shared a video: " + url)));
                }
                YoutubeScreen.isPipMode = true;
                String cleanUrl = HologramData.processUrl(url);
                double scale = context.client().getWindow().getGuiScale();
                int pW = (int)((double)YtinmcModClient.pipWidth * scale);
                int pH = (int)((double)YtinmcModClient.pipHeight * scale);
                if (YoutubeScreen.pipBrowser != null) {
                    YoutubeScreen.pipBrowser.loadURL(cleanUrl);
                    YoutubeScreen.pipBrowser.resize(Math.max(100, pW), Math.max(100, pH));
                } else {
                    YoutubeScreen.pipBrowser = MCEF.createBrowser((String)cleanUrl, (boolean)false);
                    YoutubeScreen.pipBrowser.resize(Math.max(100, pW), Math.max(100, pH));
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(HologramPayload.TYPE, (payload, context) -> context.client().execute(() -> {
            String action = payload.action;
            if ("SPAWN".equalsIgnoreCase(action)) {
                HologramManager.setHologram(payload.id, new Vec3(payload.x, payload.y, payload.z), payload.yaw, payload.pitch, payload.width, payload.height, payload.url);
            } else if ("UPDATE_URL".equalsIgnoreCase(action)) {
                HologramManager.updateHologramUrl(payload.id, payload.url);
            } else if ("RESIZE".equalsIgnoreCase(action)) {
                HologramManager.resizeHologram(payload.id, payload.width, payload.height);
            } else if ("REMOVE".equalsIgnoreCase(action)) {
                HologramManager.removeHologram(payload.id);
            }
        }));
    }

    @Environment(value=EnvType.CLIENT)
    public static void sendHologramPacket(HologramPayload payload) {
        ClientPlayNetworking.send((CustomPacketPayload)payload);
    }

    @Environment(value=EnvType.CLIENT)
    public static void sendPlayVideoPacket(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        ClientPlayNetworking.send((CustomPacketPayload)new PlayVideoPayload("", url));
    }
}

