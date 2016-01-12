package org.ipvp.ibasic.commands;

import com.google.common.base.Joiner;
import com.sk89q.squirrelid.Profile;
import lombok.Getter;
import org.ipvp.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CommandDonorJoin implements CommandExecutor, Listener {

    private IBasic plugin;

    @Getter
    private final Set<UUID> donorList = new LinkedHashSet<>();

    public CommandDonorJoin(IBasic plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.KICK_FULL && this.donorList.contains(event.getPlayer().getUniqueId())) {
            event.allow();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Please specify a player.");
                    return true;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        UUID targetUUID = null;

                        try {
                            Profile profile = plugin.getResolver().findByName(args[1]);
                            if (profile != null) {
                                targetUUID = profile.getUniqueId();
                            }
                        } catch (IOException | InterruptedException ignored) {
                        }

                        if (targetUUID == null) {
                            sender.sendMessage(ChatColor.RED + "Could not find correspondent UUID for that username.");
                            return;
                        }

                        UUID finalUUID = targetUUID;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (donorList.add(finalUUID)) {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully allowed player with UUID '" + finalUUID + "' to join the server when full.");
                                    return;
                                }

                                sender.sendMessage(ChatColor.RED + "Player with UUID '" + finalUUID + "' is already allowed to join when the server is full.");
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                List<String> donorNames = new ArrayList<>();
                if (!this.donorList.isEmpty()) {
                    for (UUID uuid : this.donorList) {
                        Profile profile = plugin.getCache().getIfPresent(uuid);
                        if (profile != null) {
                            donorNames.add(profile.getName());
                        }
                    }
                }

                sender.sendMessage(ChatColor.GREEN + "Donor List: " + ChatColor.GRAY + (donorNames.isEmpty() ? "None" : Joiner.on(", ").join(donorNames)));
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Please specify a player.");
                    return true;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        UUID targetUUID = null;

                        try {
                            Profile profile = plugin.getResolver().findByName(args[1]);
                            if (profile != null) {
                                targetUUID = profile.getUniqueId();
                            }
                        } catch (IOException | InterruptedException ignored) {
                        }

                        if (targetUUID == null) {
                            sender.sendMessage(ChatColor.RED + "Could not find correspondent UUID for that username.");
                            return;
                        }

                        UUID finalUUID = targetUUID;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (donorList.remove(finalUUID)) {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully removed player with UUID '" + finalUUID + "' from being allowed to join the server when full.");
                                    return;
                                }

                                sender.sendMessage(ChatColor.RED + "Player with UUID '" + finalUUID + "' was never on the donor list.");
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Correct Usage: /" + label + " <add|list|remove> [player]");
        return true;
    }
}
