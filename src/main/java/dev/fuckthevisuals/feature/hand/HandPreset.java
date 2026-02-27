package dev.fuckthevisuals.feature.hand;

public enum HandPreset {

    VANILLA(0f, 0f, 0f, 0f, 0f, 0f, 1f),
    OLD_PVP(0.56f, -0.52f, -0.71999997f, 45.0f, -15.0f, 0f, 0.7f),
    SMALL(0f, 0f, 0f, 0f, 0f, 0f, 0.6f),
    LARGE(0f, 0f, 0f, 0f, 0f, 0f, 1.4f),
    SIDE(0.4f, 0f, 0f, 0f, 0f, 0f, 1f),
    CUSTOM(0f, 0f, 0f, 0f, 0f, 0f, 1f);

    public final float offsetX;
    public final float offsetY;
    public final float offsetZ;
    public final float rotX;
    public final float rotY;
    public final float rotZ;
    public final float scale;

    HandPreset(float offsetX, float offsetY, float offsetZ,
               float rotX, float rotY, float rotZ, float scale) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public static HandPreset fromString(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return VANILLA;
        }
    }
}
