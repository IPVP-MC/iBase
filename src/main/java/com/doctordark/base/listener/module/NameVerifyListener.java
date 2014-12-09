package com.doctordark.base.listener.module;

import org.bukkit.Bukkit;
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
        if (!namePattern.matcher(event.getPlayer().getName()).matches()) {
            Bukkit.getLogger().info("Name verification: " + event.getPlayer().getName() + " was kicked " +
                    "for having an invalid name (to disable, turn off the name-verification component in CommandBook)");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Invalid player name detected!");
        }
    }
}
