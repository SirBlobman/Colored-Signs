package com.github.sirblobman.colored.signs;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.colored.signs.command.CommandEditSign;
import com.github.sirblobman.colored.signs.listener.ListenerHexColors;
import com.github.sirblobman.colored.signs.listener.ListenerLegacyColors;
import com.github.sirblobman.colored.signs.listener.ListenerSignEditor;
import com.github.sirblobman.colored.signs.manager.ConfigurationManager;
import com.github.sirblobman.colored.signs.utility.LegacyUtility;
import com.github.sirblobman.colored.signs.utility.ModernUtility;
import com.github.sirblobman.colored.signs.utility.VersionUtility;

public final class ColoredSignsPlugin extends JavaPlugin {
    private final ConfigurationManager configurationManager;

    public ColoredSignsPlugin() {
        this.configurationManager = new ConfigurationManager(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("language.yml");
    }

    @Override
    public void onEnable() {
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
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    @Override
    public YamlConfiguration getConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        return configurationManager.get("config.yml");
    }

    @Override
    public void saveConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.save("config.yml");
    }

    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    public String defaultFullColor(String message) {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion > 16) {
            message = ModernUtility.replaceHexColors('&', message);
        }

        return LegacyUtility.replaceAll('&', message);
    }

    private void registerCommand() {
        PluginCommand pluginCommand = getCommand("edit-sign");
        TabExecutor commandExecutor = new CommandEditSign(this);
        pluginCommand.setExecutor(commandExecutor);
        pluginCommand.setTabCompleter(commandExecutor);
    }

    private void registerListeners() {
        new ListenerLegacyColors(this).register();

        // Hex Color support was added in Minecraft 1.16
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion >= 16) {
            new ListenerHexColors(this).register();
        }

        // Sign Editor support was added in Spigot 1.18.2
        if (minorVersion >= 18) {
            new ListenerSignEditor(this).register();
        }
    }

    private void broadcastEnableMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast-enabled", true)) {
            return;
        }

        YamlConfiguration language = configurationManager.get("language.yml");
        String message = language.getString("broadcast-enabled");
        if (message == null || message.isEmpty()) {
            return;
        }

        String messageColored = defaultFullColor(message);
        Bukkit.broadcastMessage(messageColored);
    }

    private void broadcastDisableMessage() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("broadcast-disabled", true)) {
            return;
        }

        YamlConfiguration language = configurationManager.get("language.yml");
        String message = language.getString("broadcast-disabled");
        if (message == null || message.isEmpty()) {
            return;
        }

        String messageColored = defaultFullColor(message);
        Bukkit.broadcastMessage(messageColored);
    }
}
