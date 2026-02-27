package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.feature.guimove.GuiMoveFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class MixinHandledScreen {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void ftv$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!FuckTheVisuals.getInstance().getConfig().guiMoveEnabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        net.minecraft.client.option.KeyBinding[] movementKeys = {
                mc.options.forwardKey, mc.options.backKey,
                mc.options.leftKey, mc.options.rightKey,
                mc.options.jumpKey, mc.options.sneakKey
        };

        for (net.minecraft.client.option.KeyBinding key : movementKeys) {
            if (key.matchesKey(keyCode, scanCode)) {
                return;
            }
        }
    }
}
