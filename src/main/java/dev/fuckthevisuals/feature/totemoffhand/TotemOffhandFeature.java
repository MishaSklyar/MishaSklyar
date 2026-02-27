package dev.fuckthevisuals.feature.totemoffhand;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public final class TotemOffhandFeature {

    private static final int OFFHAND_SLOT = 45;

    private TotemOffhandFeature() {}

    public static void moveTotemToOffhand(MinecraftClient mc) {
        if (mc.player == null || mc.interactionManager == null) return;

        if (!mc.player.getOffHandStack().isEmpty() &&
                mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            mc.player.sendMessage(Text.translatable("fuckthevisuals.totem.moved"), true);
            return;
        }

        int totemSlot = -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot == -1) {
            mc.player.sendMessage(Text.translatable("fuckthevisuals.totem.none"), true);
            return;
        }

        int syncId = mc.player.currentScreenHandler.syncId;

        int networkSlot;
        if (totemSlot < 9) {
            networkSlot = totemSlot + 36;
        } else {
            networkSlot = totemSlot;
        }

        mc.interactionManager.clickSlot(syncId, OFFHAND_SLOT, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, networkSlot, 0, SlotActionType.PICKUP, mc.player);

        if (!mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            mc.interactionManager.clickSlot(syncId, OFFHAND_SLOT, 0, SlotActionType.PICKUP, mc.player);
        }

        mc.player.sendMessage(Text.translatable("fuckthevisuals.totem.moved"), true);
    }
}
