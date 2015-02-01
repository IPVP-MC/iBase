package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLimitListener implements Listener {

    private final BasePlugin plugin;

    public PlayerLimitListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        int curPlayers = Bukkit.getServer().getOnlinePlayers().size();
        int maxPlayers = plugin.getServerManager().getMaxPlayers();
        if (curPlayers >= maxPlayers && !player.hasPermission("hcf.serverfull.bypass")) {
            event.setResult(PlayerLoginEvent.Result.KICK_FULL);
            event.setKickMessage(ChatColor.RED + "The server is full. (" + curPlayers + "/" + maxPlayers + ") \n\n" +
                    "Donate for " + ChatColor.GREEN + "VIP" + ChatColor.RED + " to bypass this restriction.");
        }
    }
}
