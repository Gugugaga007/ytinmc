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

public class HologramPayload
implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HologramPayload> TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"ytinmc", (String)"hologram_sync"));
    public static final StreamCodec<FriendlyByteBuf, HologramPayload> CODEC = CustomPacketPayload.codec(HologramPayload::write, HologramPayload::new);
    public final String action;
    public final String id;
    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float pitch;
    public final float width;
    public final float height;
    public final String url;

    public HologramPayload(String action, String id, double x, double y, double z, float yaw, float pitch, float width, float height, String url) {
        this.action = action;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.width = width;
        this.height = height;
        this.url = url != null ? url : "";
    }

    public HologramPayload(FriendlyByteBuf buf) {
        this.action = buf.readUtf();
        this.id = buf.readUtf();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.width = buf.readFloat();
        this.height = buf.readFloat();
        this.url = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(this.action);
        buf.writeUtf(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeFloat(this.width);
        buf.writeFloat(this.height);
        buf.writeUtf(this.url);
    }

    public CustomPacketPayload.Type<HologramPayload> type() {
        return TYPE;
    }
}

