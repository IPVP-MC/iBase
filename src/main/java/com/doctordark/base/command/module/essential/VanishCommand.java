package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VanishCommand extends BaseCommand {

    private final BasePlugin plugin;

    public VanishCommand(BasePlugin plugin) {
        super("vanish", "Hide from other players.", "base.command.vanish");
        setAliases(new String[]{"v", "vis", "vanish", "invis"});
        setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player target;
        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getServer().getPlayer(args[0]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if ((target == null) || (((sender instanceof Player)) && (!((Player) sender).canSee(target)))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);

        boolean newVanished = !baseUser.isVanished() || (args.length >= 2 && Boolean.parseBoolean(args[1]));
        baseUser.setVanished(newVanished, true);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Vanish mode of " + target.getName() + " set to " + newVanished + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}
