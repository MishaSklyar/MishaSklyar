package dev.fuckthevisuals.util;

import net.minecraft.util.math.Vec3d;

public final class MathUtils {

    private MathUtils() {}

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double distanceTo(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double distanceHorizontal(double x1, double z1, double x2, double z2) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static String formatDistance(double dist) {
        if (dist < 1000) {
            return String.format("%.0fm", dist);
        }
        return String.format("%.1fkm", dist / 1000.0);
    }

    public static String getDirectionName(float yaw) {
        yaw = ((yaw % 360) + 360) % 360;
        if (yaw < 22.5 || yaw >= 337.5) return "S";
        if (yaw < 67.5) return "SW";
        if (yaw < 112.5) return "W";
        if (yaw < 157.5) return "NW";
        if (yaw < 202.5) return "N";
        if (yaw < 247.5) return "NE";
        if (yaw < 292.5) return "E";
        return "SE";
    }

    public static Vec3d lerp(Vec3d a, Vec3d b, double t) {
        return new Vec3d(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }

    public static int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }
}
