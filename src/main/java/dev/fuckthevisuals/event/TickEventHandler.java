package dev.fuckthevisuals.event;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.feature.antibot.AntiBotFeature;
import dev.fuckthevisuals.keybind.KeybindManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public final class TickEventHandler {

    private TickEventHandler() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeybindManager.handleKeybinds(client);

            FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();

            if (cfg.antiBotEnabled && client.getNetworkHandler() != null) {
                AntiBotFeature.tick(client);
            }
        });
    }
}
