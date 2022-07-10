package com.github.sirblobman.colored.signs.listener;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ListenerSignEditor implements Listener {
    private final JavaPlugin plugin;

    public ListenerSignEditor(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    public void register() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this.plugin);
    }

    private boolean isEnabled() {
        FileConfiguration configuration = this.plugin.getConfig();
        return configuration.getBoolean("enable-sign-editor", true);
    }

    private boolean hasPermission(Player player) {
        FileConfiguration configuration = this.plugin.getConfig();
        boolean usePermissions = configuration.getBoolean("permission-mode");
        return (!usePermissions || player.hasPermission("signs.edit"));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EquipmentSlot hand = e.getHand();
        if(hand != EquipmentSlot.HAND) {
            return;
        }

        Player player = e.getPlayer();
        if(!player.isSneaking()) {
            return;
        }

        if(!isEnabled() || !hasPermission(player)) {
            return;
        }

        Block block = e.getClickedBlock();
        if(block == null) {
            return;
        }

        BlockState blockState = block.getState();
        if(!(blockState instanceof Sign sign)) {
            return;
        }

        player.openSign(sign);
    }
}
