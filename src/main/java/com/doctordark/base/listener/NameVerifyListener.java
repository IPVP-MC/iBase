package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.regex.Pattern;

public class NameVerifyListener implements Listener {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,16}$");

    private final BasePlugin plugin;

    public NameVerifyListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        PlayerLoginEvent.Result result = event.getResult();
        if (result == PlayerLoginEvent.Result.ALLOWED) {
            Player player = event.getPlayer();
            String playerName = player.getName();
            if (!NAME_PATTERN.matcher(playerName).matches()) {
                plugin.getLogger().info("Name verification: " + playerName + " was kicked for having an invalid name " +
                        "(to disable, turn off the name-verification feature in the config of 'Base' plugin)");

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Invalid player name detected.");
            }
        }
    }
}
