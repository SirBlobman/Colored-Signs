package com.github.sirblobman.colored.signs.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public final class VersionUtility {
    private static final Pattern MC_VERSION_PATTERN = Pattern.compile("(\\(MC: )([\\d.]+)(\\))");
    
    public static String getMinecraftVersion() {
        String version = Bukkit.getVersion();
        Matcher matcher = MC_VERSION_PATTERN.matcher(version);
        return (matcher.find() ? matcher.group(2) : "");
    }
    
    public static String getBaseVersion() {
        String version = getMinecraftVersion();
        int lastPeriodIndex = version.lastIndexOf('\u002E');
        return (lastPeriodIndex < 2 ? version : version.substring(0, lastPeriodIndex));
    }
    
    public static int getMinorVersion() {
        String baseVersion = getBaseVersion();
        int indexOfPeriod = (baseVersion.indexOf('\u002E') + 1);
        String minorString = baseVersion.substring(indexOfPeriod);
        return Integer.parseInt(minorString);
    }
}