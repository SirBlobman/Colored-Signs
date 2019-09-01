package com.SirBlobman.colored.signs;

import com.SirBlobman.colored.signs.listener.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ColoredSigns extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new SignListener(this), this);

        FileConfiguration config = getConfig();
        if(config.getBoolean("options.broadcast startup")) {
            String message = color('&', "&2Colored Signs are now enabled!");
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