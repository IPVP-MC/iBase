package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.listener.VanishPriority;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.BukkitUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WhoisCommand extends BaseCommand {

    private final BasePlugin plugin;

    public WhoisCommand(BasePlugin plugin) {
        super("whois", "Check information about a player.", "base.command.whois");
        this.plugin = plugin;
        this.setAliases(new String[]{});
        this.setUsage("/(command) [playerName]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null || !canSee(sender, target)) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        Location location = target.getLocation();
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GREEN + " [" + target.getDisplayName() + ChatColor.GREEN + "]");
        sender.sendMessage(ChatColor.YELLOW + "  Health: " + ChatColor.GOLD + target.getHealth() + "/" + target.getMaxHealth());
        sender.sendMessage(ChatColor.YELLOW + "  Hunger: " + ChatColor.GOLD + target.getFoodLevel() + "/" + 20 + " (" + target.getSaturation() + " saturation)");
        sender.sendMessage(ChatColor.YELLOW + "  Exp/Level: " + ChatColor.GOLD + target.getExp() + "/" + target.getLevel());
        sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.GOLD + world.getName() + " " +
                ChatColor.GRAY + "[" + WordUtils.capitalizeFully(world.getEnvironment().name().replace('_', ' ')) + "] " +
                ChatColor.GOLD + "(" + x + ", " + y + ", " + z + ")");
        sender.sendMessage(ChatColor.YELLOW + "  Vanished: " + ChatColor.GOLD + baseUser.isVanished() + " (priority=" + VanishPriority.of(target).getPriorityLevel() + ")");
        sender.sendMessage(ChatColor.YELLOW + "  Staff Chat: " + ChatColor.GOLD + baseUser.isInStaffChat());
        sender.sendMessage(ChatColor.YELLOW + "  Operator: " + ChatColor.GOLD + target.isOp());
        sender.sendMessage(ChatColor.YELLOW + "  Game Mode: " + ChatColor.GOLD + WordUtils.capitalizeFully(target.getGameMode().name().replace('_', ' ')));
        sender.sendMessage(ChatColor.YELLOW + "  Idle Time: " + ChatColor.GOLD + DurationFormatUtils.formatDurationWords(BukkitUtils.getIdleTime(target), true, true));
        sender.sendMessage(ChatColor.YELLOW + "  IP Address: " + ChatColor.GOLD + target.getAddress().getHostString());
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return (args.length == 1) ? null : Collections.<String>emptyList();
    }
}