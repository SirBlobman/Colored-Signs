package com.github.sirblobman.colored.signs;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;
import com.github.sirblobman.colored.signs.configuration.LanguageConfiguration;

public interface IColoredSigns {
    Logger getLogger();
    JavaPlugin asPlugin();

    ColoredSignsConfiguration getConfiguration();
    LanguageConfiguration getLanguageConfiguration();

    String fullColor(String original);
}
