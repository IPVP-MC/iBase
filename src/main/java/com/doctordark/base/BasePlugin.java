package com.doctordark.base;

import com.doctordark.base.cmd.CommandManager;
import com.doctordark.base.cmd.SimpleCommandManager;
import com.doctordark.base.cmd.module.ChatModule;
import com.doctordark.base.cmd.module.EssentialModule;
import com.doctordark.base.cmd.module.InventoryModule;
import com.doctordark.base.cmd.module.TeleportModule;
import com.doctordark.base.cmd.module.chat.messaging.MessageHandler;
import com.doctordark.base.listener.module.ChatListener;
import com.doctordark.base.listener.module.ColouredSignListener;
import com.doctordark.base.listener.module.NameVerifyListener;
import com.doctordark.base.listener.module.PingListener;
import com.doctordark.base.listener.module.RespawnListener;
import com.doctordark.base.listener.module.VanishListener;
import com.doctordark.base.manager.FlatFileServerManager;
import com.doctordark.base.manager.FlatFileUserManager;
import com.doctordark.base.manager.ServerManager;
import com.doctordark.base.manager.UserManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BasePlugin extends JavaPlugin {

    private CommandManager commandManager;
    private MessageHandler messageHandler;
    private ServerManager serverManager;
    private UserManager userManager;

    @Override
    public void onEnable() {
        super.onEnable();
        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
    }

    private void registerManagers() {
        commandManager = new SimpleCommandManager(this);
        messageHandler = new MessageHandler();
        serverManager = new FlatFileServerManager();
        userManager = new FlatFileUserManager();
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
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ColouredSignListener(), this);
        manager.registerEvents(new NameVerifyListener(), this);
        manager.registerEvents(new PingListener(), this);
        manager.registerEvents(new RespawnListener(), this);
        manager.registerEvents(new VanishListener(this), this);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
