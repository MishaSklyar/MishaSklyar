package dev.fuckthevisuals.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public final class RenderUtils {

    private RenderUtils() {}

    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawRectOutline(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void drawGradientRect(DrawContext context, int x, int y, int width, int height, int colorTop, int colorBottom) {
        context.fillGradient(x, y, x + width, y + height, colorTop, colorBottom);
    }

    public static void drawBar(DrawContext context, int x, int y, int width, int height, float fraction, int bgColor, int fgColor) {
        context.fill(x, y, x + width, y + height, bgColor);
        int filledWidth = (int) (width * Math.max(0f, Math.min(1f, fraction)));
        if (filledWidth > 0) {
            context.fill(x, y, x + filledWidth, y + height, fgColor);
        }
    }

    public static void drawWorldBox(VertexConsumer vc, Matrix4f matrix, Box box, float r, float g, float b, float a) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        drawLine(vc, matrix, x1, y1, z1, x2, y1, z1, r, g, b, a);
        drawLine(vc, matrix, x2, y1, z1, x2, y1, z2, r, g, b, a);
        drawLine(vc, matrix, x2, y1, z2, x1, y1, z2, r, g, b, a);
        drawLine(vc, matrix, x1, y1, z2, x1, y1, z1, r, g, b, a);

        drawLine(vc, matrix, x1, y2, z1, x2, y2, z1, r, g, b, a);
        drawLine(vc, matrix, x2, y2, z1, x2, y2, z2, r, g, b, a);
        drawLine(vc, matrix, x2, y2, z2, x1, y2, z2, r, g, b, a);
        drawLine(vc, matrix, x1, y2, z2, x1, y2, z1, r, g, b, a);

        drawLine(vc, matrix, x1, y1, z1, x1, y2, z1, r, g, b, a);
        drawLine(vc, matrix, x2, y1, z1, x2, y2, z1, r, g, b, a);
        drawLine(vc, matrix, x2, y1, z2, x2, y2, z2, r, g, b, a);
        drawLine(vc, matrix, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void drawLine(VertexConsumer vc, Matrix4f matrix,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float r, float g, float b, float a) {
        vc.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        vc.vertex(matrix, x2, y2, z2).color(r, g, b, a);
    }

    public static int getHpColor(float current, float max) {
        if (max <= 0) return 0xFF00FF00;
        float fraction = current / max;
        if (fraction > 0.6f) return 0xFF00FF00;
        if (fraction > 0.3f) return 0xFFFFFF00;
        return 0xFFFF4444;
    }

    public static void drawTextWithBackground(DrawContext context, net.minecraft.client.font.TextRenderer textRenderer,
                                               String text, int x, int y, int textColor, int bgColor) {
        int textWidth = textRenderer.getWidth(text);
        context.fill(x - 2, y - 2, x + textWidth + 2, y + 10, bgColor);
        context.drawText(textRenderer, text, x, y, textColor, false);
    }
}
