package me.poisonex.plugins.ibasic.listeners;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AntiBotListener implements Listener {

    private final Set<UUID> playerSet = new HashSet<>();

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            RegionManager regionManager = WGBukkit.getRegionManager(event.getPlayer().getWorld());
            ApplicableRegionSet regionSet = regionManager.getApplicableRegions(event.getPlayer().getLocation());

            for (ProtectedRegion region : regionSet) {
                if (region.getId().equalsIgnoreCase("spawn")) {
                    this.playerSet.add(event.getPlayer().getUniqueId());
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playerSet.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        this.playerSet.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (this.playerSet.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot speak or type commands until you move.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (this.playerSet.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot speak or type commands until you move.");
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
            this.playerSet.remove(event.getPlayer().getUniqueId());
        }
    }
}
