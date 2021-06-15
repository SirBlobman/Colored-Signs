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

import com.github.sirblobman.colored.signs.utility.ModernUtility;

public final class ListenerHexColors implements Listener {
    private final JavaPlugin plugin;
    public ListenerHexColors(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    public void register() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this.plugin);
    }
    
    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        if(!isEnabled() || !hasPermission(player)) return;
        
        char colorChar = getColorCharacter();
        String[] lineArray = e.getLines();
        int lineArrayLength = lineArray.length;
        
        for(int i = 0; i < lineArrayLength; i++) {
            String oldLine = lineArray[i];
            String newLine = ModernUtility.replaceHexColors(colorChar, oldLine);
            e.setLine(i, newLine);
        }
    }
    
    private boolean isEnabled() {
        FileConfiguration configuration = this.plugin.getConfig();
        return configuration.getBoolean("enable-hex-color-codes", true);
    }
    
    private boolean hasPermission(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        boolean usePermissions = configuration.getBoolean("permission-mode");
        return (!usePermissions || player.hasPermission("signs.color.hex"));
    }
    
    private char getColorCharacter() {
        FileConfiguration configuration = this.plugin.getConfig();
        String characterString = configuration.getString("color-character");
        if(characterString == null) return '&';
        
        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }
}
