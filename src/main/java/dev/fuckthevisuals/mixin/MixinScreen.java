package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.feature.guimove.GuiMoveFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {

    @Inject(method = "tick", at = @At("HEAD"))
    private void ftv$onScreenTick(CallbackInfo ci) {
        if (FuckTheVisuals.getInstance().getConfig().guiMoveEnabled) {
            GuiMoveFeature.applyMovementKeys(MinecraftClient.getInstance());
        }
    }
}
