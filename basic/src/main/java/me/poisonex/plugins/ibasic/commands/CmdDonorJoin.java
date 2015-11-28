package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.Main;
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
import java.util.List;
import java.util.UUID;

public class CmdDonorJoin implements CommandExecutor, Listener {
    private Main plugin;
    private File file;
    private FileConfiguration config;
    public List<String> donorList;

    public CmdDonorJoin(Main plugin) {
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
        this.donorList = this.config.getStringList("donorList");

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult() == Result.KICK_FULL) {
            if (this.donorList.contains(e.getPlayer().getUniqueId().toString())) {
                e.allow();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("donorjoin.true")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <add|list|remove> [player]");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a player.");
                return true;
            }

            UUID targetUUID = this.plugin.uuidManager.getUUIDFromName(args[1]);

            if (targetUUID == null) {
                targetUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
            }

            if (targetUUID == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (!this.donorList.contains(targetUUID.toString())) {
                this.donorList.add(targetUUID.toString());
                this.saveConfig();

                sender.sendMessage(ChatColor.GREEN + "Successfully allowed player to join the server when full.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player is already allowed to join when the server is full.");
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            StringBuilder sb = new StringBuilder();

            if (this.donorList.isEmpty()) {
                sb.append(ChatColor.GRAY + "None");
            } else {
                for (String uuidString : this.donorList) {
                    UUID uuid = UUID.fromString(uuidString);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                    if (offlinePlayer != null) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }

                        sb.append(offlinePlayer.getName());
                    }
                }
            }

            sender.sendMessage(ChatColor.GREEN + "Donor List: " + sb.toString());
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Please specify a player.");
                return true;
            }

            UUID targetUUID = this.plugin.uuidManager.getUUIDFromName(args[1]);

            if (targetUUID == null) {
                targetUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
            }

            if (targetUUID == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (this.donorList.remove(targetUUID.toString())) {
                this.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Successfully removed player from being allowed to join the server when full.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player was never on the donor list.");
            }
        }

        return true;
    }

    public void saveConfig() {
        try {
            this.config.set("donorList", this.donorList);
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
