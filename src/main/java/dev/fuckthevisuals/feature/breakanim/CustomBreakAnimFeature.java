package dev.fuckthevisuals.feature.breakanim;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.util.ColorUtils;
import dev.fuckthevisuals.util.RenderUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public final class CustomBreakAnimFeature {

    private CustomBreakAnimFeature() {}

    public static void renderBreakOverlay(MatrixStack matrices, VertexConsumerProvider vcp,
                                           BlockPos pos, int stage) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        if (!cfg.breakAnimEnabled) return;

        BreakAnimStyle style = BreakAnimStyle.fromString(cfg.breakAnimStyle);
        float progress = stage / 9f;
        int color = cfg.breakAnimColor;
        float alpha = cfg.breakAnimAlpha * progress;

        float r = ColorUtils.red(color);
        float g = ColorUtils.green(color);
        float b = ColorUtils.blue(color);

        switch (style) {
            case GRADIENT -> renderGradient(matrices, vcp, pos, progress, r, g, b, alpha);
            case OUTLINE -> renderOutline(matrices, vcp, pos, r, g, b, alpha);
            case SHRINK -> renderShrink(matrices, vcp, pos, progress, r, g, b, alpha);
            case COLOR_OVERLAY -> renderColorOverlay(matrices, vcp, pos, r, g, b, alpha);
            default -> renderGradient(matrices, vcp, pos, progress, r, g, b, alpha);
        }
    }

    private static void renderGradient(MatrixStack matrices, VertexConsumerProvider vcp,
                                        BlockPos pos, float progress, float r, float g, float b, float a) {
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getDebugFilledBox());
        Box box = new Box(pos).expand(-0.002);
        renderFilledBox(vc, matrices.peek().getPositionMatrix(), box, r, g, b, a);
    }

    private static void renderOutline(MatrixStack matrices, VertexConsumerProvider vcp,
                                       BlockPos pos, float r, float g, float b, float a) {
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getDebugLineStrip(3.0));
        Box box = new Box(pos).expand(-0.002);
        RenderUtils.drawWorldBox(vc, matrices.peek().getPositionMatrix(), box, r, g, b, a);
    }

    private static void renderShrink(MatrixStack matrices, VertexConsumerProvider vcp,
                                      BlockPos pos, float progress, float r, float g, float b, float a) {
        double shrink = 0.5 - progress * 0.5;
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getDebugFilledBox());
        Box box = new Box(pos).expand(-shrink);
        renderFilledBox(vc, matrices.peek().getPositionMatrix(), box, r, g, b, a);
    }

    private static void renderColorOverlay(MatrixStack matrices, VertexConsumerProvider vcp,
                                            BlockPos pos, float r, float g, float b, float a) {
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getDebugFilledBox());
        Box box = new Box(pos).expand(-0.002);
        renderFilledBox(vc, matrices.peek().getPositionMatrix(), box, r, g, b, a * 0.5f);
    }

    private static void renderFilledBox(VertexConsumer vc, Matrix4f matrix, Box box,
                                         float r, float g, float b, float a) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        vc.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        vc.vertex(matrix, x2, y1, z1).color(r, g, b, a);
        vc.vertex(matrix, x1, y2, z1).color(r, g, b, a);
        vc.vertex(matrix, x2, y2, z1).color(r, g, b, a);
        vc.vertex(matrix, x1, y1, z2).color(r, g, b, a);
        vc.vertex(matrix, x2, y1, z2).color(r, g, b, a);
        vc.vertex(matrix, x1, y2, z2).color(r, g, b, a);
        vc.vertex(matrix, x2, y2, z2).color(r, g, b, a);
    }
}
