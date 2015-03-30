package com.doctordark.base;

import com.doctordark.base.command.CommandManager;
import com.doctordark.base.command.SimpleCommandManager;
import com.doctordark.base.command.module.ChatModule;
import com.doctordark.base.command.module.EssentialModule;
import com.doctordark.base.command.module.InventoryModule;
import com.doctordark.base.command.module.TeleportModule;
import com.doctordark.base.listener.*;
import com.doctordark.base.task.AnnouncementHandler;
import com.doctordark.base.task.AutoRestartHandler;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.NameHistory;
import com.doctordark.base.user.ServerHandler;
import com.doctordark.base.user.UserManager;
import com.doctordark.base.util.PersistableLocation;
import com.doctordark.base.warp.FlatFileWarpManager;
import com.doctordark.base.warp.Warp;
import com.doctordark.base.warp.WarpManager;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BasePlugin
        extends JavaPlugin {
    private AutoRestartHandler autoRestartHandler;
    private BukkitRunnable announcementTask;
    private CommandManager commandManager;
    private ServerHandler serverHandler;
    private UserManager userManager;
    private WarpManager warpManager;

    @Override
    public void onEnable() {
        super.onEnable();

        ConfigurationSerialization.registerClass(Warp.class);
        ConfigurationSerialization.registerClass(BaseUser.class);
        ConfigurationSerialization.registerClass(NameHistory.class);
        ConfigurationSerialization.registerClass(PersistableLocation.class);

        registerManagers();
        registerCommands();
        registerListeners();
        reloadSchedulers();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        getServerHandler().saveServerData();
        getUserManager().saveUserData();
        getWarpManager().saveWarpData();
    }

    private void registerManagers() {
        autoRestartHandler = new AutoRestartHandler(this);
        serverHandler = new ServerHandler(this);
        userManager = new UserManager(this);
        warpManager = new FlatFileWarpManager(this);
    }

    private void registerCommands() {
        commandManager = new SimpleCommandManager(this);
        commandManager.registerAll(new ChatModule(this));
        commandManager.registerAll(new EssentialModule(this));
        commandManager.registerAll(new InventoryModule());
        commandManager.registerAll(new TeleportModule(this));
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ColouredSignListener(), this);
        manager.registerEvents(new JoinListener(this), this);
        manager.registerEvents(new NameVerifyListener(), this);
        manager.registerEvents(new PingListener(), this);
        manager.registerEvents(new PlayerLimitListener(this), this);
        manager.registerEvents(new VanishListener(this), this);
    }

    public void reloadSchedulers() {
        if (announcementTask != null) {
            announcementTask.cancel();
        }

        long announcementDelay = serverHandler.getAnnouncementDelay() * 20L;
        BukkitRunnable announcementRunnable = announcementTask = new AnnouncementHandler(this);
        announcementRunnable.runTaskTimer(this, announcementDelay, announcementDelay);
    }

    public AutoRestartHandler getAutoRestartHandler() {
        return this.autoRestartHandler;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ServerHandler getServerHandler() {
        return this.serverHandler;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public WarpManager getWarpManager() {
        return this.warpManager;
    }
}
