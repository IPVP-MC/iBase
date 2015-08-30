package com.doctordark.base;

import com.doctordark.base.command.CommandManager;
import com.doctordark.base.command.SimpleCommandManager;
import com.doctordark.base.command.module.ChatModule;
import com.doctordark.base.command.module.EssentialModule;
import com.doctordark.base.command.module.InventoryModule;
import com.doctordark.base.command.module.TeleportModule;
import com.doctordark.base.kit.FlatFileKitManager;
import com.doctordark.base.kit.Kit;
import com.doctordark.base.kit.KitExecutor;
import com.doctordark.base.kit.KitListener;
import com.doctordark.base.kit.KitManager;
import com.doctordark.base.listener.ChatListener;
import com.doctordark.base.listener.ColouredSignListener;
import com.doctordark.base.listener.DecreasedLagListener;
import com.doctordark.base.listener.JoinListener;
import com.doctordark.base.listener.MobstackListener;
import com.doctordark.base.listener.NameVerifyListener;
import com.doctordark.base.listener.PingListener;
import com.doctordark.base.listener.PlayerLimitListener;
import com.doctordark.base.listener.VanishListener;
import com.doctordark.base.task.AnnouncementHandler;
import com.doctordark.base.task.AutoRestartHandler;
import com.doctordark.base.user.BaseUser;
import com.doctordark.base.user.ConsoleUser;
import com.doctordark.base.user.NameHistory;
import com.doctordark.base.user.ServerParticipator;
import com.doctordark.base.user.UserManager;
import com.doctordark.base.warp.FlatFileWarpManager;
import com.doctordark.base.warp.Warp;
import com.doctordark.base.warp.WarpManager;
import com.doctordark.util.PersistableLocation;
import com.doctordark.util.SignHandler;
import com.doctordark.util.bossbar.BossBarManager;
import com.doctordark.util.chat.Lang;
import com.doctordark.util.cuboid.Cuboid;
import com.doctordark.util.cuboid.NamedCuboid;
import com.doctordark.util.itemdb.ItemDb;
import com.doctordark.util.itemdb.SimpleItemDb;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Random;

public class BasePlugin extends JavaPlugin {

    private static BasePlugin plugin;

    private ItemDb itemDb;
    private Random random = new Random();

    private AutoRestartHandler autoRestartHandler;
    private BukkitRunnable announcementTask;
    private CommandManager commandManager;
    private KitManager kitManager;
    private PlayTimeManager playTimeManager;
    private ServerHandler serverHandler;
    private SignHandler signHandler;
    private UserManager userManager;
    private WarpManager warpManager;

    private KitExecutor kitExecutor;

    @Override
    public void onEnable() {
        BasePlugin.plugin = this;

        ConfigurationSerialization.registerClass(Warp.class);
        ConfigurationSerialization.registerClass(ServerParticipator.class);
        ConfigurationSerialization.registerClass(BaseUser.class);
        ConfigurationSerialization.registerClass(ConsoleUser.class);
        ConfigurationSerialization.registerClass(NameHistory.class);
        ConfigurationSerialization.registerClass(PersistableLocation.class);
        ConfigurationSerialization.registerClass(Cuboid.class);
        ConfigurationSerialization.registerClass(NamedCuboid.class);
        ConfigurationSerialization.registerClass(Kit.class);

        registerManagers();
        registerCommands();
        registerListeners();
        reloadSchedulers();

        Plugin plugin = getServer().getPluginManager().getPlugin("ProtocolLib");
        if (plugin != null && plugin.isEnabled()) {
            try {
                ProtocolHook.hook(this);
            } catch (Exception ex) {
                getLogger().severe("Error hooking into ProtocolLib from Base.");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        BossBarManager.unhook();

        kitManager.saveKitData();
        playTimeManager.savePlaytimeData();
        serverHandler.saveServerData();
        signHandler.cancelTasks(null);
        userManager.saveParticipatorData();
        warpManager.saveWarpData();

        BasePlugin.plugin = null;
    }

    public static BasePlugin getPlugin() {
        return plugin;
    }

    private void registerManagers() {
        BossBarManager.hook();
        autoRestartHandler = new AutoRestartHandler(this);
        kitManager = new FlatFileKitManager(this);
        serverHandler = new ServerHandler(this);
        signHandler = new SignHandler(this);
        userManager = new UserManager(this);
        warpManager = new FlatFileWarpManager(this);

        this.itemDb = new SimpleItemDb(this);
        try {
            Lang.initialize("en_US");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void registerCommands() {
        commandManager = new SimpleCommandManager(this); //TODO: Configurable
        commandManager.registerAll(new ChatModule(this));
        commandManager.registerAll(new EssentialModule(this));
        commandManager.registerAll(new InventoryModule());
        commandManager.registerAll(new TeleportModule(this));

        getCommand("kit").setExecutor(kitExecutor = new KitExecutor(this));
    }

    public KitExecutor getKitExecutor() {
        return kitExecutor;
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ColouredSignListener(), this);
        manager.registerEvents(new DecreasedLagListener(this), this);
        manager.registerEvents(new JoinListener(this), this);
        manager.registerEvents(new KitListener(this), this);
        manager.registerEvents(new MobstackListener(this), this);
        manager.registerEvents(new NameVerifyListener(this), this);
        manager.registerEvents(new PingListener(), this);
        manager.registerEvents(playTimeManager = new PlayTimeManager(this), this);
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

    public Random getRandom() {
        return random;
    }

    public AutoRestartHandler getAutoRestartHandler() {
        return autoRestartHandler;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ItemDb getItemDb() {
        return itemDb;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public PlayTimeManager getPlayTimeManager() {
        return playTimeManager;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public SignHandler getSignHandler() {
        return signHandler;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }
}
