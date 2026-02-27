package dev.fuckthevisuals.feature.targethud;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;

import java.util.Collection;

public final class TargetHudFeature {

    private static LivingEntity currentTarget = null;
    private static long lastTargetTime = 0;
    private static final long TARGET_DISPLAY_MS = 5000;

    private TargetHudFeature() {}

    public static void setTarget(LivingEntity entity) {
        currentTarget = entity;
        lastTargetTime = System.currentTimeMillis();
    }

    public static void render(DrawContext context, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (currentTarget == null || !currentTarget.isAlive()) return;
        if (System.currentTimeMillis() - lastTargetTime > TARGET_DISPLAY_MS) {
            currentTarget = null;
            return;
        }

        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();

        int panelWidth = 160;
        int panelHeight = cfg.targetHudShowEffects ? 64 : 48;
        int x = cfg.targetHudX;
        int y = cfg.targetHudY;

        context.getMatrices().push();
        context.getMatrices().scale(cfg.targetHudScale, cfg.targetHudScale, 1f);

        context.fill(x, y, x + panelWidth, y + panelHeight, 0xAA000000);
        RenderUtils.drawRectOutline(context, x, y, panelWidth, panelHeight, 0xFF555555);

        float maxHp = currentTarget.getMaxHealth();
        float hp = currentTarget.getHealth();
        float hpFraction = maxHp > 0 ? hp / maxHp : 0f;
        int hpColor = RenderUtils.getHpColor(hp, maxHp);

        int contentX = x + 4;
        int contentY = y + 4;

        if (cfg.targetHudShowFace && currentTarget instanceof AbstractClientPlayerEntity playerEntity) {
            Identifier skin = playerEntity.getSkinTextures().texture();
            context.drawTexture(RenderLayer::getGuiTextured, skin,
                    contentX, contentY, 8f, 8f, 8, 8, 64, 64);
            contentX += 20;
        }

        String name = currentTarget.getName().getString();
        context.drawTextWithShadow(mc.textRenderer, name, contentX, contentY, 0xFFFFFF);

        String hpText = String.format("%.1f / %.1f HP", hp, maxHp);
        context.drawTextWithShadow(mc.textRenderer, hpText, contentX, contentY + 11, hpColor);

        RenderUtils.drawBar(context, x + 4, y + panelHeight - 20, panelWidth - 8, 4, hpFraction, 0xFF333333, hpColor);

        if (cfg.targetHudShowArmor) {
            int armor = currentTarget.getArmor();
            if (armor > 0) {
                context.drawTextWithShadow(mc.textRenderer, "\u26c9 " + armor, x + 4, y + panelHeight - 30, 0xAABBFF);
            }
        }

        if (cfg.targetHudShowEffects) {
            Collection<StatusEffectInstance> effects = currentTarget.getStatusEffects();
            int drawnEffects = 0;
            for (StatusEffectInstance effect : effects) {
                if (drawnEffects >= 8) break;
                String symbol = effect.getEffectType().value().isBeneficial() ? "\u00a7a+" : "\u00a7c-";
                context.drawTextWithShadow(mc.textRenderer, symbol,
                        x + 4 + drawnEffects * 10, y + panelHeight - 12, 0xFFFFFF);
                drawnEffects++;
            }
        }

        context.getMatrices().pop();
    }

    public static LivingEntity getCurrentTarget() {
        return currentTarget;
    }
}
