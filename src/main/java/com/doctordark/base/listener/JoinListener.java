package com.doctordark.base.listener;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.user.BaseUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private static final int MAX_ACCOUNTS_PER_IP = 3;
    private final BasePlugin plugin;

    public JoinListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        baseUser.tryLoggingName(player);

        String ipAddress = player.getAddress().getHostString();
        baseUser.logAddress(ipAddress);
    }
}
