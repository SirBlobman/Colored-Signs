package com.SirBlobman.colored.signs.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class VersionUtil {
    private static final Pattern MC_VERSION_PATTERN = Pattern.compile("(\\(MC: )([\\d.]+)(\\))");
    public static String getMinecraftVersion() {
        String bukkitVersion = Bukkit.getVersion();
        Matcher matcher = MC_VERSION_PATTERN.matcher(bukkitVersion);
        if(!matcher.find()) return "";
        
        return matcher.group(2);
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