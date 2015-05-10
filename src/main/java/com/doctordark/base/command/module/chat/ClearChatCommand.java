package com.doctordark.base.command.module.chat;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {

    public ClearChatCommand() {
        super("clearchat", "Clears the server chat for players.", "base.command.clearchat");
        setAliases(new String[]{"cc"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] messages = new String[101];
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(messages);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + sender.getName() + " cleared in-game chat.");
        return true;
    }
}
