package dev.fuckthevisuals.feature.antibot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AntiBotFeature {

    private static final Set<UUID> suspectedBots = new HashSet<>();

    private AntiBotFeature() {}

    public static void tick(MinecraftClient mc) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null) return;

        suspectedBots.clear();

        for (PlayerListEntry entry : handler.getPlayerList()) {
            if (isSuspectedBot(entry)) {
                suspectedBots.add(entry.getProfile().getId());
            }
        }
    }

    private static boolean isSuspectedBot(PlayerListEntry entry) {
        String name = entry.getProfile().getName();
        if (name == null || name.isEmpty()) return false;

        if (name.matches(".*[A-Z]{3,}[0-9]{4,}.*")) return true;

        if (name.matches("[A-Za-z]{2,4}[0-9]{4,8}")) return true;

        if (entry.getLatency() == 0) return true;

        if (!entry.getProfile().isComplete()) return true;

        return false;
    }

    public static boolean isSuspectedBot(UUID uuid) {
        return suspectedBots.contains(uuid);
    }

    public static Set<UUID> getSuspectedBots() {
        return suspectedBots;
    }
}
