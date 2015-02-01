package com.doctordark.base.manager;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.util.GenericUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class FlatFileUserManager implements UserManager {

    private final Set<String> vanished = new HashSet<String>();
    private final Set<String> staffChatting = new HashSet<String>();

    private final Map<String, Long> lastSpeakTime = new HashMap<String, Long>();
    private final Map<String, List<String>> messageSpyMap = new HashMap<String, List<String>>();
    private final Map<String, List<String>> ipHistoryMap = new HashMap<String, List<String>>();

    private final BasePlugin plugin;

    public FlatFileUserManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.reloadUserData();
    }




    // Staff Chat Methods

    @Override
    public Set<String> getStaffChatting() {
        return staffChatting;
    }

    @Override
    public boolean isInStaffChat(UUID uuid) {
        String id = uuid.toString();
        return staffChatting.contains(id);
    }

    @Override
    public void setInStaffChat(UUID uuid, boolean chat) {
        String id = uuid.toString();

        if (chat) {
            staffChatting.add(id);
        } else {
            staffChatting.remove(id);
        }
    }



    
    // Vanish Methods

    @Override
    public Set<String> getVanished() {
        return vanished;
    }

    @Override
    public boolean isVanished(UUID uuid) {
        String id = uuid.toString();
        return vanished.contains(id);
    }

    @Override
    public void setVanished(UUID uuid, boolean vanish) {
        String id = uuid.toString();

        if (vanish) {
            vanished.add(id);
        } else {
            vanished.remove(id);
        }

        // Show the update status for the players.
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (player == null) {
            return;
        }

        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            if (!target.hasPermission("base.command.vanish")) {
                if (vanish) {
                    target.hidePlayer(player);
                } else {
                    target.showPlayer(player);
                }
            }
        }
    }




    // Chat methods, slow chat, disabled chat, etc.

    @Override
    public Map<String, Long> getLastSpeakMap() {
        return lastSpeakTime;
    }

    @Override
    public long getRemainingChatDelayTime(UUID uuid) {
        String id = uuid.toString();
        int slowChatDelay = plugin.getServerManager().getSlowChatDelay();
        long millis = System.currentTimeMillis();
        return lastSpeakTime.containsKey(id) ? (lastSpeakTime.get(id) - millis) : 0L;
    }

    @Override
    public long getLastSpeakTime(UUID uuid) {
        String id = uuid.toString();
        return lastSpeakTime.containsKey(id) ? lastSpeakTime.get(id) : 0L;
    }

    @Override
    public void updateLastSpeakTime(UUID uuid) {
        String id = uuid.toString();
        int slowChatDelay = plugin.getServerManager().getSlowChatDelay();
        long millis = System.currentTimeMillis();
        long difference = millis + TimeUnit.SECONDS.toMillis(slowChatDelay);
        lastSpeakTime.put(id, difference);
    }




    // Message spying.

    @Override
    public Map<String, List<String>> getMessageSpyMap() {
        return messageSpyMap;
    }

    @Override
    public List<String> getMessageSpying(UUID uuid) {
        String id = uuid.toString();

        if (!messageSpyMap.containsKey(id)) {
            messageSpyMap.put(id, new ArrayList<String>());
        }

        return messageSpyMap.get(id);
    }




    // IP Address management.

    @Override
    public Map<String, List<String>> getAddressMap() {
        return ipHistoryMap;
    }

    @Override
    public List<String> getAddresses(UUID uuid) {
        String id = uuid.toString();

        if (!ipHistoryMap.containsKey(id)) {
            ipHistoryMap.put(id, new ArrayList<String>());
        }

        return ipHistoryMap.get(id);
    }

    @Override
    public void removeAddress(UUID uuid, String address) {
        String id = uuid.toString();
        List<String> histories = getAddresses(uuid);
        if (histories.contains(address)) {
            histories.remove(address);
        }
    }

    @Override
    public void saveAddress(UUID uuid, String address) {
        String id = uuid.toString();
        List<String> histories = getAddresses(uuid);
        if (!histories.contains(address)) {
            histories.add(address);
        }
    }




    // Data persistence handling.

    @Override
    public void reloadUserData() {
        FileConfiguration config = plugin.getConfig();

        // Load the list of vanished-ers.
        List<String> list = config.getStringList("userdata.vanished");
        if (list != null) vanished.addAll(list);

        // Load the list of staff-chatters.
        list = config.getStringList("userdata.staffchat");
        if (list != null) staffChatting.addAll(list);

        // Load the last speak timestamp data for players.
        Object object = config.get("userdata.last-speak");
        if (object != null && object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                lastSpeakTime.put(id, config.getLong("userdata.last-speak." + id));
            }
        }

        // Load the message spy data for players.
        object = config.get("userdata.message-spy");
        if (object != null && object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                messageSpyMap.put(id, GenericUtils.castList(config.get("userdata.message-spy." + id), String.class));
            }
        }

        // Load the IP data for players.
        object = config.get("userdata.ip-history");
        if (object != null && object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                ipHistoryMap.put(id, GenericUtils.castList(config.get("userdata.ip-history." + id), String.class));
            }
        }
    }

    @Override
    public void saveUserData() {
        FileConfiguration config = plugin.getConfig();
        config.set("userdata.vanished", new ArrayList<String>(vanished));
        config.set("userdata.staffchat", new ArrayList<String>(staffChatting));

        config.set("userdata.last-speak", lastSpeakTime);
        config.set("userdata.message-spy", messageSpyMap);
        config.set("userdata.ip-history", ipHistoryMap);
        plugin.saveConfig();
    }
}
