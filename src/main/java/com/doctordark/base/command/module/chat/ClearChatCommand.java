package com.doctordark.base.command.module.chat;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ClearChatCommand extends BaseCommand {

    public ClearChatCommand() {
        super("clearchat", "Clears the server chat for players.", "base.command.clearchat");
        setAliases(new String[]{"cc"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        String[] messages = new String[101];
        for (Player player : players) {
            player.sendMessage(messages);
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + sender.getName() + " cleared in-game chat.");
        return true;
    }
}
