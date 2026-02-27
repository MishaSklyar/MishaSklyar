package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.feature.autotool.AutoToolFeature;
import dev.fuckthevisuals.feature.targethud.TargetHudFeature;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void ftv$onAttackEntity(net.minecraft.entity.player.PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target instanceof LivingEntity living && FuckTheVisuals.getInstance().getConfig().targetHudEnabled) {
            TargetHudFeature.setTarget(living);
        }
    }

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void ftv$onUpdateBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        BlockState state = mc.world.getBlockState(pos);
        FuckTheVisuals.getInstance().getAutoToolFeature().onStartMining(state);
    }

    @Inject(method = "cancelBlockBreaking", at = @At("HEAD"))
    private void ftv$onCancelBreaking(CallbackInfo ci) {
        FuckTheVisuals.getInstance().getAutoToolFeature().onStopMining();
    }
}
