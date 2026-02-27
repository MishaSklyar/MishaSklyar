package dev.fuckthevisuals.feature.hotbar;

public enum HotbarStyle {
    VANILLA_PLUS,
    MINIMAL,
    CLASSIC,
    MODERN,
    TRANSPARENT;

    public static HotbarStyle fromString(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return VANILLA_PLUS;
        }
    }
}
