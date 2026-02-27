package dev.fuckthevisuals.keybind;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.feature.totemoffhand.TotemOffhandFeature;
import dev.fuckthevisuals.feature.waypoint.WaypointManager;
import dev.fuckthevisuals.feature.waypoint.WaypointScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class KeybindManager {

    public static KeyBinding addWaypoint;
    public static KeyBinding waypointManager;
    public static KeyBinding totemOffhand;
    public static KeyBinding toggleWaypoints;
    public static KeyBinding toggleHitboxes;
    public static KeyBinding toggleTargetHud;

    private KeybindManager() {}

    public static void register() {
        addWaypoint = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fuckthevisuals.key.add_waypoint",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "fuckthevisuals.key.category"
        ));

        waypointManager = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fuckthevisuals.key.waypoint_manager",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "fuckthevisuals.key.category"
        ));

        totemOffhand = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fuckthevisuals.key.totem_offhand",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "fuckthevisuals.key.category"
        ));

        toggleWaypoints = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fuckthevisuals.key.toggle_waypoints",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "fuckthevisuals.key.category"
        ));

        toggleHitboxes = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fuckthevisuals.key.toggle_hitboxes",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "fuckthevisuals.key.category"
        ));

        toggleTargetHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fuckthevisuals.key.toggle_targethud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "fuckthevisuals.key.category"
        ));
    }

    public static void handleKeybinds(MinecraftClient client) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();

        while (addWaypoint.wasPressed()) {
            if (cfg.waypointsEnabled && client.player != null) {
                WaypointManager wm = FuckTheVisuals.getInstance().getWaypointManager();
                wm.addWaypointAtPlayer(client.player);
            }
        }

        while (waypointManager.wasPressed()) {
            if (client.player != null) {
                client.setScreen(new WaypointScreen(null));
            }
        }

        while (totemOffhand.wasPressed()) {
            if (cfg.totemOffhandEnabled && client.player != null) {
                TotemOffhandFeature.moveTotemToOffhand(client);
            }
        }

        while (toggleWaypoints.wasPressed()) {
            cfg.waypointsEnabled = !cfg.waypointsEnabled;
            cfg.save();
        }

        while (toggleHitboxes.wasPressed()) {
            cfg.hitboxEnabled = !cfg.hitboxEnabled;
            cfg.save();
        }

        while (toggleTargetHud.wasPressed()) {
            cfg.targetHudEnabled = !cfg.targetHudEnabled;
            cfg.save();
        }
    }
}
