package com.doctordark.base.user;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager implements Listener {

    private final Config userConfig;
    private final Map<String, BaseUser> users;

    public UserManager(BasePlugin plugin) {
        this.users = new HashMap<>();
        this.userConfig = new Config(plugin, "users");
        reloadUserData();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String id = uuid.toString();
        if (!this.users.containsKey(id)) {
            this.users.put(id, new BaseUser(uuid));
        }
    }

    public Map<String, BaseUser> getUsers() {
        return this.users;
    }

    public BaseUser getUser(UUID uuid) {
        String id = uuid.toString();
        if (!this.users.containsKey(id)) {
            this.users.put(id, new BaseUser(uuid));
        }
        return this.users.get(id);
    }

    public void reloadUserData() {
        this.users.clear();

        Object object = this.userConfig.get("users");
        if ((object != null) && ((object instanceof MemorySection))) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                BaseUser baseUser = (BaseUser) this.userConfig.get("users." + id);
                this.users.put(id, baseUser);
            }
        }
    }

    public void saveUserData() {
        for (BaseUser user : this.users.values()) {
            String id = user.getUserUUID().toString();
            userConfig.set("users." + id, user);
        }

        userConfig.save();
    }
}
