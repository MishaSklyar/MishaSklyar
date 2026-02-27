package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.feature.hand.CustomHandFeature;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    ordinal = 0,
                    shift = At.Shift.AFTER),
            require = 0
    )
    private void ftv$onRenderFirstPersonItem(
            PlayerEntity player, float tickDelta, float pitch,
            Hand hand, float swingProgress, ItemStack item,
            float equipProgress, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (FuckTheVisuals.getInstance().getConfig().customHandEnabled) {
            CustomHandFeature.applyTransform(matrices);
        }
    }
}
