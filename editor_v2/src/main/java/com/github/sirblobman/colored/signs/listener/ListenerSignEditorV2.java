package com.github.sirblobman.colored.signs.listener;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.HangingSign;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent e) {
        printDebug("Detected PlayerInteractEvent.");

        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            printDebug("Action is not right click block, ignoring.");
            return;
        }

        EquipmentSlot hand = e.getHand();
        if (hand != EquipmentSlot.HAND) {
            printDebug("Hand is not main, ignoring.");
            return;
        }

        Block block = e.getClickedBlock();
        if (block == null) {
            printDebug("Clicked block is null, ignoring.");
            return;
        }

        BlockState blockState = block.getState();
        if (!(blockState instanceof Sign sign)) {
            printDebug("Clicked block is not sign, ignoring.");
            return;
        }

        Player player = e.getPlayer();
        if (!player.isSneaking()) {
            e.setUseInteractedBlock(Event.Result.DENY);
            printDebug("Player is not sneaking, preventing vanilla edit and ignoring.");
            return;
        }

        if (!isEnabled() || !hasPermission(player)) {
            e.setUseInteractedBlock(Event.Result.DENY);
            printDebug("Editor not enabled or player does not have permission, preventing vanilla edit and ignoring.");
            return;
        }

        Plugin plugin = getPlugin();
        PersistentDataContainer dataContainer = sign.getPersistentDataContainer();

        Side side = getSignSide(e);
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
        }

        sign.update(true, true);
        e.setUseInteractedBlock(Event.Result.DENY);

        Runnable task = () -> {
            BlockState newState = block.getState();
            if (!(newState instanceof Sign newSign)) {
                printDebug("Updated block is not sign, ignoring.");
                return;
            }

            printDebug("Opening new sign block.");
            player.openSign(newSign, side);
        };

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, task, 2L);
    }

    private @NotNull Side getSignSide(@NotNull PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) {
            return Side.FRONT;
        }

        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            BlockFace clickedFace = e.getBlockFace();
            printDebug("Clicked Face: " + clickedFace);
            BlockFace facing = directional.getFacing();
            if (facing == clickedFace) {
                return Side.FRONT;
            }

            return Side.BACK;
        }

        if (blockData instanceof Rotatable rotatable) {
            BlockFace clickedFace = e.getBlockFace();
            BlockFace rotation = rotatable.getRotation();
            if (clickedFace == rotation || rotation.name().contains(clickedFace.name())) {
                return Side.FRONT;
            }

            return Side.BACK;
        }

        return Side.FRONT;
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
