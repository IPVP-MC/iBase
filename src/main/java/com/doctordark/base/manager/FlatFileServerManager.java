package com.doctordark.base.manager;

import com.doctordark.base.BasePlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class FlatFileServerManager implements ServerManager {

    private boolean chatEnabled;
    private boolean chatSlowed;
    private int chatSlowedDelay;

    private final BasePlugin plugin;

    public FlatFileServerManager(BasePlugin plugin) {
        this.plugin = plugin;
        this.loadData();
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
    public void loadData() {
        FileConfiguration config = plugin.getConfig();
        if (config.getConfigurationSection("chat") != null) {
            this.chatEnabled = config.getBoolean("chat.enabled", true);
            this.chatSlowed = config.getBoolean("chat.slowed", true);
            this.chatSlowedDelay = config.getInt("chat.slowed-delay", 15);
        }
    }

    @Override
    public void saveData() {
        FileConfiguration config = plugin.getConfig();
        config.set("chat.enabled", chatEnabled);
        config.set("chat.slowed", chatSlowed);
        config.set("chat.slowed-delay", chatSlowedDelay);
        plugin.saveConfig();
    }
}
