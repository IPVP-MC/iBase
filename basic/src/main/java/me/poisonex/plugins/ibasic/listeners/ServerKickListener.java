package me.poisonex.plugins.ibasic.listeners;

import com.google.common.base.Joiner;
import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.io.File;

public class ServerKickListener implements Listener {

    private String kickMessage;

    public ServerKickListener(IBasic plugin) {
        this.kickMessage = Joiner.on(System.lineSeparator()).join(IBasic.readLines(plugin, new File(plugin.getDataFolder(), "server_full.txt")));
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.KICK_FULL) {
            event.setKickMessage(this.kickMessage);
        }
    }
}
