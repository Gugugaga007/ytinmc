/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 */
package com.ytinmc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ytinmc.network.PlayVideoPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class WatchPartyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)Commands.literal((String)"watchparty").then(Commands.literal((String)"play").then(Commands.argument((String)"url", (ArgumentType)StringArgumentType.greedyString()).executes(context -> {
            String url = StringArgumentType.getString((CommandContext)context, (String)"url");
            ServerPlayer senderPlayer = ((CommandSourceStack)context.getSource()).getPlayer();
            String senderName = senderPlayer != null ? senderPlayer.getName().getString() : "Server";
            ((CommandSourceStack)context.getSource()).getServer().execute(() -> {
                for (ServerPlayer target : ((CommandSourceStack)context.getSource()).getServer().getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send((ServerPlayer)target, (CustomPacketPayload)new PlayVideoPayload(senderName, url));
                }
            });
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)("Playing video: " + url)), false);
            return 1;
        }))));
    }
}

