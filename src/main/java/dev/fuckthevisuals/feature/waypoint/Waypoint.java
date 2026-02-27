package dev.fuckthevisuals.feature.waypoint;

import java.util.UUID;

public class Waypoint {

    private String id;
    private String name;
    private double x;
    private double y;
    private double z;
    private String dimension;
    private int color;
    private boolean enabled;

    public Waypoint() {
        this.id = UUID.randomUUID().toString();
        this.name = "Waypoint";
        this.dimension = "minecraft:overworld";
        this.color = 0xFF4488FF;
        this.enabled = true;
    }

    public Waypoint(String name, double x, double y, double z, String dimension, int color) {
        this();
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.color = color;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
