package com.doctordark.base.command.module.chat;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {

    private static final int CHAT_HEIGHT = 101;

    public ClearChatCommand() {
        super("clearchat", "Clears the server chat for players.", "base.command.clearchat");
        this.setAliases(new String[]{"cc"});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] messages = new String[CHAT_HEIGHT];
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(messages);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + sender.getName() + " cleared in-game chat.");
        return true;
    }
}
