package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.Main;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class CmdFilter implements CommandExecutor, Listener {
    private boolean enabled;
    private Long currentTime, divider;
    private int numHolder = 0;
    private Main plugin;

    public CmdFilter(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.enabled = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (enabled) {
                    enabled = false;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("filternotify.true")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lAuto filtering has been disabled."));
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 20 * 60 * 7);

        this.divider = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
    }

    public boolean canLogin() {
        if (currentTime == null) {
            return true;
        }

        return System.currentTimeMillis() - this.currentTime >= divider;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        if (this.enabled) {
            if (e.getPlayer().hasPermission("filterbypass.true") || e.getPlayer().isOp()) {
                return;
            }
            if (this.plugin.cmdDonorJoin.donorList.contains(e.getPlayer().getUniqueId().toString())) {
                return;
            }

            if (this.canLogin()) {
                if (numHolder >= 9) {
                    this.currentTime = System.currentTimeMillis();
                    numHolder = 0;
                } else {
                    numHolder++;
                }
            } else {
                e.disallow(Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', "&aPlease reconnect."));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String alias, String[] args) {
        if (!s.hasPermission("filter.true")) {
            s.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            s.sendMessage(ChatColor.RED + "Correct Usage: /" + c.getName() + " <start | stop>");
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            this.enabled = true;
            s.sendMessage(ChatColor.GREEN + "Filtering is now active.");
        } else if (args[0].equalsIgnoreCase("stop")) {
            this.enabled = false;
            s.sendMessage(ChatColor.RED + "Filtering is now disabled.");
        } else {
            s.sendMessage(ChatColor.RED + "Correct Usage: /" + c.getName() + " <start | stop>");
        }

        return true;
    }
}
