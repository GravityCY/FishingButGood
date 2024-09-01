package me.gravityio.multiline_mastery.client;

import me.gravityio.multiline_mastery.ModConfig;
import me.gravityio.multiline_mastery.network.SyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class MultilineMasteryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModConfig.HANDLER.instance().angle.changed(v -> {
            if (Minecraft.getInstance().level == null) return;
            ClientPlayNetworking.send(new SyncPacket(v));
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientPlayNetworking.send(new SyncPacket(ModConfig.HANDLER.instance().angle.get())));
    }
}
