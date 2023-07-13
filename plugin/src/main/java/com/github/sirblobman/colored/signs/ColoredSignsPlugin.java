package com.github.sirblobman.colored.signs;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.colored.signs.command.CommandEditSign;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;
import com.github.sirblobman.colored.signs.configuration.ConfigurationManager;
import com.github.sirblobman.colored.signs.configuration.LanguageConfiguration;
import com.github.sirblobman.colored.signs.listener.ListenerHexColors;
import com.github.sirblobman.colored.signs.listener.ListenerLegacyColors;
import com.github.sirblobman.colored.signs.listener.ListenerSignEditor;
import com.github.sirblobman.colored.signs.utility.LegacyUtility;
import com.github.sirblobman.colored.signs.utility.ModernUtility;
import com.github.sirblobman.colored.signs.utility.VersionUtility;

public final class ColoredSignsPlugin extends JavaPlugin implements ColoredSigns {
    private final ConfigurationManager configurationManager;
    private final ColoredSignsConfiguration configuration;
    private final LanguageConfiguration languageConfiguration;

    public ColoredSignsPlugin() {
        this.configurationManager = new ConfigurationManager(this);
        this.configuration = new ColoredSignsConfiguration();
        this.languageConfiguration = new LanguageConfiguration(this);
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        reloadConfig();
        registerCommand();
        registerListeners();
        broadcastEnableMessage();
    }

    @Override
    public void onDisable() {
        broadcastDisableMessage();
    }

    @Override
    public void saveDefaultConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("language.yml");
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("language.yml");

        YamlConfiguration configFile = configurationManager.get("config.yml");
        ColoredSignsConfiguration configuration = getConfiguration();
        configuration.load(configFile);

        YamlConfiguration languageFile = configurationManager.get("language.yml");
        LanguageConfiguration languageConfiguration = getLanguageConfiguration();
        languageConfiguration.load(languageFile);
    }

    @Override
    public @NotNull ColoredSignsConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public @NotNull LanguageConfiguration getLanguageConfiguration() {
        return this.languageConfiguration;
    }

    @Override
    public @NotNull String fullColor(@NotNull String original) {
        ColoredSignsConfiguration configuration = getConfiguration();
        int minorVersion = VersionUtility.getMinorVersion();

        char colorChar = configuration.getColorCharacter();
        boolean hex = (minorVersion >= 16 && configuration.isEnableHexColorCodes());

        String replaced = LegacyUtility.replaceAll(colorChar, original);

        if (hex) {
            replaced = ModernUtility.replaceHexColors(colorChar, original);
        }

        return replaced;
    }

    private @NotNull ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    private void registerCommand() {
        PluginCommand pluginCommand = getCommand("edit-sign");
        TabExecutor commandExecutor = new CommandEditSign(this);
        pluginCommand.setExecutor(commandExecutor);
        pluginCommand.setTabCompleter(commandExecutor);
    }

    private void registerListeners() {
        new ListenerLegacyColors(this).register();

        // Hex Color support was added in Spigot 1.16.5
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion >= 16) {
            new ListenerHexColors(this).register();
        }

        // Sign Editor support was added in Spigot 1.18.2
        // Vanilla added sign editing in 1.20
        if (minorVersion > 17 && minorVersion < 20) {
            new ListenerSignEditor(this).register();
        }
    }

    private void broadcastEnableMessage() {
        ColoredSignsConfiguration configuration = getConfiguration();
        if (configuration.isBroadcastOnEnable()) {
            LanguageConfiguration languageConfiguration = getLanguageConfiguration();
            String message = languageConfiguration.getBroadcastEnabled();
            Bukkit.broadcastMessage(message);
        }
    }

    private void broadcastDisableMessage() {
        ColoredSignsConfiguration configuration = getConfiguration();
        if (configuration.isBroadcastOnDisable()) {
            LanguageConfiguration languageConfiguration = getLanguageConfiguration();
            String message = languageConfiguration.getBroadcastDisabled();
            Bukkit.broadcastMessage(message);
        }
    }
}
