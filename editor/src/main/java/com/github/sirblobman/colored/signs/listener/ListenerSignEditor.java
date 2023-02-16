package com.github.sirblobman.colored.signs.listener;

import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class ListenerSignEditor implements Listener {
    private final JavaPlugin plugin;
    private final StringArrayType stringArrayType;

    public ListenerSignEditor(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
        this.stringArrayType = new StringArrayType(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        Block block = e.getBlock();
        String[] rawLines = e.getLines();
        Runnable task = () -> {
            BlockState blockState = block.getState();
            if (!(blockState instanceof Sign sign)) {
                return;
            }

            PersistentDataContainer dataContainer = sign.getPersistentDataContainer();
            NamespacedKey rawLinesKey = new NamespacedKey(this.plugin, "raw-lines");
            dataContainer.set(rawLinesKey, getStringArrayType(), rawLines);
            sign.update(true, true);
        };

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(this.plugin, task);
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

        Player player = e.getPlayer();
        if (!player.isSneaking()) {
            printDebug("Player is not sneaking, ignoring.");
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

        if (!isEnabled() || !hasPermission(player)) {
            printDebug("Editor not enabled or player does not have permission, ignoring.");
            return;
        }

        NamespacedKey rawLinesKey = new NamespacedKey(this.plugin, "raw-lines");
        PersistentDataContainer dataContainer = sign.getPersistentDataContainer();
        String[] rawLines = dataContainer.get(rawLinesKey, getStringArrayType());
        if (rawLines != null) {
            char colorChar = getColorCharacter();
            for (int i = 0; i < 4; i++) {
                String rawLine = rawLines[i];
                printDebug("Raw Line " + i + ": " + rawLine);

                String fixed = rawLine.replace('\u00A7', colorChar);
                String fixedHex = fixHexColors(fixed);
                sign.setLine(i, fixedHex);
                printDebug("Fixed Line: " + fixedHex);
            }

            sign.update(true, true);
        }

        JavaPlugin plugin = getPlugin();
        Runnable task = () -> {
            BlockState newState = block.getState();
            if (!(newState instanceof Sign newSign)) {
                printDebug("Updated block is not sign, ignoring.");
                return;
            }

            printDebug("Opening new sign block.");
            player.openSign(newSign);
        };

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, task, 2L);
    }

    public void register() {
        JavaPlugin plugin = getPlugin();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    private JavaPlugin getPlugin() {
        return this.plugin;
    }

    private StringArrayType getStringArrayType() {
        return this.stringArrayType;
    }

    private FileConfiguration getConfiguration() {
        JavaPlugin plugin = getPlugin();
        return plugin.getConfig();
    }

    private boolean isEnabled() {
        FileConfiguration configuration = getConfiguration();
        return configuration.getBoolean("enable-sign-editor", true);
    }

    private boolean hasPermission(Player player) {
        FileConfiguration configuration = getConfiguration();
        boolean usePermissions = configuration.getBoolean("permission-mode");
        return (!usePermissions || player.hasPermission("signs.edit"));
    }

    private boolean isDebugModeDisabled() {
        FileConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("debug-mode", false);
    }

    private void printDebug(String message) {
        if (isDebugModeDisabled()) {
            return;
        }

        JavaPlugin plugin = getPlugin();
        Logger logger = plugin.getLogger();
        logger.info("[Debug] [Sign Editor] " + message);
    }

    private char getColorCharacter() {
        FileConfiguration configuration = this.plugin.getConfig();
        String characterString = configuration.getString("color-character");
        if (characterString == null) {
            return '&';
        }

        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }

    private Pattern getRgbPatternFinder() {
        char colorChar = getColorCharacter();
        String colorCharString = Character.toString(colorChar);
        String colorCharPattern = Pattern.quote(colorCharString);

        String patternString = (colorCharPattern + "x" + "((" + colorCharPattern + "[a-fA-F\\d]){6})");
        return Pattern.compile(patternString);
    }

    private String fixHexColors(String text) {
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
