package dev.fuckthevisuals.feature.hand;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

public final class CustomHandFeature {

    private CustomHandFeature() {}

    public static void applyTransform(MatrixStack matrices) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
        if (!cfg.customHandEnabled) return;

        HandPreset preset = HandPreset.fromString(cfg.handPreset);

        float ox, oy, oz, rx, ry, rz, scale;

        if (preset == HandPreset.CUSTOM) {
            ox = cfg.handOffsetX;
            oy = cfg.handOffsetY;
            oz = cfg.handOffsetZ;
            rx = cfg.handRotX;
            ry = cfg.handRotY;
            rz = cfg.handRotZ;
            scale = cfg.handScale;
        } else {
            ox = preset.offsetX;
            oy = preset.offsetY;
            oz = preset.offsetZ;
            rx = preset.rotX;
            ry = preset.rotY;
            rz = preset.rotZ;
            scale = preset.scale;
        }

        matrices.translate(ox, oy, oz);

        if (rx != 0) matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(rx)));
        if (ry != 0) matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(ry)));
        if (rz != 0) matrices.multiply(new Quaternionf().rotateZ((float) Math.toRadians(rz)));

        if (scale != 1f) matrices.scale(scale, scale, scale);
    }
}
