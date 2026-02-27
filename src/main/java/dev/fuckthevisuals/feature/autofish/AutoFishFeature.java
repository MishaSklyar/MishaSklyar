package dev.fuckthevisuals.feature.autofish;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class AutoFishFeature {

    private boolean bobberBit = false;
    private long lastActionTime = 0;
    private boolean waitingToRecast = false;

    public void onBobberSplash(FishingBobberEntity bobber) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        if (!cfg.autoFishEnabled) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        ItemStack held = mc.player.getMainHandStack();
        if (!(held.getItem() instanceof FishingRodItem)) return;

        if (!bobberBit) {
            bobberBit = true;
        }
    }

    public void tick() {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        if (!cfg.autoFishEnabled) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.interactionManager == null) return;

        ItemStack held = mc.player.getMainHandStack();
        if (!(held.getItem() instanceof FishingRodItem)) {
            bobberBit = false;
            waitingToRecast = false;
            return;
        }

        long now = System.currentTimeMillis();

        if (bobberBit && (now - lastActionTime) >= cfg.autoFishDelay) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            bobberBit = false;
            waitingToRecast = true;
            lastActionTime = now;
            return;
        }

        if (waitingToRecast && (now - lastActionTime) >= cfg.autoFishDelay) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            waitingToRecast = false;
            lastActionTime = now;
        }

        if (mc.player.fishHook == null && !waitingToRecast && !bobberBit && lastActionTime > 0
                && (now - lastActionTime) >= cfg.autoFishDelay * 2L) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            lastActionTime = now;
        }
    }

    public void onBobberCaught() {
        bobberBit = true;
    }

    public void resetState() {
        bobberBit = false;
        waitingToRecast = false;
        lastActionTime = 0;
    }
}
