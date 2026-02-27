package dev.fuckthevisuals.feature.waypoint;

import dev.fuckthevisuals.FuckTheVisuals;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;

public class WaypointScreen extends Screen {

    private final Screen parent;
    private WaypointManager manager;
    private List<Waypoint> waypoints;
    private int scrollOffset = 0;
    private static final int ENTRY_HEIGHT = 24;
    private static final int LIST_TOP = 50;
    private static final int LIST_BOTTOM_MARGIN = 60;

    public WaypointScreen(Screen parent) {
        super(Text.translatable("fuckthevisuals.waypoint.manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        manager = FuckTheVisuals.getInstance().getWaypointManager();
        waypoints = manager.getWaypoints();

        addDrawableChild(ButtonWidget.builder(Text.translatable("fuckthevisuals.waypoint.new"), btn -> {
            if (client != null && client.player != null) {
                manager.addWaypointAtPlayer(client.player);
                waypoints = manager.getWaypoints();
                clearAndInit();
            }
        }).dimensions(this.width / 2 - 155, this.height - 50, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, btn -> {
            if (client != null) client.setScreen(parent);
        }).dimensions(this.width / 2 + 5, this.height - 50, 150, 20).build());

        int listBottom = this.height - LIST_BOTTOM_MARGIN;
        int visibleCount = (listBottom - LIST_TOP) / ENTRY_HEIGHT;
        int clampedOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, waypoints.size() - visibleCount)));

        for (int i = 0; i < visibleCount && (i + clampedOffset) < waypoints.size(); i++) {
            Waypoint wp = waypoints.get(i + clampedOffset);
            int y = LIST_TOP + i * ENTRY_HEIGHT;
            final int idx = i + clampedOffset;

            addDrawableChild(ButtonWidget.builder(Text.literal(wp.isEnabled() ? "✓" : "✗"), btn -> {
                wp.setEnabled(!wp.isEnabled());
                manager.save();
                clearAndInit();
            }).dimensions(this.width / 2 - 155, y, 20, 20).build());

            addDrawableChild(ButtonWidget.builder(Text.translatable("fuckthevisuals.waypoint.delete"), btn -> {
                manager.removeWaypoint(wp.getId());
                waypoints = manager.getWaypoints();
                clearAndInit();
            }).dimensions(this.width / 2 + 110, y, 50, 20).build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        int listBottom = this.height - LIST_BOTTOM_MARGIN;
        int visibleCount = (listBottom - LIST_TOP) / ENTRY_HEIGHT;
        int clampedOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, waypoints.size() - visibleCount)));

        for (int i = 0; i < visibleCount && (i + clampedOffset) < waypoints.size(); i++) {
            Waypoint wp = waypoints.get(i + clampedOffset);
            int y = LIST_TOP + i * ENTRY_HEIGHT;

            context.fill(this.width / 2 - 130, y, this.width / 2 + 105, y + 18, 0x44000000);

            String label = String.format("%s  §7(%.0f, %.0f, %.0f) %s",
                    wp.getName(), wp.getX(), wp.getY(), wp.getZ(),
                    wp.getDimension().replace("minecraft:", ""));

            context.drawTextWithShadow(this.textRenderer, label,
                    this.width / 2 - 125, y + 5, wp.getColor() | 0xFF000000);
        }

        if (waypoints.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("No waypoints yet. Press B in-world to add one."),
                    this.width / 2, this.height / 2, 0xAAAAAA);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0, scrollOffset - (int) verticalAmount);
        clearAndInit();
        return true;
    }
}
