package dev.fuckthevisuals.event;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.feature.hitbox.EntityHitboxFeature;
import dev.fuckthevisuals.feature.hotbar.CustomHotbarFeature;
import dev.fuckthevisuals.feature.qolhud.QolHudFeature;
import dev.fuckthevisuals.feature.targethud.TargetHudFeature;
import dev.fuckthevisuals.feature.watermark.WatermarkFeature;
import dev.fuckthevisuals.feature.waypoint.WaypointRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class RenderEventHandler {

    private RenderEventHandler() {}

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDeltaManager) -> {
            FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
            float tickDelta = tickDeltaManager.getTickDelta(true);

            if (cfg.watermarkEnabled) {
                WatermarkFeature.render(drawContext);
            }
            if (cfg.qolHudEnabled) {
                QolHudFeature.render(drawContext);
            }
            if (cfg.targetHudEnabled) {
                TargetHudFeature.render(drawContext, tickDelta);
            }
            if (cfg.customHotbarEnabled) {
                CustomHotbarFeature.render(drawContext);
            }
        });

        WorldRenderEvents.END.register(context -> {
            FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();

            if (cfg.waypointsEnabled) {
                WaypointRenderer.render(context);
            }
            if (cfg.hitboxEnabled) {
                EntityHitboxFeature.renderHitboxes(context);
            }
        });
    }
}
