package com.github.sirblobman.colored.signs.listener;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.colored.signs.utility.LegacyUtility;

public final class ListenerLegacyColors implements Listener {
    private final JavaPlugin plugin;

    public ListenerLegacyColors(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    public void register() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this.plugin);
    }

    @EventHandler(priority= EventPriority.NORMAL, ignoreCancelled=true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        String[] lineArray = e.getLines();
        int lineArrayLength = lineArray.length;

        for(int i = 0; i < lineArrayLength; i++) {
            String oldLine = lineArray[i];
            String newLine = formatLine(player, oldLine);
            e.setLine(i, newLine);
        }
    }

    private char getColorCharacter() {
        FileConfiguration configuration = this.plugin.getConfig();
        String characterString = configuration.getString("color-character");
        if(characterString == null) {
            return '&';
        }

        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }

    private boolean hasAllPermissions(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        boolean permissionMode = configuration.getBoolean("permission-mode");
        if(!permissionMode) {
            return true;
        }

        if(player.hasPermission("signs.all")) {
            return true;
        }

        return (player.hasPermission("signs.color.all") && player.hasPermission("signs.format.all"));
    }

    private String formatLine(Player player, String string) {
        char colorChar = getColorCharacter();
        if(hasAllPermissions(player)) {
            return LegacyUtility.replaceAll(colorChar, string);
        }

        boolean ignoreColorCheck = false;
        boolean ignoreFormatCheck = false;

        if(player.hasPermission("signs.color.all")) {
            string = LegacyUtility.replaceColor(colorChar, string);
            ignoreColorCheck = true;
        }

        if(player.hasPermission("signs.format.all")) {
            string = LegacyUtility.replaceFormat(colorChar, string);
            ignoreFormatCheck = true;
        }

        if(!ignoreColorCheck) {
            string = replaceColors(player, string);
        }

        if(!ignoreFormatCheck) {
            string = replaceFormats(player, string);
        }

        return string;
    }

    private String replaceColors(Player player, String string) {
        char colorChar = getColorCharacter();
        String[] validColorArray = {"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        for(String code : validColorArray) {
            String permission = ("signs.color." + code);
            if(!player.hasPermission(permission)) {
                continue;
            }

            String caps = code.toUpperCase();
            string = LegacyUtility.replaceSpecific(colorChar, caps + code, string);
        }

        return string;
    }

    private String replaceFormats(Player player, String string) {
        char colorChar = getColorCharacter();
        String[] validFormatArray = {"k", "l", "m", "n", "o", "r"};

        for(String code : validFormatArray) {
            String permission = ("signs.format." + code);
            if(!player.hasPermission(permission)) {
                continue;
            }

            String caps = code.toUpperCase();
            string = LegacyUtility.replaceSpecific(colorChar, caps + code, string);
        }

        return string;
    }
}
