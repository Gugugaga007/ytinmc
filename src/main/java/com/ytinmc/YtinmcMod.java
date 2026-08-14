/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
 *  net.minecraft.commands.CommandSourceStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.ytinmc;

import com.mojang.brigadier.CommandDispatcher;
import com.ytinmc.command.WatchPartyCommands;
import com.ytinmc.hologram.HologramCommands;
import com.ytinmc.network.WatchPartyNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YtinmcMod
implements ModInitializer {
    public static final String MOD_ID = "ytinmc";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"ytinmc");

    public void onInitialize() {
        LOGGER.info("Initializing YT-in-MC Watch Party Mod!");
        WatchPartyNetwork.registerServer();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WatchPartyCommands.register((CommandDispatcher<CommandSourceStack>)dispatcher);
            HologramCommands.register((CommandDispatcher<CommandSourceStack>)dispatcher);
        });
    }
}

