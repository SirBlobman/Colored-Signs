package com.SirBlobman.colored.signs.listener;

import java.util.Objects;

import com.SirBlobman.colored.signs.utility.HexColorUtility;

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
        if(!isEnabled() || !hasPermission(player)) return;
        
        char colorChar = getColorCharacter();
        String[] lineArray = e.getLines();
        int lineArrayLength = lineArray.length;
        
        for(int i = 0; i < lineArrayLength; i++) {
            String oldLine = lineArray[i];
            String newLine = HexColorUtility.replaceHexColors(colorChar, oldLine);
            e.setLine(i, newLine);
        }
    }
    
    private boolean isEnabled() {
        FileConfiguration config = this.plugin.getConfig();
        return config.getBoolean("options.use hex colors", true);
    }
    
    private boolean hasPermission(Player player) {
        FileConfiguration config = this.plugin.getConfig();
        boolean usePermissions = config.getBoolean("options.use permissions");
        return (!usePermissions || player.hasPermission("signs.color.hex"));
    }
    
    private char getColorCharacter() {
        FileConfiguration config = this.plugin.getConfig();
        String characterString = config.getString("options.color character");
        if(characterString == null) return '&';
        
        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }
}