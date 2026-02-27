package dev.fuckthevisuals.feature.waypoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.fuckthevisuals.FuckTheVisuals;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WaypointManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path WAYPOINTS_PATH = FabricLoader.getInstance().getConfigDir().resolve("fuckthevisuals_waypoints.json");
    private static final Type LIST_TYPE = new TypeToken<List<Waypoint>>() {}.getType();

    private final List<Waypoint> waypoints = new ArrayList<>();

    public WaypointManager() {
        load();
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
        save();
    }

    public void removeWaypoint(String id) {
        waypoints.removeIf(w -> w.getId().equals(id));
        save();
    }

    public void addWaypointAtPlayer(ClientPlayerEntity player) {
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        int x = (int) player.getX();
        int y = (int) player.getY();
        int z = (int) player.getZ();
        String name = String.format("WP %d,%d,%d", x, y, z);
        Waypoint wp = new Waypoint(name, player.getX(), player.getY(), player.getZ(), dimension, 0xFF4488FF);
        addWaypoint(wp);
        player.sendMessage(Text.translatable("fuckthevisuals.waypoint.added", name), true);
    }

    public List<Waypoint> getActiveWaypointsForDimension(String dimension) {
        List<Waypoint> result = new ArrayList<>();
        for (Waypoint wp : waypoints) {
            if (wp.isEnabled() && wp.getDimension().equals(dimension)) {
                result.add(wp);
            }
        }
        return result;
    }

    public void save() {
        try (Writer writer = new FileWriter(WAYPOINTS_PATH.toFile())) {
            GSON.toJson(waypoints, writer);
        } catch (IOException e) {
            FuckTheVisuals.LOGGER.error("Failed to save waypoints", e);
        }
    }

    private void load() {
        File file = WAYPOINTS_PATH.toFile();
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            List<Waypoint> loaded = GSON.fromJson(reader, LIST_TYPE);
            if (loaded != null) {
                waypoints.addAll(loaded);
            }
        } catch (IOException e) {
            FuckTheVisuals.LOGGER.error("Failed to load waypoints", e);
        }
    }
}
