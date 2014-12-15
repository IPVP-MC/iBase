package com.doctordark.base.listener.module;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Listens for events when a player re-spawns.
 */
public class RespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.setFireTicks(0);
    }
}
