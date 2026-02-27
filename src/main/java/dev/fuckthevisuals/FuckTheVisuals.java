package dev.fuckthevisuals;

import dev.fuckthevisuals.config.FTVConfig;
import dev.fuckthevisuals.event.RenderEventHandler;
import dev.fuckthevisuals.event.TickEventHandler;
import dev.fuckthevisuals.feature.autofish.AutoFishFeature;
import dev.fuckthevisuals.feature.autotool.AutoToolFeature;
import dev.fuckthevisuals.feature.waypoint.WaypointManager;
import dev.fuckthevisuals.keybind.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class FuckTheVisuals implements ClientModInitializer {

    public static final String MOD_ID = "fuckthevisuals";
    public static final String MOD_NAME = "FuckTheVisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static FuckTheVisuals instance;

    private FTVConfig config;
    private WaypointManager waypointManager;
    private AutoFishFeature autoFishFeature;
    private AutoToolFeature autoToolFeature;

    public static FuckTheVisuals getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        LOGGER.info("Initializing {}", MOD_NAME);

        config = FTVConfig.load();
        waypointManager = new WaypointManager();

        KeybindManager.register();

        autoFishFeature = new AutoFishFeature();
        autoToolFeature = new AutoToolFeature();

        RenderEventHandler.register();
        TickEventHandler.register();

        LOGGER.info("{} initialized successfully", MOD_NAME);
    }

    public FTVConfig getConfig() {
        return config;
    }

    public WaypointManager getWaypointManager() {
        return waypointManager;
    }

    public AutoFishFeature getAutoFishFeature() {
        return autoFishFeature;
    }

    public AutoToolFeature getAutoToolFeature() {
        return autoToolFeature;
    }
}
