package me.poisonex.plugins.ibasic.listeners;

import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ServerKickListener implements Listener {
    private String kickMessage;

    public ServerKickListener(IBasic plugin) {
        File file = new File(plugin.getDataFolder(), "server_full.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Scanner s = null;
        this.kickMessage = "";

        StringBuilder builder = new StringBuilder();

        try {
            s = new Scanner(file);

            while (s.hasNextLine()) {
                String nextLine = ChatColor.translateAlternateColorCodes('&', s.nextLine());

                builder.append(nextLine).append(System.lineSeparator());
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult() == Result.KICK_FULL) {
            e.setKickMessage(this.kickMessage);
        }
    }
}
