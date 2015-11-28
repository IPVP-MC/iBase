package me.poisonex.plugins.ibasic.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UUIDManager implements Listener {

    private IBasic IBasicPlugin;

    private File file;

    private FileConfiguration config;

    private final BiMap<UUID, String> storedUUIDs;

    public UUIDManager(IBasic IBasicPlugin) {
        this.IBasicPlugin = IBasicPlugin;
        this.file = new File(this.IBasicPlugin.getDataFolder(), "uuids.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.storedUUIDs = HashBiMap.create();

        this.IBasicPlugin.getServer().getPluginManager().registerEvents(this, this.IBasicPlugin);
    }

    public void set(final String path, final Object value) {
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    config.set(path, value);
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.runTaskAsynchronously(this.IBasicPlugin);
    }

    public String getNameFromUUID(final UUID uuid) {
        if (this.storedUUIDs.containsKey(uuid)) return this.storedUUIDs.get(uuid);

        String foundName = this.config.getString(uuid.toString(), null);

        if (foundName == null) {
            this.storedUUIDs.forcePut(uuid, null);
        }

        return foundName;
    }

    public UUID getUUIDFromName(final String name) {
        if (this.storedUUIDs.containsValue(name)) return this.storedUUIDs.inverse().get(name);

        UUID foundUUID = null;

        for (String playerUUID : this.config.getKeys(false)) {
            String playerName = this.config.getString(playerUUID);

            if (name.equalsIgnoreCase(playerName)) {
                foundUUID = UUID.fromString(playerUUID);
                this.storedUUIDs.forcePut(foundUUID, playerName);
                break;
            }
        }

        return foundUUID;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        this.set(e.getUniqueId().toString(), e.getName());
        this.storedUUIDs.forcePut(e.getUniqueId(), e.getName());
    }

}
