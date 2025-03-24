package com.github.sirblobman.colored.signs.listener;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.colored.signs.ColoredSigns;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;

public final class ListenerSignEditorV2 extends ColoredSignsListener {
    private final StringArrayTypeV2 stringArrayType;

    public ListenerSignEditorV2(@NotNull ColoredSigns coloredSigns) {
        super(coloredSigns);
        this.stringArrayType = new StringArrayTypeV2(coloredSigns.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        Block block = e.getBlock();
        String[] rawLines = e.getLines();
        Plugin plugin = getPlugin();

        Runnable task = () -> {
            BlockState blockState = block.getState();
            if (!(blockState instanceof Sign sign)) {
                return;
            }

            Side side = e.getSide();
            NamespacedKey rawLinesKey = new NamespacedKey(plugin, "raw-lines-" + side.name().toLowerCase(Locale.US));
            PersistentDataContainer dataContainer = sign.getPersistentDataContainer();
            dataContainer.set(rawLinesKey, getStringArrayType(), rawLines);
            sign.update(true, true);
        };

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(plugin, task);
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onOpenSign(PlayerSignOpenEvent e) {
        if (!isEnabled()) {
            return;
        }

        PlayerSignOpenEvent.Cause cause = e.getCause();
        if (cause != PlayerSignOpenEvent.Cause.INTERACT) {
            return;
        }

        Player player = e.getPlayer();
        if (!hasPermission(player)) {
            printDebug("Player does not have permission, preventing edit.");
            e.setCancelled(true);
            return;
        }

        Side side = e.getSide();
        Sign sign = e.getSign();
        Plugin plugin = getPlugin();
        PersistentDataContainer dataContainer = sign.getPersistentDataContainer();

        printDebug("Detected click on sign side: " + side);
        SignSide signSide = sign.getSide(side);
        NamespacedKey rawLinesKey = new NamespacedKey(plugin, "raw-lines-" + side.name().toLowerCase(Locale.US));
        String[] rawLines = dataContainer.get(rawLinesKey, getStringArrayType());
        if (rawLines != null) {
            char colorChar = getColorCharacter();
            for (int i = 0; i < 4; i++) {
                String rawLine = rawLines[i];
                printDebug("Raw Line " + i + ": " + rawLine);

                @SuppressWarnings("UnnecessaryUnicodeEscape")
                String fixed = rawLine.replace('\u00A7', colorChar);
                String fixedHex = fixHexColors(fixed);
                signSide.setLine(i, fixedHex);
                printDebug("Fixed Line: " + fixedHex);
            }

            dataContainer.remove(rawLinesKey);
            sign.update(true, true);
            e.setCancelled(true);

            player.closeInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.openSign(sign, side), 2L);
        }
    }

    private @NotNull StringArrayTypeV2 getStringArrayType() {
        return this.stringArrayType;
    }

    private boolean isEnabled() {
        ColoredSignsConfiguration configuration = getConfiguration();
        return configuration.isEnableSignEditor();
    }

    private boolean hasPermission(@NotNull Player player) {
        ColoredSignsConfiguration configuration = getConfiguration();
        if (configuration.isPermissionMode()) {
            return player.hasPermission("signs.edit");
        }

        return true;
    }

    private char getColorCharacter() {
        ColoredSignsConfiguration configuration = getConfiguration();
        return configuration.getColorCharacter();
    }

    private @NotNull Pattern getRgbPatternFinder() {
        char colorChar = getColorCharacter();
        String colorCharString = Character.toString(colorChar);
        String colorCharPattern = Pattern.quote(colorCharString);

        String patternString = (colorCharPattern + "x" + "((" + colorCharPattern + "[a-fA-F\\d]){6})");
        return Pattern.compile(patternString);
    }

    private @NotNull String fixHexColors(@NotNull String text) {
        Pattern pattern = getRgbPatternFinder();
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        char colorChar = getColorCharacter();

        while (matcher.find()) {
            String hexWithAnd = matcher.group(1);
            String hexPart = hexWithAnd.replace("&", "");
            matcher.appendReplacement(builder, colorChar + "#" + hexPart);
        }

        matcher.appendTail(builder);
        return builder.toString();
    }
}
