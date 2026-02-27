package dev.fuckthevisuals.feature.chat;

import dev.fuckthevisuals.FuckTheVisuals;
import dev.fuckthevisuals.config.FTVConfig;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public final class BetterChatFeature {

    private static final Map<String, DuplicateEntry> duplicateCounts = new HashMap<>();

    private BetterChatFeature() {}

    public static Text processMessage(Text message) {
        FTVConfig cfg = FuckTheVisuals.getInstance().getConfig();

        String rawString = message.getString();
        MutableText result = Text.empty();

        if (cfg.chatTimestamps) {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern(cfg.chatTimestampFormat));
            result.append(Text.literal("§8[" + timestamp + "] §r"));
        }

        if (cfg.chatCompactDuplicates) {
            DuplicateEntry entry = duplicateCounts.get(rawString);
            long now = System.currentTimeMillis();

            if (entry != null && (now - entry.lastTime) < 30000) {
                entry.count++;
                entry.lastTime = now;
                result.append(message);
                result.append(Text.literal(" §8(x" + entry.count + ")"));
                return result;
            } else {
                duplicateCounts.put(rawString, new DuplicateEntry(now));
            }
        }

        result.append(message);
        return result;
    }

    public static void clearDuplicates() {
        long now = System.currentTimeMillis();
        duplicateCounts.entrySet().removeIf(e -> (now - e.getValue().lastTime) > 60000);
    }

    private static class DuplicateEntry {
        int count = 1;
        long lastTime;

        DuplicateEntry(long time) {
            this.lastTime = time;
        }
    }
}
