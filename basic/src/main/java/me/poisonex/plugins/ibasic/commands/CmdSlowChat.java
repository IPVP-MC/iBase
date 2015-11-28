package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.Main;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdSlowChat implements CommandExecutor, Listener {
    private Main plugin;
    private long slowChatTime;
    private BukkitTask bukkitTask;
    private HashMap<UUID, Long> playerChatTimes;

    public CmdSlowChat(Main plugin) {
        this.plugin = plugin;

        this.slowChatTime = 0;
        this.playerChatTimes = new HashMap<UUID, Long>();

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
        if (!sender.hasPermission("slowchat.true")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <{seconds} | off>");
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (slowChatTime == 0) {
                sender.sendMessage(ChatColor.RED + "SlowChat is already off.");
            } else {
                slowChatTime = 0;
//				sender.sendMessage(ChatColor.GREEN + "SlowChat is now off.");

                if (this.bukkitTask != null) {
                    this.bukkitTask.cancel();
                    this.bukkitTask = null;
                }

                this.playerChatTimes.clear();
                this.plugin.getServer().broadcastMessage(ChatColor.GRAY + "Chat is no longer being slowed.");
            }
        } else {
            try {
                Integer time = Integer.parseInt(args[0]);

                if (time == 0) {
                    sender.sendMessage(ChatColor.RED + "You must supply a number greater than zero.");
                    sender.sendMessage(ChatColor.RED + "If you want to turn off slowchat, use /slowchat off.");
                } else {
                    if (this.bukkitTask != null) {
                        this.bukkitTask.cancel();
                    }

                    this.bukkitTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p.hasPermission("slowchat.true")) {
                                    String slowChatMessage = DurationFormatUtils.formatDurationWords(slowChatTime, true, true);

                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d&lThe chat is still being slowed (delay of " + slowChatMessage + ")."));
                                }
                            }
                        }
                    }.runTaskTimer(this.plugin, 5 * 60 * 20, 5 * 60 * 20);

                    this.slowChatTime = this.convertToMillis(time, TimeUnit.SECONDS);
//					sender.sendMessage(ChatColor.GREEN + "SlowChat is now enabled for every " + this.getTimeMessage(this.slowChatTime) + ".");
                    this.plugin.getServer().broadcastMessage(ChatColor.GRAY + "Chat has been slowed by " + sender.getName() + ".");
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "You must provide a valid number.");
            }
        }

        return true;
    }
}
