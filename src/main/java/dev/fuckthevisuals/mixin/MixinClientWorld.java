package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    @Inject(method = "removeEntity", at = @At("HEAD"))
    private void ftv$onRemoveEntity(int entityId, Entity.RemovalReason reason, CallbackInfo ci) {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player == null) return;
        ClientWorld world = (ClientWorld)(Object)this;
        Entity entity = world.getEntityById(entityId);
        if (entity instanceof FishingBobberEntity bobber) {
            Entity owner = bobber.getOwner();
            if (owner == mc.player && reason == Entity.RemovalReason.KILLED) {
                FuckTheVisuals.getInstance().getAutoFishFeature().onBobberCaught();
            }
        }
    }
}
