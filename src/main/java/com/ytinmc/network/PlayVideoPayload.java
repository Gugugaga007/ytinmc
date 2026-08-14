/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.Identifier
 */
package com.ytinmc.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public class PlayVideoPayload
implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayVideoPayload> TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"ytinmc", (String)"play_video"));
    public static final StreamCodec<FriendlyByteBuf, PlayVideoPayload> CODEC = CustomPacketPayload.codec(PlayVideoPayload::write, PlayVideoPayload::new);
    public final String senderName;
    public final String url;

    public PlayVideoPayload(String senderName, String url) {
        this.senderName = senderName != null ? senderName : "";
        this.url = url != null ? url : "";
    }

    public PlayVideoPayload(FriendlyByteBuf buf) {
        this.senderName = buf.readUtf();
        this.url = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.senderName);
        buf.writeUtf(this.url);
    }

    public CustomPacketPayload.Type<PlayVideoPayload> type() {
        return TYPE;
    }
}

