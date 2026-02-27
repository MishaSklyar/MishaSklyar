package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public class MixinFishingBobberEntity {

    @Shadow
    private int hookCountdown;

    @Inject(method = "tick", at = @At("HEAD"))
    private void ftv$onFishingTick(CallbackInfo ci) {
        FuckTheVisuals.getInstance().getAutoFishFeature().tick();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ftv$onFishingTickEnd(CallbackInfo ci) {
        if (hookCountdown > 0) {
            FuckTheVisuals.getInstance().getAutoFishFeature().onBobberCaught();
        }
    }
}
