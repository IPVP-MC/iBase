package me.poisonex.plugins.ibasic.listeners;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
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

    private Set<UUID> playerSet = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            RegionManager regionManager = WGBukkit.getRegionManager(e.getPlayer().getWorld());
            ApplicableRegionSet regionSet = regionManager.getApplicableRegions(e.getPlayer().getLocation());

            for (ProtectedRegion region : regionSet) {
                if (region.getId().equalsIgnoreCase("spawn")) {
                    this.playerSet.add(e.getPlayer().getUniqueId());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.playerSet.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.playerSet.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (this.playerSet.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot speak or type commands until you move.");
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (this.playerSet.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot speak or type commands until you move.");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            this.playerSet.remove(e.getPlayer().getUniqueId());
        }
    }
}
