package com.doctordark.base.manager;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FlatFileUserManager implements UserManager {

    private Map<String, Long> lastSpeakTime = new HashMap<String, Long>();
    private Set<String> vanished = new HashSet<String>();
    private Set<String> staffChat = new HashSet<String>();
    private final BasePlugin plugin;

    public FlatFileUserManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.loadData();
    }

    @Override
    public boolean isInStaffChat(UUID uuid) {
        return staffChat.contains(uuid.toString());
    }

    @Override
    public void setInStaffChat(UUID uuid, boolean chat) {
        if (chat) {
            staffChat.add(uuid.toString());
        } else {
            staffChat.remove(uuid.toString());
        }
    }

    @Override
    public boolean isVanished(UUID uuid) {
        return vanished.contains(uuid.toString());
    }

    @Override
    public void setVanished(UUID uuid, boolean vanish) {
        if (vanish) {
            vanished.add(uuid.toString());
        } else {
            vanished.remove(uuid.toString());
        }

        // Show the update status for the players.
        Player player = Bukkit.getServer().getPlayer(uuid);
        if (player != null) {
            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

            for (Player target : players) {
                if (!target.hasPermission("base.command.vanish")) {
                    if (vanish) {
                        target.hidePlayer(player);
                    } else {
                        target.showPlayer(player);
                    }
                }
            }
        }
    }

    @Override
    public double getRemainingChatTime(UUID uuid) {
        String id = uuid.toString();
        long millis = System.currentTimeMillis();
        return lastSpeakTime.containsKey(id) ? ((lastSpeakTime.get(id) / 1000.0) + 15.0) - (millis / 1000.0) : 0.0;
    }

    @Override
    public long getLastChatTime(UUID uuid) {
        String id = uuid.toString();
        return lastSpeakTime.containsKey(id) ? lastSpeakTime.get(id) : 0L;
    }

    @Override
    public void setLastChatTime(UUID uuid, long millis) {
        lastSpeakTime.put(uuid.toString(), millis);
    }

    @Override
    public void loadData() {
        FileConfiguration config = plugin.getConfig();

        try {
            this.vanished = new HashSet<String>(config.getStringList("userdata.vanished"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            this.staffChat = new HashSet<String>(config.getStringList("userdata.staffchat"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.lastSpeakTime = new HashMap<String, Long>();
    }

    @Override
    public void saveData() {
        FileConfiguration config = plugin.getConfig();
        config.set("userdata.vanished", new ArrayList<String>(vanished));
        config.set("userdata.staffchat", new ArrayList<String>(staffChat));
        plugin.saveConfig();
    }
}
