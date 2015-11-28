package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CmdText {
    private String name;
    private File file;

    public CmdText(IBasic plugin, String name) {
        this.name = name;
        this.file = new File(plugin.getDataFolder(), this.name + ".txt");

        if (!this.file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean onCommand(Player sender) {
        try (Scanner scanner = new Scanner(this.file)) {
            while (scanner.hasNextLine()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', scanner.nextLine()));
            }
        } catch (IOException ex) {
            sender.sendMessage(ChatColor.RED + "Error reading from " + this.name + " file.");
        }

        return true;
    }
}
