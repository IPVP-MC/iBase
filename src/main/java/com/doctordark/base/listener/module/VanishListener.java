package com.doctordark.base.listener.module;

import com.doctordark.base.BasePlugin;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles listeners for when a player is vanished.
 */
public class VanishListener implements Listener {

    private final BasePlugin plugin;

    public VanishListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    // Re-hides vanished players on re-log.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!player.hasPermission("base.command.vanish")) {
            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                boolean vanished = plugin.getUserManager().isVanished(other.getUniqueId());
                if (vanished) {
                    player.hidePlayer(other);
                }
            }
        } else if (plugin.getUserManager().isVanished(uuid)) {
            event.setJoinMessage(null);

            for (Player other : Bukkit.getServer().getOnlinePlayers()) {
                if (!other.hasPermission("base.command.vanish")) {
                    other.hidePlayer(player);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean vanished = plugin.getUserManager().isVanished(uuid);
        if (vanished) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean vanished = plugin.getUserManager().isVanished(uuid);
        if (vanished) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot drop items whilst invisible!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            UUID uuid = player.getUniqueId();
            boolean vanished = plugin.getUserManager().isVanished(uuid);
            if (vanished) {
                event.setCancelled(true);
                ItemStack stack = player.getItemInHand();
                stack.setAmount(stack.getAmount() + 1);
                player.sendMessage(ChatColor.RED + "You cannot launch projectiles whilst invisible!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean vanished = plugin.getUserManager().isVanished(uuid);
        if (vanished) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        boolean vanished = plugin.getUserManager().isVanished(uuid);
        if (vanished) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID uuid = player.getUniqueId();
            boolean vanished = plugin.getUserManager().isVanished(uuid);
            if (vanished) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean vanished = plugin.getUserManager().isVanished(uuid);
        if (vanished) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break blocks whilst invisible!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean vanished = plugin.getUserManager().isVanished(uuid);
        if (vanished) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks whilst invisible!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
        public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            boolean vanished = plugin.getUserManager().isVanished(uuid);
            if ((vanished) && (state instanceof InventoryHolder)) {
                InventoryHolder holder = (InventoryHolder) block.getState();
                Inventory inventory = holder.getInventory();
                InventoryType type = inventory.getType();

                // Create a fake inventory, so nearby players
                // don't see the vanished player open the chest.
                Inventory fakeInventory = Bukkit.createInventory(null, type, "[F] " + type.getDefaultTitle());

                if (state instanceof Chest) {
                    Chest chest = (Chest) state;
                    fakeInventory.setContents(chest.getBlockInventory().getContents());
                } else {
                    fakeInventory.setContents(inventory.getContents());
                }

                event.setCancelled(true);
                player.openInventory(fakeInventory);
            }
        }
    }
}
