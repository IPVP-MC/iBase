package com.doctordark.base;

import com.doctordark.base.cmd.CommandManager;
import com.doctordark.base.cmd.SimpleCommandManager;
import com.doctordark.base.cmd.module.ChatModule;
import com.doctordark.base.cmd.module.EssentialModule;
import com.doctordark.base.cmd.module.InventoryModule;
import com.doctordark.base.cmd.module.TeleportModule;
import com.doctordark.base.cmd.module.chat.messaging.MessageHandler;
import com.doctordark.base.cmd.module.chat.messaging.MessageSpyListener;
import com.doctordark.base.listener.*;
import com.doctordark.base.manager.FlatFileServerManager;
import com.doctordark.base.manager.FlatFileUserManager;
import com.doctordark.base.manager.ServerManager;
import com.doctordark.base.manager.UserManager;
import com.doctordark.base.task.AnnouncementHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class BasePlugin extends JavaPlugin {

    private BukkitRunnable announcementTask;
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
        this.reloadSchedulers();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getServerManager().saveServerData();
        getUserManager().saveUserData();
    }

    private void registerManagers() {
        commandManager = new SimpleCommandManager(this);
        messageHandler = new MessageHandler();
        serverManager = new FlatFileServerManager(this);
        userManager = new FlatFileUserManager(this);
    }

    private void registerCommands() {
        getCommandManager().registerAll(new ChatModule(this));
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
        manager.registerEvents(new MessageSpyListener(this), this);
        manager.registerEvents(new PingListener(), this);
        manager.registerEvents(new PlayerLimitListener(this), this);
        manager.registerEvents(new RespawnListener(), this);
        manager.registerEvents(new VanishListener(this), this);
    }

    public void reloadSchedulers() {
        BukkitScheduler scheduler = getServer().getScheduler();

        if (announcementTask != null) {
            announcementTask.cancel();
        }

        long delay = serverManager.getAnnouncementDelay() * 20L;
        scheduler.runTaskTimer(this, (announcementTask = new AnnouncementHandler(this)), delay, delay);
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
