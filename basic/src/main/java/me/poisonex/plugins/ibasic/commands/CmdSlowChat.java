package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.IBasic;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdSlowChat implements CommandExecutor, Listener {

    private long slowChatTime = 0L;
    private BukkitTask bukkitTask;
    private final Map<UUID, Long> playerChatTimes = new HashMap<>();
    private final IBasic plugin;

    public CmdSlowChat(IBasic plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    private long convertToMillis(long time, TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(time, unit);
    }

    private String getTimeMessage(long time) {
        return DurationFormatUtils.formatDurationWords(time, true, true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission("slowchat.bypass")) {
            return;
        }

        if (this.slowChatTime > 0) {
            if (!this.playerChatTimes.containsKey(e.getPlayer().getUniqueId())) {
                this.playerChatTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + this.slowChatTime);
            } else {
                long timeRemaining = this.playerChatTimes.get(e.getPlayer().getUniqueId()) - System.currentTimeMillis();

                if (timeRemaining / 1000 > 0) {
                    e.setCancelled(true);

                    String timeMessage = this.getTimeMessage(timeRemaining);
                    e.getPlayer().sendMessage(ChatColor.RED + "Chat has been slowed. You can speak in " + timeMessage + ".");
                } else {
                    this.playerChatTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + this.slowChatTime);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <{seconds} | off>");
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (this.slowChatTime == 0) {
                sender.sendMessage(ChatColor.RED + "SlowChat is already off.");
                return true;
            }

            this.slowChatTime = 0;
            // sender.sendMessage(ChatColor.GREEN + "SlowChat is now off.");

            if (this.bukkitTask != null) {
                this.bukkitTask.cancel();
                this.bukkitTask = null;
            }

            this.playerChatTimes.clear();
            this.plugin.getServer().broadcastMessage(ChatColor.GRAY + "Chat is no longer being slowed.");
        } else {
            Integer time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(ChatColor.RED + "You must provide a valid number.");
                return true;
            }

            if (time == 0) {
                sender.sendMessage(ChatColor.RED + "You must supply a number greater than zero.");
                sender.sendMessage(ChatColor.RED + "If you want to turn off slowchat, use /slowchat off.");
                return true;
            }

            if (this.bukkitTask != null) {
                this.bukkitTask.cancel();
            }

            long alertIntervalTicks = TimeUnit.SECONDS.toMillis(30L) / 50;
            this.bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Collection<Permissible> permissibles = Bukkit.getPluginManager().getPermissionSubscriptions("slowchat.true");
                    String slowChatMessage = ChatColor.translateAlternateColorCodes('&', "&d&lThe chat is still being slowed (delay of " +
                            DurationFormatUtils.formatDurationWords(slowChatTime, true, true) + ").");
                    for (Permissible permissible : permissibles) {
                        if (permissible instanceof CommandSender) {
                            ((CommandSender) permissible).sendMessage(slowChatMessage);
                        }
                    }
                }
            }.runTaskTimer(this.plugin, alertIntervalTicks, alertIntervalTicks);

            this.slowChatTime = this.convertToMillis(time, TimeUnit.SECONDS);
            this.plugin.getServer().broadcastMessage(ChatColor.GRAY + "Chat has been slowed by " + sender.getName() + ".");
            // sender.sendMessage(ChatColor.GREEN + "SlowChat is now enabled for every " + this.getTimeMessage(this.slowChatTime) + ".");
        }

        return true;
    }
}
