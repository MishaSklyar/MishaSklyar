package dev.fuckthevisuals.feature.autotool;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;

public class AutoToolFeature {

    private int previousSlot = -1;

    public void onStartMining(BlockState blockState) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        if (!cfg.autoToolEnabled) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        int bestSlot = findBestTool(mc, blockState);
        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            if (previousSlot == -1) {
                previousSlot = mc.player.getInventory().selectedSlot;
            }
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }

    public void onStopMining() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (previousSlot != -1) {
            mc.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }

    private int findBestTool(MinecraftClient mc, BlockState blockState) {
        float bestSpeed = -1f;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            float speed = stack.getMiningSpeedMultiplier(blockState);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        float currentSpeed = mc.player.getInventory()
                .getStack(mc.player.getInventory().selectedSlot)
                .getMiningSpeedMultiplier(blockState);

        if (bestSlot != -1 && bestSpeed > currentSpeed) {
            return bestSlot;
        }
        return -1;
    }
}
