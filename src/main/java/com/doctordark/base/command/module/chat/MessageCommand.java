package com.doctordark.base.command.module.chat;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.doctordark.util.BukkitUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
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
        Player target = BukkitUtils.playerWithNameOrUUID(args[0]);

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }

        String message = StringUtils.join(args, ' ', 1, args.length);
        Set<Player> recipients = Collections.singleton(target);

        PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false);
        Bukkit.getPluginManager().callEvent(playerMessageEvent);
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
