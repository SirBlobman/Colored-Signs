package com.github.sirblobman.colored.signs.listener;

import java.util.Locale;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.colored.signs.ColoredSigns;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;

public abstract class ColoredSignsListener implements Listener {
    private final ColoredSigns coloredSigns;

    public ColoredSignsListener(@NotNull ColoredSigns coloredSigns) {
        this.coloredSigns = coloredSigns;
    }

    public final void register() {
        ColoredSigns coloredSigns = getColoredSigns();
        Plugin plugin = coloredSigns.getPlugin();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    protected final @NotNull ColoredSigns getColoredSigns() {
        return this.coloredSigns;
    }

    protected final @NotNull Plugin getPlugin() {
        ColoredSigns coloredSigns = getColoredSigns();
        return coloredSigns.getPlugin();
    }

    protected final Logger getLogger() {
        ColoredSigns plugin = getColoredSigns();
        return plugin.getLogger();
    }

    protected final ColoredSignsConfiguration getConfiguration() {
        ColoredSigns plugin = getColoredSigns();
        return plugin.getConfiguration();
    }

    protected final void printDebug(@NotNull String message) {
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
