package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class MessageCommand extends BaseCommand {

    public MessageCommand(BasePlugin plugin) {
        super("message", "Sends a message to a recipient(s).", "base.command.message");
        setAliases(new String[]{"msg", "m", "whisper", "w", "tell"});
        setUsage("/(command) <playerName> [text...]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        String message = builder.toString();
        Set<Player> recipients = Sets.newHashSet(target);

        PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false);
        Bukkit.getServer().getPluginManager().callEvent(playerMessageEvent);
        if (!playerMessageEvent.isCancelled()) {
            playerMessageEvent.send();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
