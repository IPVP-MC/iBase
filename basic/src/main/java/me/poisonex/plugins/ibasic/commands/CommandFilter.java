package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.IBasic;
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

import java.util.concurrent.TimeUnit;

public class CommandFilter implements CommandExecutor, Listener {

    private static final long DIVIDER = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);

    private boolean enabled;
    private long currentTime = -1L;
    private int numHolder = 0;

    private final IBasic plugin;

    public CommandFilter(IBasic plugin) {
        this.plugin = plugin;
        this.enabled = true;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (enabled) {
                    enabled = false;

                    String message = ChatColor.RED.toString() + ChatColor.BOLD + "Auto filtering has been disabled.";
                    for (Permissible permissible : plugin.getServer().getPluginManager().getPermissionSubscriptions("filternotify.true")) {
                        if (permissible instanceof Player) {
                            ((Player) permissible).sendMessage(message);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 240L);
    }

    public boolean canLogin() {
        return this.currentTime != -1L || System.currentTimeMillis() - this.currentTime >= DIVIDER;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        if (this.enabled) {
            Player player = event.getPlayer();
            if (player.hasPermission("filterbypass.true") || this.plugin.getDonatorList().contains(event.getPlayer().getUniqueId().toString())) {
                return;
            }

            if (!this.canLogin()) {
                event.disallow(Result.KICK_OTHER, ChatColor.GREEN + "Please reconnect.");
                return;
            }

            if (numHolder >= 9) {
                this.currentTime = System.currentTimeMillis();
                this.numHolder = 0;
            } else {
                this.numHolder++;
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + command.getName() + " <start | stop>");
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            this.enabled = true;
            sender.sendMessage(ChatColor.GREEN + "Filtering is now active.");
        } else if (args[0].equalsIgnoreCase("stop")) {
            this.enabled = false;
            sender.sendMessage(ChatColor.RED + "Filtering is now disabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + command.getName() + " <start | stop>");
        }

        return true;
    }
}
