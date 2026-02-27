package dev.fuckthevisuals.feature.qolhud;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

public final class QolHudFeature {

    private static int cachedFps = 0;
    private static long lastFpsUpdate = 0;

    private QolHudFeature() {}

    public static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getDebugHud().shouldShowDebugHud()) return;

        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();

        List<String> lines = new ArrayList<>();

        if (cfg.qolFps) {
            long now = System.currentTimeMillis();
            if (now - lastFpsUpdate > 500) {
                cachedFps = mc.getCurrentFps();
                lastFpsUpdate = now;
            }
            lines.add("FPS: " + cachedFps);
        }

        if (cfg.qolCoords && mc.player != null) {
            int x = (int) mc.player.getX();
            int y = (int) mc.player.getY();
            int z = (int) mc.player.getZ();
            lines.add(String.format("XYZ: %d / %d / %d", x, y, z));
        }

        if (cfg.qolDirection && mc.player != null) {
            float yaw = mc.player.getYaw();
            String dir = MathUtils.getDirectionName(yaw);
            lines.add(String.format("Facing: %s (%.1f°)", dir, ((yaw % 360) + 360) % 360));
        }

        if (cfg.qolPing) {
            int ping = getPing(mc);
            String pingColor = ping < 80 ? "§a" : ping < 150 ? "§e" : "§c";
            lines.add("Ping: " + pingColor + ping + "ms");
        }

        if (lines.isEmpty()) return;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int lineHeight = mc.textRenderer.fontHeight + 2;
        int totalHeight = lines.size() * lineHeight;
        int maxWidth = 0;
        for (String line : lines) {
            int w = mc.textRenderer.getWidth(line);
            if (w > maxWidth) maxWidth = w;
        }

        int padding = 3;
        int x, y;

        switch (cfg.qolCorner) {
            case "TOP_RIGHT" -> {
                x = screenWidth - maxWidth - padding * 2;
                y = padding;
            }
            case "TOP_LEFT" -> {
                x = padding;
                y = padding;
            }
            case "BOTTOM_RIGHT" -> {
                x = screenWidth - maxWidth - padding * 2;
                y = screenHeight - totalHeight - padding * 2;
            }
            default -> {
                x = padding;
                y = screenHeight - totalHeight - padding * 2;
            }
        }

        context.fill(x - padding, y - padding,
                x + maxWidth + padding, y + totalHeight + padding,
                0x99000000);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (cfg.qolShadow) {
                context.drawTextWithShadow(mc.textRenderer, line, x, y + i * lineHeight, cfg.qolColor);
            } else {
                context.drawText(mc.textRenderer, line, x, y + i * lineHeight, cfg.qolColor, false);
            }
        }
    }

    private static int getPing(MinecraftClient mc) {
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        if (handler == null || mc.player == null) return 0;
        PlayerListEntry entry = handler.getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }
}
