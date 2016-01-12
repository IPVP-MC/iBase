package org.ipvp.ibasic.commands;

import org.ipvp.ibasic.IBasic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.permissions.Permissible;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class CommandFilter implements CommandExecutor, Listener {

    private boolean enabled;
    private Long currentTime, divider;
    private int numHolder = 0;
    private final IBasic plugin;

    public CommandFilter(IBasic plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);

        this.enabled = plugin.isFilterOnStartup();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (enabled) {
                    enabled = false;

                    Collection<Permissible> permissibles = Bukkit.getPluginManager().getPermissionSubscriptions("filternotify.true");
                    for (Permissible permissible : permissibles) {
                        if (permissible instanceof CommandSender) {
                            CommandSender sender = (CommandSender) permissible;

                            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Auto filtering has been disabled.");
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 8400L);

        this.divider = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
    }

    public boolean canLogin() {
        return currentTime == null || System.currentTimeMillis() - this.currentTime >= divider;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (this.enabled) {
            Player player = event.getPlayer();
            if (player.hasPermission("filterbypass.true") || player.isOp()) {
                return;
            }

            if (this.plugin.getCommandDonorJoin().getDonorList().contains(player.getUniqueId())) {
                return;
            }

            if (!this.canLogin()) {
                event.disallow(Result.KICK_OTHER, ChatColor.GREEN + "Please reconnect.");
                return;
            }

            if (this.numHolder >= 9) {
                this.currentTime = System.currentTimeMillis();
                this.numHolder = 0;
            } else {
                this.numHolder++;
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                this.enabled = true;
                sender.sendMessage(ChatColor.GREEN + "Filtering is now active.");
                return true;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                this.enabled = false;
                sender.sendMessage(ChatColor.RED + "Filtering is now disabled.");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Correct Usage: /" + label + " <start | stop>");
        return true;
    }
}
