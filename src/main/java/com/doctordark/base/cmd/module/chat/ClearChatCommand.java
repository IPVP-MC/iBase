package com.doctordark.base.cmd.module.chat;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Command used for clearing the server chat.
 */
public class ClearChatCommand extends BaseCommand {

    public ClearChatCommand() {
        super("clearchat", "Clears the server chat for players.", "base.command.clearchat");
        this.setAliases(new String[]{});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
        String[] messages = new String[101];

        for (Player player : players) {
            player.sendMessage(messages);
        }

        return true;
    }
}