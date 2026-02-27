package dev.fuckthevisuals.feature.breakanim;

public enum BreakAnimStyle {
    GRADIENT,
    OUTLINE,
    PIXELATE,
    SHRINK,
    COLOR_OVERLAY;

    public static BreakAnimStyle fromString(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GRADIENT;
        }
    }
}
