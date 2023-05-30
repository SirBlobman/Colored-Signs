package com.github.sirblobman.colored.signs.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang.Validate;

public final class ConfigurationManager {
    private final File baseFolder;
    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> configurationMap;

    public ConfigurationManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.baseFolder = plugin.getDataFolder();
        this.configurationMap = new HashMap<>();
    }

    public @NotNull JavaPlugin getPlugin() {
        return this.plugin;
    }

    public @NotNull File getBaseFolder() {
        return this.baseFolder;
    }

    public void saveDefault(@NotNull String fileName) {
        File file = getFile(fileName);
        saveDefault(fileName, file);
    }

    public @Nullable YamlConfiguration getInternal(@NotNull String fileName) {
        JavaPlugin resourceHolder = getPlugin();
        InputStream inputStream = resourceHolder.getResource(fileName);
        if (inputStream == null) {
            return null;
        }

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(inputStreamReader);
            return configuration;
        } catch (IOException | InvalidConfigurationException ex) {
            return null;
        }
    }

    /**
     * @param fileName The relative name of the configuration to get
     * @return A configuration from memory. If the configuration is not in memory it will be loaded from storage first.
     * If a file can't be loaded, an empty configuration will be returned.
     */
    public @NotNull YamlConfiguration get(@NotNull String fileName) {
        YamlConfiguration configuration = this.configurationMap.getOrDefault(fileName, null);
        if (configuration != null) {
            return configuration;
        }

        reload(fileName);
        return this.configurationMap.getOrDefault(fileName, new YamlConfiguration());
    }

    /**
     * Load a configuration from storage into memory.
     *
     * @param fileName The relative name of the configuration.
     */
    public void reload(@NotNull String fileName) {
        File file = getFile(fileName);
        JavaPlugin resourceHolder = getPlugin();
        if (!file.exists() || !file.isFile()) {
            Logger logger = resourceHolder.getLogger();
            logger.warning("Failed to reload '" + fileName + "' because it is not a file or does not exist.");
            return;
        }

        try {
            YamlConfiguration configuration = new YamlConfiguration();

            YamlConfiguration jarConfiguration = getInternal(fileName);
            if (jarConfiguration != null) {
                configuration.setDefaults(jarConfiguration);
            }

            configuration.load(file);
            this.configurationMap.put(fileName, configuration);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger logger = resourceHolder.getLogger();
            logger.log(Level.WARNING, "Failed to reload '" + fileName + "':", ex);
        }
    }

    private @NotNull File getFile(@NotNull String fileName) {
        Validate.notEmpty(fileName, "fileName cannot be null or empty!");
        File baseFolder = getBaseFolder();
        return new File(baseFolder, fileName);
    }

    private void saveDefault(@NotNull String fileName, @NotNull File realFile) {
        Validate.notEmpty(fileName, "jarName cannot be null or empty!");
        if (realFile.exists()) {
            return;
        }

        JavaPlugin resourceHolder = getPlugin();
        InputStream jarStream = resourceHolder.getResource(fileName);
        if (jarStream == null) {
            Logger logger = resourceHolder.getLogger();
            logger.warning("Failed to save default config '" + fileName
                    + "' because it does not exist in the jar.");
            return;
        }

        try {
            File parentFile = realFile.getParentFile();
            if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
                Logger logger = resourceHolder.getLogger();
                logger.warning("Failed to save default config '" + fileName
                        + "' because the parent folder could not be created.");
                return;
            }

            if (!realFile.createNewFile()) {
                Logger logger = resourceHolder.getLogger();
                logger.warning("Failed to save default config '" + fileName
                        + "' because the file could not be created.");
                return;
            }

            Path absolutePath = realFile.toPath();
            Files.copy(jarStream, absolutePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger logger = resourceHolder.getLogger();
            logger.log(Level.WARNING, "An I/O exception occurred while saving a default file:", ex);
        }
    }
}
