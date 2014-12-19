package com.doctordark.base;

import com.doctordark.base.cmd.CommandManager;
import com.doctordark.base.cmd.SimpleCommandManager;
import com.doctordark.base.cmd.module.ChatModule;
import com.doctordark.base.cmd.module.EssentialModule;
import com.doctordark.base.cmd.module.InventoryModule;
import com.doctordark.base.cmd.module.TeleportModule;
import com.doctordark.base.cmd.module.chat.ChatManager;
import com.doctordark.base.cmd.module.chat.messaging.MessageHandler;
import com.doctordark.base.listener.module.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BasePlugin extends JavaPlugin {

    private ChatManager chatManager;
    private CommandManager commandManager;
    private MessageHandler messageHandler;

    @Override
    public void onEnable() {
        super.onEnable();
        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
    }

    private void registerManagers() {
        chatManager = new ChatManager();
        commandManager = new SimpleCommandManager(this);
        messageHandler = new MessageHandler();
    }

    private void registerCommands() {
        getCommandManager().registerAll(new ChatModule());
        getCommandManager().registerAll(new EssentialModule(this));
        getCommandManager().registerAll(new InventoryModule());
        getCommandManager().registerAll(new TeleportModule());
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(messageHandler, this);
        manager.registerEvents(new ChatListener(), this);
        manager.registerEvents(new ColouredSignListener(), this);
        manager.registerEvents(new NameVerifyListener(), this);
        manager.registerEvents(new PingListener(), this);
        manager.registerEvents(new RespawnListener(), this);
        manager.registerEvents(new VanishListener(), this);
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
