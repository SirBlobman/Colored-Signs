package com.github.sirblobman.colored.signs.configuration;

import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.colored.signs.IColoredSigns;

public final class LanguageConfiguration {
    private final IColoredSigns plugin;

    private String errorPlayerOnly;
    private String errorNotSign;
    private String errorInvalidLine;

    private String broadcastEnabled;
    private String broadcastDisabled;

    private String successfulEdit;

    public LanguageConfiguration(IColoredSigns plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");

        this.errorPlayerOnly = "";
        this.errorNotSign = "";
        this.errorInvalidLine = "";
        this.broadcastEnabled = "";
        this.broadcastDisabled = "";
        this.successfulEdit = "";
    }

    private IColoredSigns getPlugin() {
        return this.plugin;
    }

    private String color(String original) {
        IColoredSigns plugin = getPlugin();
        return plugin.fullColor(original);
    }

    public void load(ConfigurationSection config) {
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

    public String getErrorPlayerOnly() {
        return this.errorPlayerOnly;
    }

    public void setErrorPlayerOnly(String errorPlayerOnly) {
        this.errorPlayerOnly = errorPlayerOnly;
    }

    public String getErrorNotSign() {
        return this.errorNotSign;
    }

    public void setErrorNotSign(String errorNotSign) {
        this.errorNotSign = errorNotSign;
    }

    public String getErrorInvalidLine() {
        return this.errorInvalidLine;
    }

    public void setErrorInvalidLine(String errorInvalidLine) {
        this.errorInvalidLine = errorInvalidLine;
    }

    public String getBroadcastEnabled() {
        return this.broadcastEnabled;
    }

    public void setBroadcastEnabled(String broadcastEnabled) {
        this.broadcastEnabled = broadcastEnabled;
    }

    public String getBroadcastDisabled() {
        return this.broadcastDisabled;
    }

    public void setBroadcastDisabled(String broadcastDisabled) {
        this.broadcastDisabled = broadcastDisabled;
    }

    public String getSuccessfulEdit() {
        return this.successfulEdit;
    }

    public void setSuccessfulEdit(String successfulEdit) {
        this.successfulEdit = successfulEdit;
    }
}
