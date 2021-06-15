package com.github.sirblobman.colored.signs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.sirblobman.colored.signs.ColoredSignsPlugin;
import com.github.sirblobman.colored.signs.manager.ConfigurationManager;
import com.github.sirblobman.colored.signs.utility.LegacyUtility;
import com.github.sirblobman.colored.signs.utility.ModernUtility;
import com.github.sirblobman.colored.signs.utility.VersionUtility;

public final class CommandEditSign implements TabExecutor {
    private final ColoredSignsPlugin plugin;

    public CommandEditSign(ColoredSignsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            List<String> valueList = Arrays.asList("1", "2", "3", "4");
            return StringUtil.copyPartialMatches(args[0], valueList, new ArrayList<>());
        }

        if(args.length == 2) {
            return Collections.singletonList("Line Text");
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;
        if(!(sender instanceof Player)) {
            sendMessage(sender, "error.player-only", null);
            return true;
        }

        Player player = (Player) sender;
        String lineIndexString = args[0];
        int lineIndex;

        try {
            lineIndex = Integer.parseInt(lineIndexString);
            if(lineIndex < 1) throw new NumberFormatException("too small!");
            if(lineIndex > 4) throw new NumberFormatException("too big!");
            lineIndex = (lineIndex - 1);
        } catch(NumberFormatException ex) {
            sendMessage(player, "error.invalid-line",
                    message -> message.replace("{value}", lineIndexString));
            return true;
        }

        String[] lineArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
        String lineText = String.join(" ", lineArgs);

        char colorChar = getColorCharacter();
        int minorVersion = VersionUtility.getMinorVersion();

        lineText = LegacyUtility.replaceAll(colorChar, lineText);
        if(minorVersion >= 16) {
            lineText = ModernUtility.replaceHexColors(colorChar, lineText);
        }

        Block targetBlock;
        if(minorVersion >= 16) {
            targetBlock = ModernUtility.getTargetBlock(player);
        } else {
            targetBlock = LegacyUtility.getTargetBlock(player);
        }


        BlockState blockState = targetBlock.getState();
        if(!(blockState instanceof Sign)) {
            sendMessage(player, "error.not-sign", null);
            return true;
        }

        Sign sign = (Sign) blockState;
        sign.setLine(lineIndex, lineText);
        sign.update(true, true);

        sendMessage(player, "successful-edit", null);
        return true;
    }

    private char getColorCharacter() {
        FileConfiguration configuration = this.plugin.getConfig();
        String characterString = configuration.getString("color-character");
        if(characterString == null) return '&';

        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }

    private void sendMessage(CommandSender sender, String key, Function<String, String> replacer) {
        if(sender == null || key == null) return;

        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration language = configurationManager.get("language.yml");
        String message = language.getString(key);

        if(message == null || message.isEmpty()) return;
        if(replacer != null) message = replacer.apply(message);
        if(message == null || message.isEmpty()) return;

        String messageColored = this.plugin.defaultFullColor(message);
        sender.sendMessage(messageColored);
    }
}
