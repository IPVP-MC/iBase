package me.poisonex.plugins.ibasic.commands.special;

import me.poisonex.plugins.ibasic.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CmdText {
    private String name;
    private File file;

    public CmdText(Main plugin, String name) {
        this.name = name;
        this.file = new File(plugin.getDataFolder(), this.name + ".txt");

        if (!this.file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean onCommand(Player sender) {
        try {
            Scanner scanner = new Scanner(this.file);

            while (scanner.hasNextLine()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', scanner.nextLine()));
            }

            scanner.close();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reading from " + this.name + " file.");
        }

        return true;
    }
}
