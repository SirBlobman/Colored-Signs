package com.SirBlobman.colored.signs.listener;

import com.SirBlobman.colored.signs.ColoredSigns;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.List;

public class SignListener implements Listener {
    private final ColoredSigns plugin;
    public SignListener(ColoredSigns plugin) {
        this.plugin = plugin;
    }

    private void debug(String message) {
        this.plugin.debug(message);
    }

    private FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        String[] lines = e.getLines();
        debug("Sign changed, checking for codes...");
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            debug("Line before codes: '" + line + "'.");

            String color = getFormattedSignString(player, line);
            debug("Line after codes: '" + (color.replace("\u00A7", getConfig().getString("options.color character"))) + "'.");

            e.setLine(i, color);
        }
    }

    private String getFormattedSignString(Player player, String line) {
        boolean permissions = getConfig().getBoolean("options.use permissions");
        if(!permissions) {
            debug("Permissions are not being used, replacing all colors and formatting...");
            return replaceAll(line);
        }

        debug("Permissions enabled, checking player '" + player.getName() + "'.");
        if(player.hasPermission("signs.all") || (player.hasPermission("signs.color.all") && player.hasPermission("signs.format.all"))) {
            debug("Player has all permissions, replacing all colors and formatting...");
            return replaceAll(line);
        }

        String finalLine = line;

        boolean ignoreColorCheck = false, ignoreFormatCheck = false;
        if(player.hasPermission("signs.color.all")) {
            debug("Player has all color permissions, replacing all colors...");
            finalLine = replaceColorsOnly(line);
            ignoreColorCheck = true;
        }

        if(player.hasPermission("signs.format.all")) {
            debug("Player has all formatting permissions, replacing all formats...");
            finalLine = replaceFormattingOnly(line);
            ignoreFormatCheck = true;
        }

        if(!ignoreColorCheck) finalLine = runColorCheck(player, finalLine);
        if(!ignoreFormatCheck) finalLine = runFormatCheck(player, finalLine);
        return finalLine;
    }

    private String runColorCheck(Player player, String line) {
        List<String> validColors = Arrays.asList("a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        for(String code : validColors) {
            String permission = "signs.color." + code;
            debug("Checking permission '" + permission + "'...");

            String caps = code.toUpperCase();
            if(player.hasPermission(permission)) {
                debug("Player has permission, replacing color...");
                line = replaceSpecific(line, caps + code);
            }
        }
        return line;
    }

    private String runFormatCheck(Player player, String line) {
        List<String> validFormats = Arrays.asList("k", "l", "m", "n", "o", "r");
        for(String code : validFormats) {
            String permission = "signs.format." + code;
            debug("Checking permission '" + permission + "'...");

            String caps = code.toUpperCase();
            if(player.hasPermission(permission)) {
                debug("Player has permission, replacing format...");
                line = replaceSpecific(line, caps + code);
            }
        }
        return line;
    }

    private String replaceColorsOnly(String line) {
        return replaceSpecific(line, "0123456789AaBbCcDdEeFf");
    }

    private String replaceFormattingOnly(String line) {
        return replaceSpecific(line, "KkLlMmNnOoRr");
    }

    private String replaceAll(String line) {
        char character = getConfig().getString("options.color character").charAt(0);
        return this.plugin.color(character, line);
    }

    private String replaceSpecific(String line, String specific) {
        char character = getConfig().getString("options.color character").charAt(0);
        final char[] charArray = line.toCharArray();
        for(int i = 0; (i < (charArray.length - 1)); i++) {
            boolean hasColor1 = (charArray[i] == character);
            boolean hasColor2 = (specific.indexOf(charArray[i + 1]) > -1);
            if(hasColor1 && hasColor2) {
                charArray[i] = ChatColor.COLOR_CHAR;
                charArray[i + 1] = Character.toLowerCase(charArray[i + 1]);
            }
        }

        return new String(charArray);
    }
}