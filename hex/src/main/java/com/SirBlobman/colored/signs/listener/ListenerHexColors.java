package com.SirBlobman.colored.signs.listener;

import java.awt.*;
import java.util.logging.Logger;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ListenerHexColors implements Listener {
    private final JavaPlugin plugin;
    public ListenerHexColors(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        if(!isEnabled()) return;
        if(!hasPermission(player)) return;
    
        String[] lineArray = e.getLines();
        for(int i = 0; i < lineArray.length; i++) {
            String oldLine = lineArray[i];
            String newLine = replaceRGB(oldLine);
            e.setLine(i, newLine);
        }
    }
    
    private FileConfiguration getConfig() {
        return this.plugin.getConfig();
    }
    
    private boolean isEnabled() {
        FileConfiguration config = getConfig();
        return config.getBoolean("options.use hex colors", true);
    }
    
    private boolean hasPermission(Player player) {
        FileConfiguration config = getConfig();
        boolean usePermissions = config.getBoolean("options.use permissions");
        return (!usePermissions || player.hasPermission("signs.color.hex"));
    }
    
    private char getColorCharacter() {
        FileConfiguration config = getConfig();
        String characterString = config.getString("options.color character");
        if(characterString == null) return '&';
        
        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }
    
    private Pattern getReplaceAllRgbPattern() {
        char colorChar = getColorCharacter();
        String colorCharString = Character.toString(colorChar);
        String colorCharPattern = Pattern.quote(colorCharString);
        
        String patternString = ("(" + colorCharPattern + ")?" + colorCharPattern + "#([0-9a-fA-F]{6})");
        return Pattern.compile(patternString);
    }
    
    /**
     * Replace RGB Section copied from EssentialsX
     * @param string The string to replace colors in
     * @return A string with RGB colors replaced (1.16+)
     */
    private String replaceRGB(String string) {
        char colorChar = getColorCharacter();
        Pattern rgbPattern = getReplaceAllRgbPattern();
        Matcher matcher = rgbPattern.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        
        while(matcher.find()) {
            boolean isEscaped = (matcher.group(1) != null);
            if(!isEscaped) {
                try {
                    String hexCode = matcher.group(2);
                    matcher.appendReplacement(stringBuffer, parseHexColor(hexCode));
                    continue;
                } catch(NumberFormatException ignored) {}
            }
            
            matcher.appendReplacement(stringBuffer, colorChar + "#$2");
        }
        
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
    
    private String parseHexColor(String hexColor) throws NumberFormatException {
        if(hexColor.startsWith("#")) hexColor = hexColor.substring(1);
        if(hexColor.length() != 6) throw new NumberFormatException("Invalid hext length");
    
        Color.decode("#" + hexColor);
        StringBuilder assembled = new StringBuilder();
        assembled.append("\u00A7x");
    
        char[] charArray = hexColor.toCharArray();
        for(char character : charArray) {
            assembled.append('\u00A7');
            assembled.append(character);
        }
        
        return assembled.toString();
    }
}