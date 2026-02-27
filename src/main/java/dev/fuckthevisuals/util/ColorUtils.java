package dev.fuckthevisuals.util;

public final class ColorUtils {

    private ColorUtils() {}

    public static int rgba(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public static float red(int color) {
        return ((color >> 16) & 0xFF) / 255f;
    }

    public static float green(int color) {
        return ((color >> 8) & 0xFF) / 255f;
    }

    public static float blue(int color) {
        return (color & 0xFF) / 255f;
    }

    public static float alpha(int color) {
        return ((color >> 24) & 0xFF) / 255f;
    }

    public static int lerpColor(int colorA, int colorB, float t) {
        float r = lerp(red(colorA), red(colorB), t);
        float g = lerp(green(colorA), green(colorB), t);
        float b = lerp(blue(colorA), blue(colorB), t);
        float a = lerp(alpha(colorA), alpha(colorB), t);
        return rgba((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
    }

    public static int hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = v - c;
        float r, g, b;
        if (h < 60) { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }
        return rgba((int)((r + m) * 255), (int)((g + m) * 255), (int)((b + m) * 255), 255);
    }

    public static int rainbowColor(long timeMs, float speed) {
        float hue = ((timeMs * speed) % 360 + 360) % 360;
        return hsvToRgb(hue, 1f, 1f);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
