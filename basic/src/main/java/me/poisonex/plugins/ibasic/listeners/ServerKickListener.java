package me.poisonex.plugins.ibasic.listeners;

import me.poisonex.plugins.ibasic.Main;
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
    private Main plugin;
    private File file;
    private String kickMessage;

    public ServerKickListener(Main plugin) {
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), "server_full.txt");

        if (!this.file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Scanner s = null;
        this.kickMessage = "";

        try {
            s = new Scanner(this.file);

            while (s.hasNextLine()) {
                String nextLine = ChatColor.translateAlternateColorCodes('&', s.nextLine());

                kickMessage += nextLine + System.lineSeparator();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
