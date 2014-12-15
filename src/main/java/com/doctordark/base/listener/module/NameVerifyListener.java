package com.doctordark.base.listener.module;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.regex.Pattern;

/**
 * Listener that verifies that player names are valid when they join.
 */
public class NameVerifyListener implements Listener {

    protected final static Pattern namePattern = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!namePattern.matcher(player.getName()).matches()) {
            Bukkit.getLogger().info("Name verification: " + player.getName() + " was kicked " +
                    "for having an invalid name (to disable, turn off the name-verification feature in the config)");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Invalid player name detected!");
        }
    }
}
