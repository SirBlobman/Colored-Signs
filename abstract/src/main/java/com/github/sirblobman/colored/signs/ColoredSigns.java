package com.github.sirblobman.colored.signs;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.plugin.Plugin;

import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;
import com.github.sirblobman.colored.signs.configuration.LanguageConfiguration;

public interface ColoredSigns {
    Logger getLogger();

    @NotNull Plugin getPlugin();

    @NotNull ColoredSignsConfiguration getConfiguration();

    @NotNull LanguageConfiguration getLanguageConfiguration();

    @NotNull String fullColor(@NotNull String original);
}
