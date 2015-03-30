package com.doctordark.base.command.module.teleport;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BackCommand extends BaseCommand implements Listener {

    private final BasePlugin plugin;

    public BackCommand(BasePlugin plugin) {
        super("back", "Go to a players last known location.", "base.command.back");
        setAliases(new String[0]);
        setUsage("/(command) [playerName]");
        Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin = plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player target = args.length < 1 ? (Player) sender : Bukkit.getServer().getPlayer(args[0]);

        if ((target == null) || (!canSee(sender, target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }

        BaseUser targetUser = this.plugin.getUserManager().getUser(target.getUniqueId());
        Location previous = targetUser.getBackLocation();

        if (previous == null) {
            sender.sendMessage(ChatColor.RED + target.getName() + " doesn't have a back location.");
            return true;
        }

        Player player = (Player) sender;
        player.teleport(previous);

        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Teleported to back location of " + target.getName() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? null : Collections.emptyList();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        BaseUser baseUser = plugin.getUserManager().getUser(uuid);
        Location location = player.getLocation();
        baseUser.setBackLocation(location);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if ((cause == PlayerTeleportEvent.TeleportCause.COMMAND) || (cause == PlayerTeleportEvent.TeleportCause.UNKNOWN) || (cause == PlayerTeleportEvent.TeleportCause.PLUGIN)) {
            Player player = event.getPlayer();
            UUID uuid = player.getUniqueId();
            BaseUser baseUser = plugin.getUserManager().getUser(uuid);
            baseUser.setBackLocation(event.getFrom().clone());
        }
    }
}
