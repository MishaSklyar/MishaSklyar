package dev.fuckthevisuals.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.fuckthevisuals.FuckTheVisuals;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("fuckthevisuals.config.title"))
                    .setSavingRunnable(cfg::save);

            ConfigEntryBuilder eb = builder.entryBuilder();

            // ── Waypoints ────────────────────────────────────────────────
            ConfigCategory waypoints = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.waypoints"));
            waypoints.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.waypointsEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.waypointsEnabled = v).build());
            waypoints.addEntry(eb.startBooleanToggle(Text.literal("Beacon Beams"), cfg.waypointBeams)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.waypointBeams = v).build());
            waypoints.addEntry(eb.startBooleanToggle(Text.literal("Show Distance"), cfg.waypointDistance)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.waypointDistance = v).build());
            waypoints.addEntry(eb.startBooleanToggle(Text.literal("Show Labels"), cfg.waypointLabels)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.waypointLabels = v).build());
            waypoints.addEntry(eb.startIntField(Text.literal("Max Render Distance"), cfg.waypointMaxRenderDistance)
                    .setDefaultValue(2048).setMin(64).setMax(8192).setSaveConsumer(v -> cfg.waypointMaxRenderDistance = v).build());

            // ── Custom Hand ──────────────────────────────────────────────
            ConfigCategory hand = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.hand"));
            hand.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.customHandEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.customHandEnabled = v).build());
            hand.addEntry(eb.startSelector(Text.literal("Preset"), new String[]{"VANILLA", "OLD_PVP", "SMALL", "LARGE", "SIDE", "CUSTOM"}, cfg.handPreset)
                    .setDefaultValue("VANILLA").setSaveConsumer(v -> cfg.handPreset = v).build());
            hand.addEntry(eb.startFloatField(Text.literal("Scale"), cfg.handScale)
                    .setDefaultValue(1f).setMin(0.1f).setMax(3f).setSaveConsumer(v -> cfg.handScale = v).build());
            hand.addEntry(eb.startFloatField(Text.literal("Offset X"), cfg.handOffsetX)
                    .setDefaultValue(0f).setMin(-2f).setMax(2f).setSaveConsumer(v -> cfg.handOffsetX = v).build());
            hand.addEntry(eb.startFloatField(Text.literal("Offset Y"), cfg.handOffsetY)
                    .setDefaultValue(0f).setMin(-2f).setMax(2f).setSaveConsumer(v -> cfg.handOffsetY = v).build());
            hand.addEntry(eb.startFloatField(Text.literal("Offset Z"), cfg.handOffsetZ)
                    .setDefaultValue(0f).setMin(-2f).setMax(2f).setSaveConsumer(v -> cfg.handOffsetZ = v).build());

            // ── Target HUD ───────────────────────────────────────────────
            ConfigCategory targetHud = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.targethud"));
            targetHud.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.targetHudEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.targetHudEnabled = v).build());
            targetHud.addEntry(eb.startBooleanToggle(Text.literal("Show Status Effects"), cfg.targetHudShowEffects)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.targetHudShowEffects = v).build());
            targetHud.addEntry(eb.startBooleanToggle(Text.literal("Show Armor"), cfg.targetHudShowArmor)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.targetHudShowArmor = v).build());
            targetHud.addEntry(eb.startBooleanToggle(Text.literal("Show Player Face"), cfg.targetHudShowFace)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.targetHudShowFace = v).build());
            targetHud.addEntry(eb.startFloatField(Text.literal("Scale"), cfg.targetHudScale)
                    .setDefaultValue(1f).setMin(0.5f).setMax(2f).setSaveConsumer(v -> cfg.targetHudScale = v).build());

            // ── Watermark ────────────────────────────────────────────────
            ConfigCategory watermark = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.watermark"));
            watermark.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.watermarkEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.watermarkEnabled = v).build());
            watermark.addEntry(eb.startStrField(Text.literal("Text"), cfg.watermarkText)
                    .setDefaultValue("FuckTheVisuals").setSaveConsumer(v -> cfg.watermarkText = v).build());
            watermark.addEntry(eb.startSelector(Text.literal("Corner"), new String[]{"TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT"}, cfg.watermarkCorner)
                    .setDefaultValue("TOP_LEFT").setSaveConsumer(v -> cfg.watermarkCorner = v).build());
            watermark.addEntry(eb.startBooleanToggle(Text.literal("Shadow"), cfg.watermarkShadow)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.watermarkShadow = v).build());

            // ── Entity Hitboxes ──────────────────────────────────────────
            ConfigCategory hitbox = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.hitbox"));
            hitbox.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.hitboxEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.hitboxEnabled = v).build());
            hitbox.addEntry(eb.startBooleanToggle(Text.literal("Health Bars"), cfg.hitboxHealthBar)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.hitboxHealthBar = v).build());
            hitbox.addEntry(eb.startBooleanToggle(Text.literal("Only Mobs"), cfg.hitboxOnlyMobs)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.hitboxOnlyMobs = v).build());
            hitbox.addEntry(eb.startIntField(Text.literal("Max Render Distance"), cfg.hitboxMaxDistance)
                    .setDefaultValue(64).setMin(8).setMax(256).setSaveConsumer(v -> cfg.hitboxMaxDistance = v).build());

            // ── Breaking Animation ────────────────────────────────────────
            ConfigCategory breakAnim = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.breakanim"));
            breakAnim.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.breakAnimEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.breakAnimEnabled = v).build());
            breakAnim.addEntry(eb.startSelector(Text.literal("Style"), new String[]{"GRADIENT", "OUTLINE", "PIXELATE", "SHRINK", "COLOR_OVERLAY"}, cfg.breakAnimStyle)
                    .setDefaultValue("GRADIENT").setSaveConsumer(v -> cfg.breakAnimStyle = v).build());
            breakAnim.addEntry(eb.startFloatField(Text.literal("Alpha"), cfg.breakAnimAlpha)
                    .setDefaultValue(0.6f).setMin(0.1f).setMax(1f).setSaveConsumer(v -> cfg.breakAnimAlpha = v).build());

            // ── Better Chat ──────────────────────────────────────────────
            ConfigCategory chat = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.chat"));
            chat.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.betterChatEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.betterChatEnabled = v).build());
            chat.addEntry(eb.startBooleanToggle(Text.literal("Timestamps"), cfg.chatTimestamps)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.chatTimestamps = v).build());
            chat.addEntry(eb.startBooleanToggle(Text.literal("Compact Duplicates"), cfg.chatCompactDuplicates)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.chatCompactDuplicates = v).build());
            chat.addEntry(eb.startIntField(Text.literal("Chat Width"), cfg.chatWidth)
                    .setDefaultValue(320).setMin(160).setMax(640).setSaveConsumer(v -> cfg.chatWidth = v).build());
            chat.addEntry(eb.startIntField(Text.literal("Chat Height"), cfg.chatHeight)
                    .setDefaultValue(180).setMin(60).setMax(480).setSaveConsumer(v -> cfg.chatHeight = v).build());

            // ── Custom Hotbar ─────────────────────────────────────────────
            ConfigCategory hotbar = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.hotbar"));
            hotbar.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.customHotbarEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.customHotbarEnabled = v).build());
            hotbar.addEntry(eb.startSelector(Text.literal("Style"), new String[]{"VANILLA_PLUS", "MINIMAL", "CLASSIC", "MODERN", "TRANSPARENT"}, cfg.hotbarStyle)
                    .setDefaultValue("VANILLA_PLUS").setSaveConsumer(v -> cfg.hotbarStyle = v).build());
            hotbar.addEntry(eb.startBooleanToggle(Text.literal("Durability Bars"), cfg.hotbarDurabilityBars)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.hotbarDurabilityBars = v).build());
            hotbar.addEntry(eb.startBooleanToggle(Text.literal("Slot Numbers"), cfg.hotbarSlotNumbers)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.hotbarSlotNumbers = v).build());
            hotbar.addEntry(eb.startBooleanToggle(Text.literal("Durability Warning"), cfg.hotbarDurabilityWarning)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.hotbarDurabilityWarning = v).build());
            hotbar.addEntry(eb.startIntField(Text.literal("Durability Warn %"), cfg.hotbarDurabilityWarnPercent)
                    .setDefaultValue(20).setMin(5).setMax(50).setSaveConsumer(v -> cfg.hotbarDurabilityWarnPercent = v).build());

            // ── QoL HUD ───────────────────────────────────────────────────
            ConfigCategory qolHud = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.qolhud"));
            qolHud.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.qolHudEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.qolHudEnabled = v).build());
            qolHud.addEntry(eb.startBooleanToggle(Text.literal("Coordinates"), cfg.qolCoords)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.qolCoords = v).build());
            qolHud.addEntry(eb.startBooleanToggle(Text.literal("FPS"), cfg.qolFps)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.qolFps = v).build());
            qolHud.addEntry(eb.startBooleanToggle(Text.literal("Ping"), cfg.qolPing)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.qolPing = v).build());
            qolHud.addEntry(eb.startBooleanToggle(Text.literal("Direction"), cfg.qolDirection)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.qolDirection = v).build());
            qolHud.addEntry(eb.startBooleanToggle(Text.literal("Custom Crosshair"), cfg.qolCrosshair)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.qolCrosshair = v).build());
            qolHud.addEntry(eb.startSelector(Text.literal("Corner"), new String[]{"TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT"}, cfg.qolCorner)
                    .setDefaultValue("BOTTOM_LEFT").setSaveConsumer(v -> cfg.qolCorner = v).build());

            // ── Anti-Bot ──────────────────────────────────────────────────
            ConfigCategory antiBot = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.antibot"));
            antiBot.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.antiBotEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.antiBotEnabled = v).build());
            antiBot.addEntry(eb.startBooleanToggle(Text.literal("Highlight Bots"), cfg.antiBotHighlight)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.antiBotHighlight = v).build());

            // ── Auto Fish ─────────────────────────────────────────────────
            ConfigCategory autoFish = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.autofish"));
            autoFish.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.autoFishEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.autoFishEnabled = v).build());
            autoFish.addEntry(eb.startIntField(Text.literal("Reel/Cast Delay (ms)"), cfg.autoFishDelay)
                    .setDefaultValue(500).setMin(100).setMax(3000).setSaveConsumer(v -> cfg.autoFishDelay = v).build());

            // ── Auto Tool ─────────────────────────────────────────────────
            ConfigCategory autoTool = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.autotool"));
            autoTool.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.autoToolEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.autoToolEnabled = v).build());

            // ── GUI Move ──────────────────────────────────────────────────
            ConfigCategory guiMove = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.guimove"));
            guiMove.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.guiMoveEnabled)
                    .setDefaultValue(false).setSaveConsumer(v -> cfg.guiMoveEnabled = v).build());

            // ── Totem Offhand ─────────────────────────────────────────────
            ConfigCategory totem = builder.getOrCreateCategory(Text.translatable("fuckthevisuals.category.totemoffhand"));
            totem.addEntry(eb.startBooleanToggle(Text.literal("Enabled"), cfg.totemOffhandEnabled)
                    .setDefaultValue(true).setSaveConsumer(v -> cfg.totemOffhandEnabled = v).build());

            return builder.build();
        };
    }
}
