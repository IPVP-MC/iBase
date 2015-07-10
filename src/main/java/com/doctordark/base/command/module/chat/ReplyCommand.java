package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.doctordark.base.user.BaseUser;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReplyCommand extends BaseCommand {

    private static final long VANISH_REPLY_TIMEOUT = TimeUnit.SECONDS.toMillis(45L);
    private final BasePlugin plugin;

    public ReplyCommand(BasePlugin plugin) {
        super("reply", "Replies to the last conversing player.", "base.command.reply");
        this.setAliases(new String[]{"r", "respond"});
        this.setUsage("/(command) <message>");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);

        UUID lastReplied = baseUser.getLastRepliedTo();
        Player target = lastReplied == null ? null : Bukkit.getPlayer(lastReplied);
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            if (lastReplied != null && canSee(sender, target)) {
                sender.sendMessage(ChatColor.RED + "You are in a conversation with " + target.getName() + '.');
            }

            return true;
        }

        long millis = System.currentTimeMillis();
        if (target == null || (!canSee(sender, target) && millis - baseUser.getLastReceivedMessageMillis() > VANISH_REPLY_TIMEOUT)) {
            sender.sendMessage(ChatColor.GOLD + "There is no player to reply to.");
            return true;
        }

        String message = StringUtils.join(args, ' ', 0, args.length);
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