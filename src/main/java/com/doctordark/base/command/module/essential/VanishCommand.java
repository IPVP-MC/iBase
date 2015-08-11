package com.doctordark.base.command.module.essential;

import com.doctordark.base.BaseConstants;
import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import com.doctordark.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

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
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        } else {
            target = (Player) sender;
        }

        if (target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
            sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, args[0]));
            return true;
        }

        BaseUser baseUser = plugin.getUserManager().getUser(target.getUniqueId());
        boolean newVanished = !baseUser.isVanished() || (args.length >= 2 && Boolean.parseBoolean(args[1]));
        baseUser.setVanished(target, newVanished, true);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Vanish mode of " + target.getName() + " set to " + newVanished + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }
}
