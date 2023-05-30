package com.github.sirblobman.colored.signs.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import com.github.sirblobman.colored.signs.ColoredSigns;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;
import com.github.sirblobman.colored.signs.utility.ModernUtility;

public final class ListenerHexColors extends ColoredSignsListener {
    public ListenerHexColors(@NotNull ColoredSigns coloredSigns) {
        super(coloredSigns);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        if (!isEnabled() || !hasPermission(player)) {
            return;
        }

        char colorChar = getColorCharacter();
        String[] lineArray = e.getLines();
        int lineArrayLength = lineArray.length;

        for (int i = 0; i < lineArrayLength; i++) {
            String oldLine = lineArray[i];
            String newLine = ModernUtility.replaceHexColors(colorChar, oldLine);
            e.setLine(i, newLine);
        }
    }

    private boolean isEnabled() {
        ColoredSignsConfiguration configuration = getConfiguration();
        return configuration.isEnableHexColorCodes();
    }

    private boolean hasPermission(@NotNull Player player) {
        ColoredSignsConfiguration configuration = getConfiguration();
        if (configuration.isPermissionMode()) {
            return player.hasPermission("signs.color.hex");
        }

        return true;
    }

    private char getColorCharacter() {
        ColoredSignsConfiguration configuration = getConfiguration();
        return configuration.getColorCharacter();
    }
}
