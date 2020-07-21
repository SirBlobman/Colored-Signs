package com.SirBlobman.colored.signs;

import java.util.logging.Logger;

import com.SirBlobman.colored.signs.listener.ListenerHexColors;
import com.SirBlobman.colored.signs.listener.ListenerLegacyColors;
import com.SirBlobman.colored.signs.utility.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ColoredSigns extends JavaPlugin {
    @Override
    public void onLoad() {
        saveDefaultConfig();
    }
    
    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        ListenerLegacyColors listenerLegacyColors = new ListenerLegacyColors(this);
        manager.registerEvents(listenerLegacyColors, this);
        
        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion >= 16) {
            ListenerHexColors listenerHexColors = new ListenerHexColors(this);
            manager.registerEvents(listenerHexColors, this);
        }

        FileConfiguration config = getConfig();
        if(config.getBoolean("options.broadcast startup")) {
            String message = color('&', "&2Colored Signs are now enabled!");
            Bukkit.broadcastMessage(message);
        }
    }
    
    @Override
    public void onDisable() {
        FileConfiguration config = getConfig();
        if(config.getBoolean("options.broadcast startup")) {
            String message = color('&', "&4Colored Signs are now disabled!");
            Bukkit.broadcastMessage(message);
        }
    }

    public String color(char colorChar, String message) {
        return ChatColor.translateAlternateColorCodes(colorChar, message);
    }

    public void debug(String message) {
        FileConfiguration config = getConfig();
        if(!config.getBoolean("options.debug")) return;

        Logger logger = getLogger();
        logger.info("[Debug] " + message);
    }
}