package dev.fuckthevisuals.feature.waypoint;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.util.ColorUtils;
import dev.fuckthevisuals.util.MathUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.List;

public final class WaypointRenderer {

    private WaypointRenderer() {}

    public static void render(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        WaypointManager wm = FuckTheVisuals.getInstance().getWaypointManager();

        String dimension = mc.world.getRegistryKey().getValue().toString();
        List<Waypoint> active = wm.getActiveWaypointsForDimension(dimension);
        if (active.isEmpty()) return;

        PlayerEntity player = mc.player;
        Vec3d camPos = ctx.camera().getPos();

        MatrixStack matrices = ctx.matrixStack();
        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

        for (Waypoint wp : active) {
            double dx = wp.getX() - player.getX();
            double dy = wp.getY() - player.getY();
            double dz = wp.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist > cfg.waypointMaxRenderDistance) continue;

            float r = ColorUtils.red(wp.getColor());
            float g = ColorUtils.green(wp.getColor());
            float b = ColorUtils.blue(wp.getColor());
            float a = cfg.waypointBeamAlpha;

            if (cfg.waypointBeams) {
                renderBeam(matrices, immediate, wp, r, g, b, a);
            }

            if (cfg.waypointLabels || cfg.waypointDistance) {
                renderLabel(matrices, mc, wp, dist, cfg, r, g, b);
            }
        }

        immediate.draw();
        matrices.pop();
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vcp,
                                    Waypoint wp, float r, float g, float b, float a) {
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getDebugLineStrip(2.0));
        org.joml.Matrix4f matrix = matrices.peek().getPositionMatrix();

        vc.vertex(matrix, (float) wp.getX(), -64f, (float) wp.getZ()).color(r, g, b, a);
        vc.vertex(matrix, (float) wp.getX(), 320f, (float) wp.getZ()).color(r, g, b, a * 0.1f);
    }

    private static void renderLabel(MatrixStack matrices, MinecraftClient mc,
                                     Waypoint wp, double dist, FTVConfig cfg,
                                     float r, float g, float b) {
        TextRenderer tr = mc.textRenderer;

        matrices.push();
        matrices.translate(wp.getX(), wp.getY() + 2.5, wp.getZ());

        float yaw = mc.gameRenderer.getCamera().getYaw();
        float pitch = mc.gameRenderer.getCamera().getPitch();

        matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(yaw + 180f)));
        matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(-pitch)));

        float scale = 0.025f;
        matrices.scale(-scale, -scale, scale);

        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        int labelColor = (int)(r * 255) << 16 | (int)(g * 255) << 8 | (int)(b * 255) | 0xFF000000;

        if (cfg.waypointLabels) {
            String name = wp.getName();
            int nameWidth = tr.getWidth(name);
            tr.draw(name, -nameWidth / 2f, 0, labelColor, false,
                    matrices.peek().getPositionMatrix(), immediate,
                    TextRenderer.TextLayerType.SEE_THROUGH, 0x44000000, 0xF000F0);
        }

        if (cfg.waypointDistance) {
            String distStr = MathUtils.formatDistance(dist);
            int distWidth = tr.getWidth(distStr);
            tr.draw(distStr, -distWidth / 2f, 10, 0xFFFFFFFF, false,
                    matrices.peek().getPositionMatrix(), immediate,
                    TextRenderer.TextLayerType.SEE_THROUGH, 0x44000000, 0xF000F0);
        }

        immediate.draw();
        matrices.pop();
    }
}
