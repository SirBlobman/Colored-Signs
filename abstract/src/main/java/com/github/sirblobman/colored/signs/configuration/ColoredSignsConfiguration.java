package com.github.sirblobman.colored.signs.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

public final class ColoredSignsConfiguration {
    private boolean debugMode;
    private char colorCharacter;
    private boolean permissionMode;
    private boolean broadcastOnEnable;
    private boolean broadcastOnDisable;
    private boolean enableHexColorCodes;
    private boolean enableSignEditor;

    public ColoredSignsConfiguration() {
        this.debugMode = false;
        this.colorCharacter = '&';
        this.permissionMode = false;
        this.broadcastOnEnable = true;
        this.broadcastOnDisable = true;
        this.enableHexColorCodes = true;
        this.enableSignEditor = true;
    }

    public void load(@NotNull ConfigurationSection config) {
        setDebugMode(config.getBoolean("debug-mode", false));

        String colorCharString = config.getString("color-character", "&");
        if (colorCharString.isEmpty()) {
            setColorCharacter('&');
        } else {
            char[] chars = colorCharString.toCharArray();
            setColorCharacter(chars[0]);
        }

        setPermissionMode(config.getBoolean("permission-mode", false));
        setBroadcastOnEnable(config.getBoolean("broadcast-enabled", true));
        setBroadcastOnDisable(config.getBoolean("broadcast-disabled", true));
        setEnableHexColorCodes(config.getBoolean("enable-hex-color-codes", true));
        setEnableSignEditor(config.getBoolean("enable-sign-editor", true));
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public char getColorCharacter() {
        return this.colorCharacter;
    }

    public void setColorCharacter(char colorCharacter) {
        this.colorCharacter = colorCharacter;
    }

    public boolean isPermissionMode() {
        return this.permissionMode;
    }

    public void setPermissionMode(boolean permissionMode) {
        this.permissionMode = permissionMode;
    }

    public boolean isBroadcastOnEnable() {
        return this.broadcastOnEnable;
    }

    public void setBroadcastOnEnable(boolean broadcastOnEnable) {
        this.broadcastOnEnable = broadcastOnEnable;
    }

    public boolean isBroadcastOnDisable() {
        return this.broadcastOnDisable;
    }

    public void setBroadcastOnDisable(boolean broadcastOnDisable) {
        this.broadcastOnDisable = broadcastOnDisable;
    }

    public boolean isEnableHexColorCodes() {
        return this.enableHexColorCodes;
    }

    public void setEnableHexColorCodes(boolean enableHexColorCodes) {
        this.enableHexColorCodes = enableHexColorCodes;
    }

    public boolean isEnableSignEditor() {
        return this.enableSignEditor;
    }

    public void setEnableSignEditor(boolean enableSignEditor) {
        this.enableSignEditor = enableSignEditor;
    }
}
