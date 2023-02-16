package com.github.sirblobman.colored.signs.listener;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.colored.signs.IColoredSigns;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;

public abstract class ColoredSignsListener implements Listener {
    private final IColoredSigns plugin;

    public ColoredSignsListener(IColoredSigns plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    public final void register() {
        IColoredSigns coloredSigns = getPlugin();
        JavaPlugin plugin = coloredSigns.asPlugin();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    protected final IColoredSigns getPlugin() {
        return this.plugin;
    }

    protected final Logger getLogger() {
        IColoredSigns plugin = getPlugin();
        return plugin.getLogger();
    }

    protected final ColoredSignsConfiguration getConfiguration() {
        IColoredSigns plugin = getPlugin();
        return plugin.getConfiguration();
    }

    protected final void printDebug(String message) {
        ColoredSignsConfiguration configuration = getConfiguration();
        if (!configuration.isDebugMode()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String fullMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger logger = getLogger();
        logger.info(fullMessage);
    }
}
