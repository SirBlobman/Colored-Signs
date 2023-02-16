package com.github.sirblobman.colored.signs.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import com.github.sirblobman.colored.signs.IColoredSigns;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;
import com.github.sirblobman.colored.signs.utility.LegacyUtility;

public final class ListenerLegacyColors extends ColoredSignsListener {
    public ListenerLegacyColors(IColoredSigns plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        String[] lineArray = e.getLines();
        int lineArrayLength = lineArray.length;

        for (int i = 0; i < lineArrayLength; i++) {
            String oldLine = lineArray[i];
            String newLine = formatLine(player, oldLine);
            e.setLine(i, newLine);
        }
    }

    private char getColorCharacter() {
        ColoredSignsConfiguration configuration = getConfiguration();
        return configuration.getColorCharacter();
    }

    private boolean hasAllPermissions(Player player) {
        ColoredSignsConfiguration configuration = getConfiguration();
        if (!configuration.isPermissionMode()) {
            return true;
        }

        if (player.hasPermission("signs.all")) {
            return true;
        }

        return (player.hasPermission("signs.color.all") && player.hasPermission("signs.format.all"));
    }

    private String formatLine(Player player, String string) {
        char colorChar = getColorCharacter();
        if (hasAllPermissions(player)) {
            return LegacyUtility.replaceAll(colorChar, string);
        }

        boolean ignoreColorCheck = false;
        boolean ignoreFormatCheck = false;

        if (player.hasPermission("signs.color.all")) {
            string = LegacyUtility.replaceColor(colorChar, string);
            ignoreColorCheck = true;
        }

        if (player.hasPermission("signs.format.all")) {
            string = LegacyUtility.replaceFormat(colorChar, string);
            ignoreFormatCheck = true;
        }

        if (!ignoreColorCheck) {
            string = replaceColors(player, string);
        }

        if (!ignoreFormatCheck) {
            string = replaceFormats(player, string);
        }

        return string;
    }

    private String replaceColors(Player player, String string) {
        char colorChar = getColorCharacter();
        String[] validColorArray = {"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

        for (String code : validColorArray) {
            String permission = ("signs.color." + code);
            if (!player.hasPermission(permission)) {
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

        for (String code : validFormatArray) {
            String permission = ("signs.format." + code);
            if (!player.hasPermission(permission)) {
                continue;
            }

            String caps = code.toUpperCase();
            string = LegacyUtility.replaceSpecific(colorChar, caps + code, string);
        }

        return string;
    }
}
