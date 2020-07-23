package com.SirBlobman.colored.signs.listener;

import java.util.Objects;

import com.SirBlobman.colored.signs.ColoredSigns;
import com.SirBlobman.colored.signs.utility.LegacyColorUtility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ListenerLegacyColors implements Listener {
    private final ColoredSigns plugin;
    public ListenerLegacyColors(ColoredSigns plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
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
        FileConfiguration config = this.plugin.getConfig();
        String characterString = config.getString("options.color character");
        if(characterString == null) return '&';
    
        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }
    
    private boolean hasAllPermissions(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        boolean usePermissions = config.getBoolean("options.use permissions");
        if(!usePermissions) return true;
        
        if(player.hasPermission("signs.all")) return true;
        return (player.hasPermission("signs.color.all") && player.hasPermission("signs.format.all"));
    }
    
    private String formatLine(Player player, String string) {
        char colorChar = getColorCharacter();
        if(hasAllPermissions(player)) return LegacyColorUtility.replaceAll(colorChar, string);
        boolean ignoreColorCheck = false, ignoreFormatCheck = false;
        
        if(player.hasPermission("signs.color.all")) {
            string = LegacyColorUtility.replaceColor(colorChar, string);
            ignoreColorCheck = true;
        }
        
        if(player.hasPermission("signs.format.all")) {
            string = LegacyColorUtility.replaceFormat(colorChar, string);
            ignoreFormatCheck = true;
        }
        
        if(!ignoreColorCheck) string = replaceColors(player, string);
        if(!ignoreFormatCheck) string = replaceFormats(player, string);
        return string;
    }

    private String replaceColors(Player player, String string) {
        char colorChar = getColorCharacter();
        String[] validColorArray = {"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        
        for(String code : validColorArray) {
            String permission = ("signs.color." + code);
            if(!player.hasPermission(permission)) continue;
            
            String caps = code.toUpperCase();
            string = LegacyColorUtility.replaceSpecific(colorChar, caps + code, string);
        }
        
        return string;
    }

    private String replaceFormats(Player player, String string) {
        char colorChar = getColorCharacter();
        String[] validFormatArray = {"k", "l", "m", "n", "o", "r"};
    
        for(String code : validFormatArray) {
            String permission = ("signs.format." + code);
            if(!player.hasPermission(permission)) continue;
        
            String caps = code.toUpperCase();
            string = LegacyColorUtility.replaceSpecific(colorChar, caps + code, string);
        }
    
        return string;
    }
}