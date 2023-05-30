package com.github.sirblobman.colored.signs.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.colored.signs.ColoredSigns;

public final class LanguageConfiguration {
    private final ColoredSigns plugin;

    private String errorPlayerOnly;
    private String errorNotSign;
    private String errorInvalidLine;

    private String broadcastEnabled;
    private String broadcastDisabled;

    private String successfulEdit;

    public LanguageConfiguration(@NotNull ColoredSigns plugin) {
        this.plugin = plugin;
        this.errorPlayerOnly = "";
        this.errorNotSign = "";
        this.errorInvalidLine = "";
        this.broadcastEnabled = "";
        this.broadcastDisabled = "";
        this.successfulEdit = "";
    }

    private @NotNull ColoredSigns getPlugin() {
        return this.plugin;
    }

    private @NotNull String color(@NotNull String original) {
        ColoredSigns plugin = getPlugin();
        return plugin.fullColor(original);
    }

    public void load(@NotNull ConfigurationSection config) {
        String errorPlayerOnly = config.getString("error.player-only", "");
        String errorNotSign = config.getString("error.not-sign", "");
        String errorInvalidLine = config.getString("error.invalid-line", "");
        String broadcastEnabled = config.getString("broadcast-enabled", "");
        String broadcastDisabled = config.getString("broadcast-disabled", "");
        String successfulEdit = config.getString("successful-edit", "");

        setErrorPlayerOnly(color(errorPlayerOnly));
        setErrorNotSign(color(errorNotSign));
        setErrorInvalidLine(color(errorInvalidLine));
        setBroadcastEnabled(color(broadcastEnabled));
        setBroadcastDisabled(color(broadcastDisabled));
        setSuccessfulEdit(color(successfulEdit));
    }

    public @NotNull String getErrorPlayerOnly() {
        return this.errorPlayerOnly;
    }

    public void setErrorPlayerOnly(@NotNull String errorPlayerOnly) {
        this.errorPlayerOnly = errorPlayerOnly;
    }

    public @NotNull String getErrorNotSign() {
        return this.errorNotSign;
    }

    public void setErrorNotSign(@NotNull String errorNotSign) {
        this.errorNotSign = errorNotSign;
    }

    public @NotNull String getErrorInvalidLine() {
        return this.errorInvalidLine;
    }

    public void setErrorInvalidLine(@NotNull String errorInvalidLine) {
        this.errorInvalidLine = errorInvalidLine;
    }

    public @NotNull String getBroadcastEnabled() {
        return this.broadcastEnabled;
    }

    public void setBroadcastEnabled(@NotNull String broadcastEnabled) {
        this.broadcastEnabled = broadcastEnabled;
    }

    public @NotNull String getBroadcastDisabled() {
        return this.broadcastDisabled;
    }

    public void setBroadcastDisabled(@NotNull String broadcastDisabled) {
        this.broadcastDisabled = broadcastDisabled;
    }

    public @NotNull String getSuccessfulEdit() {
        return this.successfulEdit;
    }

    public void setSuccessfulEdit(@NotNull String successfulEdit) {
        this.successfulEdit = successfulEdit;
    }
}
