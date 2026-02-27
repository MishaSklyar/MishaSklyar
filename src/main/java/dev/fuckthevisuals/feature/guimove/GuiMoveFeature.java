package dev.fuckthevisuals.feature.guimove;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class GuiMoveFeature {

    private GuiMoveFeature() {}

    public static void applyMovementKeys(MinecraftClient mc) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        if (!cfg.guiMoveEnabled) return;
        if (mc.currentScreen == null) return;

        long handle = mc.getWindow().getHandle();

        setMovementKey(mc.options.forwardKey, handle);
        setMovementKey(mc.options.backKey, handle);
        setMovementKey(mc.options.leftKey, handle);
        setMovementKey(mc.options.rightKey, handle);
        setMovementKey(mc.options.jumpKey, handle);
        setMovementKey(mc.options.sneakKey, handle);
    }

    private static void setMovementKey(KeyBinding binding, long windowHandle) {
        InputUtil.Key key = binding.getDefaultKey();
        boolean pressed = InputUtil.isKeyPressed(windowHandle, key.getCode());
        KeyBinding.setKeyPressed(key, pressed);
    }
}
