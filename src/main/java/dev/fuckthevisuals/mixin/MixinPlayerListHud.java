package dev.fuckthevisuals.mixin;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.feature.antibot.AntiBotFeature;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void ftv$modifyPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!FuckTheVisuals.getInstance().getConfig().antiBotEnabled) return;
        if (!FuckTheVisuals.getInstance().getConfig().antiBotHighlight) return;

        if (AntiBotFeature.isSuspectedBot(entry.getProfile().getId())) {
            Text original = cir.getReturnValue();
            cir.setReturnValue(Text.literal("\u00a7c[BOT] \u00a7r").append(original));
        }
    }
}
