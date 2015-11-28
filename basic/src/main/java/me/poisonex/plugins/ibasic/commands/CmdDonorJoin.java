package me.poisonex.plugins.ibasic.commands;

import com.google.common.base.Joiner;
import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class CmdDonorJoin implements CommandExecutor, Listener {
    private IBasic plugin;
    private File file;
    private FileConfiguration config;

    public CmdDonorJoin(IBasic plugin) {
        this.plugin = plugin;

        this.file = new File(this.plugin.getDataFolder(), "donorjoin.yml");

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() == Result.KICK_FULL && plugin.getDonatorList().contains(event.getPlayer().getUniqueId().toString())) {
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

                UUID targetUUID = this.plugin.getUuidManager().getUUIDFromName(args[1]);

                if (targetUUID == null) {
                    targetUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                }

                if (targetUUID == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }

                if (!plugin.getDonatorList().contains(targetUUID.toString())) {
                    plugin.getDonatorList().add(targetUUID.toString());
                    this.saveConfig();

                    sender.sendMessage(ChatColor.GREEN + "Successfully allowed player to join the server when full.");
                } else {
                    sender.sendMessage(ChatColor.RED + "That player is already allowed to join when the server is full.");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                Set<String> builder = new LinkedHashSet<>();
                if (!plugin.getDonatorList().isEmpty()) {
                    for (String uuidString : plugin.getDonatorList()) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
                        if (offlinePlayer != null) {
                            builder.add(offlinePlayer.getName());
                        }
                    }
                }

                sender.sendMessage(builder.isEmpty() ? ChatColor.GRAY + "None" : ChatColor.GREEN + "Donor List: " + Joiner.on(", ").join(builder));
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Please specify a player.");
                    return true;
                }

                UUID targetUUID = this.plugin.getUuidManager().getUUIDFromName(args[1]);

                if (targetUUID == null) {
                    targetUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                }

                if (targetUUID == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }

                if (plugin.getDonatorList().remove(targetUUID.toString())) {
                    this.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Successfully removed player from being allowed to join the server when full.");
                } else {
                    sender.sendMessage(ChatColor.RED + "That player was never on the donor list.");
                }

                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Correct Usage: /" + command.getName() + " <add|list|remove> [player]");
        return true;
    }

    public void saveConfig() {
        try {
            this.config.set("donorList", plugin.getDonatorList());
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
