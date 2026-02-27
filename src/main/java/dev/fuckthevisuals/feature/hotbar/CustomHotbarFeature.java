package dev.fuckthevisuals.feature.hotbar;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.util.ColorUtils;
import dev.fuckthevisuals.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class CustomHotbarFeature {

    private static final int SLOT_SIZE = 20;
    private static final int SLOT_COUNT = 9;
    private static final int HOTBAR_HEIGHT = 22;

    private CustomHotbarFeature() {}

    public static void render(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        HotbarStyle style = HotbarStyle.fromString(cfg.hotbarStyle);

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int hotbarWidth = SLOT_SIZE * SLOT_COUNT + 2;
        int x = (screenWidth - hotbarWidth) / 2;
        int y = screenHeight - HOTBAR_HEIGHT - 2;

        context.getMatrices().push();
        context.getMatrices().scale(cfg.hotbarScale, cfg.hotbarScale, 1f);

        int scaledX = (int) (x / cfg.hotbarScale);
        int scaledY = (int) (y / cfg.hotbarScale);

        renderBackground(context, style, scaledX, scaledY, hotbarWidth);

        int selectedSlot = mc.player.getInventory().selectedSlot;

        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            int slotX = scaledX + 1 + slot * SLOT_SIZE;
            int slotY = scaledY + 1;

            ItemStack stack = mc.player.getInventory().getStack(slot);

            renderSlot(context, mc, style, slotX, slotY, stack, slot, slot == selectedSlot, cfg);
        }

        context.getMatrices().pop();
    }

    private static void renderBackground(DrawContext context, HotbarStyle style, int x, int y, int width) {
        switch (style) {
            case VANILLA_PLUS -> {
                context.fill(x, y, x + width, y + HOTBAR_HEIGHT, 0xCC1A1A1A);
                RenderUtils.drawRectOutline(context, x, y, width, HOTBAR_HEIGHT, 0xFF444444);
            }
            case MINIMAL -> {
                context.fill(x, y, x + width, y + HOTBAR_HEIGHT, 0x55000000);
            }
            case CLASSIC -> {
                context.fill(x, y, x + width, y + HOTBAR_HEIGHT, 0xDD2A2A2A);
                context.fill(x, y, x + width, y + 1, 0xFF666666);
                context.fill(x, y + HOTBAR_HEIGHT - 1, x + width, y + HOTBAR_HEIGHT, 0xFF333333);
            }
            case MODERN -> {
                context.fillGradient(x, y, x + width, y + HOTBAR_HEIGHT, 0xDD1A1A2A, 0xDD0A0A1A);
                RenderUtils.drawRectOutline(context, x, y, width, HOTBAR_HEIGHT, 0xFF2244AA);
            }
            case TRANSPARENT -> {
                context.fill(x, y, x + width, y + HOTBAR_HEIGHT, 0x33000000);
            }
        }
    }

    private static void renderSlot(DrawContext context, MinecraftClient mc, HotbarStyle style,
                                    int x, int y, ItemStack stack, int slot, boolean selected, FTVConfig cfg) {
        if (selected) {
            context.fill(x, y, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x55FFFFFF);
            RenderUtils.drawRectOutline(context, x, y, SLOT_SIZE - 1, SLOT_SIZE - 1, 0xFFFFFFFF);
        } else if (style == HotbarStyle.VANILLA_PLUS) {
            RenderUtils.drawRectOutline(context, x, y, SLOT_SIZE - 1, SLOT_SIZE - 1, 0xFF333333);
        }

        if (!stack.isEmpty()) {
            context.drawItem(stack, x + 2, y + 2);
            context.drawItemInSlot(mc.textRenderer, stack, x + 2, y + 2);
        }

        if (cfg.hotbarSlotNumbers) {
            context.drawText(mc.textRenderer, String.valueOf(slot + 1), x + 2, y + 2, 0xAAAAAA, true);
        }

        if (cfg.hotbarDurabilityBars && !stack.isEmpty() && stack.isDamageable()) {
            int maxDmg = stack.getMaxDamage();
            int currentDmg = stack.getDamage();
            float fraction = 1f - (float) currentDmg / maxDmg;
            int barColor;

            if (cfg.hotbarDurabilityWarning && fraction < cfg.hotbarDurabilityWarnPercent / 100f) {
                long time = System.currentTimeMillis();
                barColor = (time / 500) % 2 == 0 ? 0xFFFF4444 : 0xFFFFFF44;
            } else {
                barColor = RenderUtils.getHpColor(fraction, 1f);
            }

            RenderUtils.drawBar(context, x + 2, y + SLOT_SIZE - 4, SLOT_SIZE - 5, 2,
                    fraction, 0xFF333333, barColor);
        }
    }
}
