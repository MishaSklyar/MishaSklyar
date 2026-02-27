package dev.fuckthevisuals.feature.hitbox;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.util.ColorUtils;
import dev.fuckthevisuals.util.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class EntityHitboxFeature {

    private EntityHitboxFeature() {}

    public static void renderHitboxes(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        Vec3d camPos = ctx.camera().getPos();
        MatrixStack matrices = ctx.matrixStack();

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (cfg.hitboxOnlyMobs && !(entity instanceof MobEntity)) continue;

            double dist = mc.player.distanceTo(entity);
            if (dist > cfg.hitboxMaxDistance) continue;

            Box box = entity.getBoundingBox();

            int color;
            if (entity instanceof PlayerEntity) {
                color = cfg.hitboxPlayerColor;
            } else if (entity instanceof MobEntity) {
                color = cfg.hitboxMobColor;
            } else {
                color = cfg.hitboxColor;
            }

            float r = ColorUtils.red(color);
            float g = ColorUtils.green(color);
            float b = ColorUtils.blue(color);

            RenderUtils.drawWorldBox(buffer, matrix, box, r, g, b, 1f);
        }

        BufferBuilder.BuiltBuffer built = buffer.endNullable();
        if (built != null) {
            BufferRenderer.drawWithGlobalProgram(built);
        }

        if (cfg.hitboxHealthBar) {
            renderHealthBars(matrices, immediate, mc, cfg);
        }

        immediate.draw();
        matrices.pop();
    }

    private static void renderHealthBars(MatrixStack matrices,
                                          VertexConsumerProvider.Immediate immediate,
                                          MinecraftClient mc, FTVConfig cfg) {
        TextRenderer tr = mc.textRenderer;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (entity == mc.player) continue;
            if (cfg.hitboxOnlyMobs && !(entity instanceof MobEntity)) continue;

            double dist = mc.player.distanceTo(entity);
            if (dist > cfg.hitboxMaxDistance) continue;

            float hp = living.getHealth();
            float maxHp = living.getMaxHealth();
            float fraction = maxHp > 0 ? hp / maxHp : 0f;
            int hpColor = RenderUtils.getHpColor(hp, maxHp);

            Box box = entity.getBoundingBox();
            double labelX = (box.minX + box.maxX) / 2.0;
            double labelY = box.maxY + 0.3;
            double labelZ = (box.minZ + box.maxZ) / 2.0;

            matrices.push();
            matrices.translate(labelX, labelY, labelZ);

            float yaw = mc.gameRenderer.getCamera().getYaw();
            float pitch = mc.gameRenderer.getCamera().getPitch();
            matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(yaw + 180f)));
            matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(-pitch)));

            float scale = 0.025f;
            matrices.scale(-scale, -scale, scale);

            String hpText = String.format("%.1f / %.1f", hp, maxHp);
            int textWidth = tr.getWidth(hpText);

            tr.draw(hpText, -textWidth / 2f, 0, hpColor, false,
                    matrices.peek().getPositionMatrix(), immediate,
                    TextRenderer.TextLayerType.SEE_THROUGH, 0x44000000, 0xF000F0);

            matrices.pop();
        }
    }
}
