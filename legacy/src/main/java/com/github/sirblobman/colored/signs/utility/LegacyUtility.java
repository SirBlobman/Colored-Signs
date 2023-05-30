package com.github.sirblobman.colored.signs.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class LegacyUtility {
    @SuppressWarnings("deprecation")
    public static @NotNull Block getTargetBlock(@NotNull Player player) {
        return player.getTargetBlock(null, 20);
    }

    public static @NotNull String replaceAll(char colorChar, @Nullable String string) {
        if (string == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes(colorChar, string);
    }

    public static @NotNull String replaceColor(char colorChar, @NotNull String string) {
        String specific = "0123456789AaBbCcDdEeFf";
        return replaceSpecific(colorChar, specific, string);
    }

    public static @NotNull String replaceFormat(char colorChar, @NotNull String string) {
        String specific = "KkLlMmNnOoRr";
        return replaceSpecific(colorChar, specific, string);
    }

    public static @NotNull String replaceSpecific(char colorChar, @NotNull String specific, @NotNull String string) {
        char[] charArray = string.toCharArray();
        for (int i = 0; i < (charArray.length - 1); i++) {
            boolean hasColor1 = (colorChar == charArray[i]);
            boolean hasColor2 = (specific.indexOf(charArray[i + 1]) > -1);
            if (hasColor1 && hasColor2) {
                charArray[i] = ChatColor.COLOR_CHAR;
                charArray[i + 1] = Character.toLowerCase(charArray[i + 1]);
            }
        }

        return new String(charArray);
    }
}
