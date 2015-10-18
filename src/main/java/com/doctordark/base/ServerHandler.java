package com.doctordark.base;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ServerHandler {

    private final List<String> announcements = new ArrayList<>();
    private final List<String> serverRules = new ArrayList<>();

    public boolean useProtocolLib;
    private int announcementDelay;
    private long chatSlowedMillis;
    private long chatDisabledMillis;
    private int chatSlowedDelay;
    private String broadcastFormat;
    private FileConfiguration config;
    private boolean decreasedLagMode;

    private final BasePlugin plugin;

    public ServerHandler(BasePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        reloadServerData();
    }

    public int getAnnouncementDelay() {
        return this.announcementDelay;
    }

    public void setAnnouncementDelay(int delay) {
        this.announcementDelay = delay;
    }

    public List<String> getAnnouncements() {
        return this.announcements;
    }

    public boolean isChatSlowed() {
        return getRemainingChatSlowedMillis() > 0L;
    }

    public long getChatSlowedMillis() {
        return this.chatSlowedMillis;
    }

    public void setChatSlowedMillis(long ticks) {
        this.chatSlowedMillis = System.currentTimeMillis() + ticks;
    }

    public long getRemainingChatSlowedMillis() {
        return this.chatSlowedMillis - System.currentTimeMillis();
    }

    public boolean isChatDisabled() {
        return getRemainingChatDisabledMillis() > 0L;
    }

    public long getChatDisabledMillis() {
        return this.chatDisabledMillis;
    }

    public void setChatDisabledMillis(long ticks) {
        long millis = System.currentTimeMillis();
        this.chatDisabledMillis = (millis + ticks);
    }

    public long getRemainingChatDisabledMillis() {
        return this.chatDisabledMillis - System.currentTimeMillis();
    }

    public int getChatSlowedDelay() {
        return this.chatSlowedDelay;
    }

    public void setChatSlowedDelay(int delay) {
        this.chatSlowedDelay = delay;
    }

    public String getBroadcastFormat() {
        return this.broadcastFormat;
    }

    public void setBroadcastFormat(String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
    }

    public List<String> getServerRules() {
        return this.serverRules;
    }

    public boolean isDecreasedLagMode() {
        return decreasedLagMode;
    }

    public void setDecreasedLagMode(boolean decreasedLagMode) {
        this.decreasedLagMode = decreasedLagMode;
    }

    public void reloadServerData() {
        this.plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.serverRules.clear();
        for (String each : config.getStringList("server-rules")) {
            this.serverRules.add(ChatColor.translateAlternateColorCodes('&', each));
        }

        this.announcementDelay = config.getInt("announcements.delay", 300);
        this.announcements.clear();
        for (String each : config.getStringList("announcements.list")) {
            this.announcements.add(ChatColor.translateAlternateColorCodes('&', each));
        }

        this.chatDisabledMillis = config.getLong("chat.disabled.millis", 0L);
        this.chatSlowedMillis = config.getLong("chat.slowed.millis", 0L);
        this.chatSlowedDelay = config.getInt("chat.slowed.delay", 15);

        this.useProtocolLib = config.getBoolean("use-protocol-lib", true);
        this.decreasedLagMode = config.getBoolean("decreased-lag-mode");
        this.broadcastFormat = ChatColor.translateAlternateColorCodes('&', config.getString("broadcast.format", "[Base] &7%1$s"));
    }

    public void saveServerData() {
        config.set("server-rules", this.serverRules);
        config.set("use-protocol-lib", this.useProtocolLib);
        config.set("chat.disabled.millis", this.chatDisabledMillis);
        config.set("chat.slowed.millis", this.chatSlowedMillis);
        config.set("chat.slowed-delay", this.chatSlowedDelay);
        config.set("announcements.delay", this.announcementDelay);
        config.set("announcements.list", this.announcements);
        config.set("decreased-lag-mode", this.decreasedLagMode);
        plugin.saveConfig();
    }
}
