package com.github.sirblobman.colored.signs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.sirblobman.colored.signs.ColoredSignsPlugin;
import com.github.sirblobman.colored.signs.configuration.ColoredSignsConfiguration;
import com.github.sirblobman.colored.signs.configuration.LanguageConfiguration;
import com.github.sirblobman.colored.signs.utility.LegacyUtility;
import com.github.sirblobman.colored.signs.utility.ModernUtility;
import com.github.sirblobman.colored.signs.utility.VersionUtility;

public final class CommandEditSign implements TabExecutor {
    private final ColoredSignsPlugin plugin;

    public CommandEditSign(@NotNull ColoredSignsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> valueList = Arrays.asList("1", "2", "3", "4");
            return StringUtil.copyPartialMatches(args[0], valueList, new ArrayList<>());
        }

        if (args.length == 2) {
            return Collections.singletonList("Line Text");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        ColoredSignsPlugin plugin = getPlugin();
        LanguageConfiguration languageConfiguration = plugin.getLanguageConfiguration();
        if (!(sender instanceof Player)) {
            String message = languageConfiguration.getErrorPlayerOnly();
            sendMessage(sender, message);
            return true;
        }

        Player player = (Player) sender;
        String lineIndexString = args[0];
        int lineIndex;

        try {
            lineIndex = Integer.parseInt(lineIndexString);
            if (lineIndex < 1) {
                throw new NumberFormatException("too small!");
            }

            if (lineIndex > 4) {
                throw new NumberFormatException("too big!");
            }

            lineIndex = (lineIndex - 1);
        } catch (NumberFormatException ex) {
            String message = languageConfiguration.getErrorInvalidLine().replace("{value}", lineIndexString);
            sendMessage(player, message);
            return true;
        }

        String[] lineArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
        String lineText = String.join(" ", lineArgs);

        ColoredSignsConfiguration configuration = plugin.getConfiguration();
        char colorChar = configuration.getColorCharacter();

        int minorVersion = VersionUtility.getMinorVersion();
        boolean enableHexConfig = configuration.isEnableHexColorCodes();
        boolean versionCheck16 = (minorVersion >= 16);

        lineText = LegacyUtility.replaceAll(colorChar, lineText);
        if (enableHexConfig && versionCheck16) {
            lineText = ModernUtility.replaceHexColors(colorChar, lineText);
        }

        Block targetBlock;
        if (versionCheck16) {
            targetBlock = ModernUtility.getTargetBlock(player);
        } else {
            targetBlock = LegacyUtility.getTargetBlock(player);
        }


        BlockState blockState = targetBlock.getState();
        if (!(blockState instanceof Sign)) {
            String message = languageConfiguration.getErrorNotSign();
            sendMessage(player, message);
            return true;
        }

        Sign sign = (Sign) blockState;
        sign.setLine(lineIndex, lineText);
        sign.update(true, true);

        String message = languageConfiguration.getSuccessfulEdit();
        sendMessage(player, message);
        return true;
    }

    private @NotNull ColoredSignsPlugin getPlugin() {
        return this.plugin;
    }

    private void sendMessage(@NotNull CommandSender sender, @NotNull String message) {
        if (!message.isEmpty()) {
            sender.sendMessage(message);
        }
    }
}
