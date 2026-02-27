package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.feature.targethud.TargetHudFeature;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

}
