package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLimitListener implements Listener {

    private static final String BYPASS_FULL_JOIN = "base.serverfull.bypass";

    private final BasePlugin plugin;

    public PlayerLimitListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerLoginEvent.Result result = event.getResult();
        if (result == PlayerLoginEvent.Result.KICK_FULL) {
            if (player.hasPermission(BYPASS_FULL_JOIN)) {
                event.allow();
                return;
            }

            String kickMessage = plugin.getServerHandler().getFullServerKickMessage();
            event.setKickMessage(kickMessage);
        }
    }
}
