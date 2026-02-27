package dev.fuckthevisuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.fuckthevisuals.FuckTheVisuals;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class FTVConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fuckthevisuals.json");

    // ── Waypoints ────────────────────────────────────────────────────────
    public boolean waypointsEnabled = true;
    public boolean waypointBeams = true;
    public boolean waypointDistance = true;
    public boolean waypointLabels = true;
    public int waypointMaxRenderDistance = 2048;
    public float waypointBeamAlpha = 0.6f;

    // ── Custom Hand ──────────────────────────────────────────────────────
    public boolean customHandEnabled = false;
    public String handPreset = "VANILLA";
    public float handOffsetX = 0f;
    public float handOffsetY = 0f;
    public float handOffsetZ = 0f;
    public float handRotX = 0f;
    public float handRotY = 0f;
    public float handRotZ = 0f;
    public float handScale = 1f;

    // ── Target HUD ───────────────────────────────────────────────────────
    public boolean targetHudEnabled = true;
    public int targetHudX = 10;
    public int targetHudY = 10;
    public boolean targetHudShowEffects = true;
    public boolean targetHudShowArmor = true;
    public boolean targetHudShowFace = true;
    public float targetHudScale = 1f;

    // ── Watermark ────────────────────────────────────────────────────────
    public boolean watermarkEnabled = true;
    public String watermarkText = "FuckTheVisuals";
    public String watermarkCorner = "TOP_LEFT";
    public int watermarkColor = 0xFFFFFFFF;
    public boolean watermarkShadow = true;
    public float watermarkScale = 1f;

    // ── Entity Hitboxes ──────────────────────────────────────────────────
    public boolean hitboxEnabled = false;
    public boolean hitboxHealthBar = true;
    public int hitboxColor = 0xFF00FF00;
    public int hitboxPlayerColor = 0xFFFF0000;
    public int hitboxMobColor = 0xFFFFFF00;
    public int hitboxMaxDistance = 64;
    public boolean hitboxOnlyMobs = false;

    // ── Breaking Animation ───────────────────────────────────────────────
    public boolean breakAnimEnabled = false;
    public String breakAnimStyle = "GRADIENT";
    public int breakAnimColor = 0xFFFF5500;
    public float breakAnimAlpha = 0.6f;

    // ── Better Chat ──────────────────────────────────────────────────────
    public boolean betterChatEnabled = true;
    public boolean chatTimestamps = true;
    public boolean chatCompactDuplicates = true;
    public int chatWidth = 320;
    public int chatHeight = 180;
    public String chatTimestampFormat = "HH:mm";

    // ── Custom Hotbar ────────────────────────────────────────────────────
    public boolean customHotbarEnabled = false;
    public String hotbarStyle = "VANILLA_PLUS";
    public boolean hotbarDurabilityBars = true;
    public boolean hotbarSlotNumbers = false;
    public boolean hotbarDurabilityWarning = true;
    public int hotbarDurabilityWarnPercent = 20;
    public float hotbarScale = 1f;

    // ── QoL HUD ──────────────────────────────────────────────────────────
    public boolean qolHudEnabled = true;
    public boolean qolCoords = true;
    public boolean qolFps = true;
    public boolean qolPing = true;
    public boolean qolDirection = true;
    public boolean qolCrosshair = true;
    public String qolCorner = "BOTTOM_LEFT";
    public int qolColor = 0xFFFFFFFF;
    public boolean qolShadow = true;

    // ── Anti-Bot ─────────────────────────────────────────────────────────
    public boolean antiBotEnabled = false;
    public boolean antiBotHighlight = true;
    public int antiBotColor = 0xFFFF0000;

    // ── Auto Fish ────────────────────────────────────────────────────────
    public boolean autoFishEnabled = false;
    public int autoFishDelay = 500;

    // ── Auto Tool ────────────────────────────────────────────────────────
    public boolean autoToolEnabled = true;

    // ── GUI Move ─────────────────────────────────────────────────────────
    public boolean guiMoveEnabled = false;

    // ── Totem Offhand ────────────────────────────────────────────────────
    public boolean totemOffhandEnabled = true;

    public void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            FuckTheVisuals.LOGGER.error("Failed to save config", e);
        }
    }

    public static FTVConfig load() {
        File file = CONFIG_PATH.toFile();
        if (!file.exists()) {
            FTVConfig cfg = new FTVConfig();
            cfg.save();
            return cfg;
        }
        try (Reader reader = new FileReader(file)) {
            FTVConfig cfg = GSON.fromJson(reader, FTVConfig.class);
            if (cfg == null) {
                cfg = new FTVConfig();
            }
            cfg.save();
            return cfg;
        } catch (IOException e) {
            FuckTheVisuals.LOGGER.error("Failed to load config", e);
            return new FTVConfig();
        }
    }
}
