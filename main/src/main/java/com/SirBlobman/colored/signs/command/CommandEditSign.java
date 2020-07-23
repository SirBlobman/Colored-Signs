package com.SirBlobman.colored.signs.command;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.SirBlobman.colored.signs.ColoredSigns;
import com.SirBlobman.colored.signs.utility.HexColorUtility;
import com.SirBlobman.colored.signs.utility.LegacyColorUtility;
import com.SirBlobman.colored.signs.utility.Replacer;
import com.SirBlobman.colored.signs.utility.VersionUtility;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandEditSign implements TabExecutor {
    private final ColoredSigns plugin;
    public CommandEditSign(ColoredSigns plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 4) return Collections.singletonList("0");
        
        if(args.length == 4) {
            Set<String> valueSet = Stream.of(1, 2, 3, 4).map(Object::toString).collect(Collectors.toSet());
            return getMatching(valueSet, args[3]);
        }
        
        if(args.length == 5) return Collections.singletonList("&5Line &6Text &7With &8Colors");
        return Collections.emptyList();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sendMessage(sender, "messages.not-player");
            return true;
        }
        
        Player player = (Player) sender;
        if(args.length < 5) return false;
        
        World world = player.getWorld();
        String xString = args[0], yString = args[1], zString = args[2];
        int x, y, z;
        
        try {
            x = Integer.parseInt(xString);
            y = Integer.parseInt(yString);
            z = Integer.parseInt(zString);
        } catch(NumberFormatException ex) {
            sendMessage(player, "messages.invalid-coordinates", string -> string.replace("{value}", xString + ", " + yString + ", " + zString));
            return true;
        }
        
        String lineIndexString = args[3];
        int lineIndex;
        
        try {
            lineIndex = Integer.parseInt(lineIndexString);
            if(lineIndex < 1) throw new NumberFormatException("too small!");
            if(lineIndex > 4) throw new NumberFormatException("too big!");
            lineIndex = (lineIndex - 1);
        } catch(NumberFormatException ex) {
            sendMessage(player, "messages.invalid-line", string -> string.replace("{value}", lineIndexString));
            return true;
        }
        
        String[] lineTextArgs = Arrays.copyOfRange(args, 4, args.length);
        String lineText = String.join(" ", lineTextArgs);
        
        char colorChar = getColorCharacter();
        int minorVersion = VersionUtility.getMinorVersion();
        
        lineText = LegacyColorUtility.replaceAll(colorChar, lineText);
        if(minorVersion >= 16) lineText = HexColorUtility.replaceHexColors(colorChar, lineText);
        
        Block block = world.getBlockAt(x, y, z);
        BlockState blockState = block.getState();
        if(!(blockState instanceof Sign)) {
            sendMessage(player, "messages.not-sign");
            return true;
        }
        
        Sign sign = (Sign) blockState;
        sign.setLine(lineIndex, lineText);
        sign.update(true, true);
        
        sendMessage(player, "messages.successful-edit");
        return true;
    }
    
    private char getColorCharacter() {
        FileConfiguration config = this.plugin.getConfig();
        String characterString = config.getString("options.color character");
        if(characterString == null) return '&';
        
        char[] charArray = characterString.toCharArray();
        return charArray[0];
    }
    
    private List<String> getMatching(Iterable<String> valueList, String arg) {
        if(valueList == null || arg == null) return Collections.emptyList();
        List<String> matchList = new ArrayList<>();
        
        String lowerArg = arg.toLowerCase();
        for(String value : valueList) {
            String lowerValue = value.toLowerCase();
            if(!lowerValue.startsWith(lowerArg)) continue;
            matchList.add(value);
        }
        
        return matchList;
    }
    
    private void sendMessage(CommandSender sender, String path, Replacer... replacers) {
        FileConfiguration config = this.plugin.getConfig();
        String message = config.getString(path);
        if(message == null || message.isEmpty()) return;
        
        String color = LegacyColorUtility.replaceColor('&', message);
        for(Replacer replacer : replacers) color = replacer.replace(color);
        if(color == null || color.isEmpty()) return;
        
        sender.sendMessage(color);
    }
}