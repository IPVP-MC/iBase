package com.doctordark.base.cmd.module.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager implements Listener {

    private final Map<UUID, Long> lastSpoken = new HashMap<UUID, Long>();

    private boolean enabledChat;
    private boolean slowedChat;
    private int slowedChatDelay;

    public ChatManager() {
        setSlowedChatDelay(15);
    }

    public boolean isEnabledChat() {
        return enabledChat;
    }

    public void setEnabledChat(boolean enabledChat) {
        this.enabledChat = enabledChat;
    }

    public boolean isSlowedChat() {
        return slowedChat;
    }

    public void setSlowedChat(boolean slowedChat) {
        this.slowedChat = slowedChat;
    }

    public int getSlowedChatDelay() {
        return slowedChatDelay;
    }

    public void setSlowedChatDelay(int slowedChatDelay) {
        this.slowedChatDelay = slowedChatDelay;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long millis = System.currentTimeMillis();

        if (!isEnabledChat() && !player.hasPermission("base.disabledchat.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Chat is currently disabled!");
        } else if (isSlowedChat() && !player.hasPermission("base.slowedchat.bypass")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Chat is currently slowed!");
        } else {
            lastSpoken.put(uuid, millis);
        }
    }

    private double getLastChat(UUID uuid) {
        return 0;
    }
}
