package dev.fuckthevisuals.feature.watermark;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class WatermarkFeature {

    private WatermarkFeature() {}

    public static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getDebugHud().shouldShowDebugHud()) return;

        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        String text = cfg.watermarkText;
        if (text == null || text.isEmpty()) return;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int textWidth = mc.textRenderer.getWidth(text);
        int textHeight = mc.textRenderer.fontHeight;

        int padding = 3;
        int x, y;

        switch (cfg.watermarkCorner) {
            case "TOP_RIGHT" -> {
                x = screenWidth - textWidth - padding * 2;
                y = padding;
            }
            case "BOTTOM_LEFT" -> {
                x = padding;
                y = screenHeight - textHeight - padding * 2;
            }
            case "BOTTOM_RIGHT" -> {
                x = screenWidth - textWidth - padding * 2;
                y = screenHeight - textHeight - padding * 2;
            }
            default -> {
                x = padding;
                y = padding;
            }
        }

        context.getMatrices().push();
        context.getMatrices().scale(cfg.watermarkScale, cfg.watermarkScale, 1f);

        int scaledX = (int) (x / cfg.watermarkScale);
        int scaledY = (int) (y / cfg.watermarkScale);

        context.fill(scaledX - padding, scaledY - padding,
                scaledX + textWidth + padding, scaledY + textHeight + padding,
                0x99000000);

        if (cfg.watermarkShadow) {
            context.drawTextWithShadow(mc.textRenderer, text, scaledX, scaledY, cfg.watermarkColor);
        } else {
            context.drawText(mc.textRenderer, text, scaledX, scaledY, cfg.watermarkColor, false);
        }

        context.getMatrices().pop();
    }
}
