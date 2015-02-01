package com.doctordark.base.manager;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class FlatFileServerManager implements ServerManager {

    private final List<String> announcements = new ArrayList<String>();
    private int announcementDelay;
    private boolean chatEnabled;
    private boolean chatSlowed;
    private int chatSlowedDelay;
    private int maxPlayers;

    private final FileConfiguration config;
    private final BasePlugin plugin;

    public FlatFileServerManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.reloadServerData();
    }

    @Override
    public int getAnnouncementDelay() {
        return announcementDelay;
    }

    @Override
    public void setAnnouncementDelay(int delay) {
        this.announcementDelay = delay;
    }

    @Override
    public List<String> getAnnouncements() {
        return announcements;
    }

    @Override
    public boolean isChatEnabled() {
        return chatEnabled;
    }

    @Override
    public void setChatEnabled(boolean enabled) {
        this.chatEnabled = enabled;
    }

    @Override
    public boolean isChatSlowed() {
        return chatSlowed;
    }

    @Override
    public void setChatSlowed(boolean slowed) {
        this.chatSlowed = slowed;
    }

    @Override
    public int getSlowChatDelay() {
        return chatSlowedDelay;
    }

    @Override
    public void setSlowChatDelay(int delay) {
        this.chatSlowedDelay = delay;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void reloadServerData() {
        this.announcements.clear();

        if (config.getConfigurationSection("chat") != null) {
            this.chatEnabled = config.getBoolean("chat.enabled", true);
            this.chatSlowed = config.getBoolean("chat.slowed", true);
            this.chatSlowedDelay = config.getInt("chat.slowed-delay", 15);
        }

        this.announcements.addAll(config.getStringList("announcements.list"));
        this.announcementDelay = config.getInt("announcements.delay", 60 * 5);
        this.maxPlayers = config.getInt("max-players", Bukkit.getMaxPlayers());
    }

    @Override
    public void saveServerData() {
        config.set("chat.enabled", isChatEnabled());
        config.set("chat.slowed", isChatSlowed());
        config.set("chat.slowed-delay", getSlowChatDelay());
        config.set("announcements.list", getAnnouncements());
        config.set("announcements.delay", getAnnouncementDelay());
        config.set("max-players", getMaxPlayers());
        plugin.saveConfig();
    }
}
