package com.github.sirblobman.colored.signs.utility;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class ModernUtility {
    public static Block getTargetBlock(Player player) {
        return player.getTargetBlock(null, 20);
    }

    public static String replaceHexColors(char colorChar, String string) {
        Pattern pattern = getReplaceAllRgbPattern(colorChar);
        Matcher matcher = pattern.matcher(string);

        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                matcher.appendReplacement(buffer, colorChar + "#$2");
                continue;
            }

            try {
                String hexCodeString = matcher.group(2);
                String hexCode = parseHexColor(hexCodeString);
                matcher.appendReplacement(buffer, hexCode);
            } catch (NumberFormatException ignored) {
                // Ignored Exception
            }
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static Pattern getReplaceAllRgbPattern(char colorChar) {
        String colorCharString = Character.toString(colorChar);
        String colorCharPattern = Pattern.quote(colorCharString);

        String patternString = ("(" + colorCharPattern + ")?" + colorCharPattern + "#([0-9a-fA-F]{6})");
        return Pattern.compile(patternString);
    }

    private static String parseHexColor(String string) throws NumberFormatException {
        if (string.startsWith("#")) {
            string = string.substring(1);
        }

        if (string.length() != 6) {
            throw new NumberFormatException("Invalid hex length");
        }

        Color.decode("#" + string);
        StringBuilder assembled = new StringBuilder();

        assembled.append(ChatColor.COLOR_CHAR);
        assembled.append("x");

        char[] charArray = string.toCharArray();
        for (char character : charArray) {
            assembled.append(ChatColor.COLOR_CHAR);
            assembled.append(character);
        }

        return assembled.toString();
    }
}
