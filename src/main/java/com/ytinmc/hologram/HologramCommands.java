/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.phys.Vec3
 */
package com.ytinmc.hologram;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ytinmc.network.HologramPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class HologramCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"hologram").requires(source -> true)).then(((LiteralArgumentBuilder)Commands.literal((String)"spawn").then(Commands.argument((String)"url", (ArgumentType)StringArgumentType.greedyString()).executes(context -> HologramCommands.spawnHologram((CommandSourceStack)context.getSource(), "main", StringArgumentType.getString((CommandContext)context, (String)"url"))))).then(Commands.argument((String)"id", (ArgumentType)StringArgumentType.string()).then(Commands.argument((String)"url", (ArgumentType)StringArgumentType.greedyString()).executes(context -> HologramCommands.spawnHologram((CommandSourceStack)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"id"), StringArgumentType.getString((CommandContext)context, (String)"url"))))))).then(((LiteralArgumentBuilder)Commands.literal((String)"create").then(Commands.argument((String)"url", (ArgumentType)StringArgumentType.greedyString()).executes(context -> HologramCommands.spawnHologram((CommandSourceStack)context.getSource(), "main", StringArgumentType.getString((CommandContext)context, (String)"url"))))).then(Commands.argument((String)"id", (ArgumentType)StringArgumentType.string()).then(Commands.argument((String)"url", (ArgumentType)StringArgumentType.greedyString()).executes(context -> HologramCommands.spawnHologram((CommandSourceStack)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"id"), StringArgumentType.getString((CommandContext)context, (String)"url"))))))).then(((LiteralArgumentBuilder)Commands.literal((String)"remove").then(Commands.argument((String)"id", (ArgumentType)StringArgumentType.string()).executes(context -> {
            String id = StringArgumentType.getString((CommandContext)context, (String)"id");
            HologramPayload payload = new HologramPayload("REMOVE", id, 0.0, 0.0, 0.0, 0.0f, 0.0f, 0.0f, 0.0f, "");
            HologramCommands.broadcastHologramAction((CommandSourceStack)context.getSource(), payload);
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Removed hologram " + id)), false);
            return 1;
        }))).executes(context -> {
            HologramPayload payload = new HologramPayload("REMOVE", "main", 0.0, 0.0, 0.0, 0.0f, 0.0f, 0.0f, 0.0f, "");
            HologramCommands.broadcastHologramAction((CommandSourceStack)context.getSource(), payload);
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)"Removed hologram main"), false);
            return 1;
        }))).then(Commands.literal((String)"play").then(Commands.argument((String)"id", (ArgumentType)StringArgumentType.string()).then(Commands.argument((String)"url", (ArgumentType)StringArgumentType.greedyString()).executes(context -> {
            String id = StringArgumentType.getString((CommandContext)context, (String)"id");
            String url = StringArgumentType.getString((CommandContext)context, (String)"url");
            HologramPayload payload = new HologramPayload("UPDATE_URL", id, 0.0, 0.0, 0.0, 0.0f, 0.0f, 0.0f, 0.0f, url);
            HologramCommands.broadcastHologramAction((CommandSourceStack)context.getSource(), payload);
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Playing " + url + " on hologram " + id)), false);
            return 1;
        }))))).then(Commands.literal((String)"resize").then(Commands.argument((String)"id", (ArgumentType)StringArgumentType.string()).then(Commands.argument((String)"width", (ArgumentType)FloatArgumentType.floatArg((float)0.1f)).then(Commands.argument((String)"height", (ArgumentType)FloatArgumentType.floatArg((float)0.1f)).executes(context -> {
            String id = StringArgumentType.getString((CommandContext)context, (String)"id");
            float w = FloatArgumentType.getFloat((CommandContext)context, (String)"width");
            float h = FloatArgumentType.getFloat((CommandContext)context, (String)"height");
            HologramPayload payload = new HologramPayload("RESIZE", id, 0.0, 0.0, 0.0, 0.0f, 0.0f, w, h, "");
            HologramCommands.broadcastHologramAction((CommandSourceStack)context.getSource(), payload);
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Resized hologram " + id + " to " + w + "x" + h)), false);
            return 1;
        }))))));
    }

    private static int spawnHologram(CommandSourceStack source, String id, String url) {
        try {
            ServerPlayer player = source.getPlayerOrException();
            Vec3 lookVec = player.getLookAngle();
            Vec3 spawnPos = player.getEyePosition().add(lookVec.scale(3.5));
            float yaw = player.getYRot() + 180.0f;
            float pitch = 0.0f;
            float width = 4.0f;
            float height = 2.25f;
            HologramPayload payload = new HologramPayload("SPAWN", id, spawnPos.x, spawnPos.y, spawnPos.z, yaw, pitch, width, height, url);
            HologramCommands.broadcastHologramAction(source, payload);
            source.sendSuccess(() -> Component.literal((String)"Spawned 3D Hologram screen in front of you!"), false);
        }
        catch (Exception e) {
            source.sendFailure((Component)Component.literal((String)("Error spawning hologram: " + e.getMessage())));
        }
        return 1;
    }

    private static void broadcastHologramAction(CommandSourceStack source, HologramPayload payload) {
        if (source.getServer() != null) {
            for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
                ServerPlayNetworking.send((ServerPlayer)player, (CustomPacketPayload)payload);
            }
        }
    }
}

