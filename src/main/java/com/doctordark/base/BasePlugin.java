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
import com.doctordark.util.bossbar.BossBarHandler;
import com.doctordark.util.chat.Lang;
import com.doctordark.util.cuboid.Cuboid;
import com.doctordark.util.cuboid.NamedCuboid;
import com.doctordark.util.itemdb.ItemDb;
import com.doctordark.util.itemdb.SimpleItemDb;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Random;

public class BasePlugin extends JavaPlugin {

    @Getter
    private static BasePlugin plugin;

    @Getter
    private Random random = new Random();

    @Getter
    private AutoRestartHandler autoRestartHandler;

    @Getter
    private BossBarHandler bossBarHandler;

    @Getter
    private BukkitRunnable announcementTask;

    @Getter
    private CommandManager commandManager;

    @Getter
    private ItemDb itemDb;

    @Getter
    private KitExecutor kitExecutor;

    @Getter
    private KitManager kitManager;

    @Getter
    private PlayTimeManager playTimeManager;

    @Getter
    private ServerHandler serverHandler;

    @Getter
    private SignHandler signHandler;

    @Getter
    private UserManager userManager;

    @Getter
    private WarpManager warpManager;

    private void registerManagers() {
        this.autoRestartHandler = new AutoRestartHandler(this);
        this.bossBarHandler = new BossBarHandler(this);
        this.itemDb = new SimpleItemDb(this);
        this.kitManager = new FlatFileKitManager(this);
        this.serverHandler = new ServerHandler(this);
        this.signHandler = new SignHandler(this);
        this.userManager = new UserManager(this);
        this.warpManager = new FlatFileWarpManager(this);

        try {
            Lang.initialize("en_US");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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

        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
        this.reloadSchedulers();

        Plugin plugin = this.getServer().getPluginManager().getPlugin("ProtocolLib");
        if (plugin != null && plugin.isEnabled()) {
            try {
                ProtocolHook.hook(this);
            } catch (NoSuchMethodError ex) {
                this.getLogger().severe("Error hooking into ProtocolLib from Base.");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        this.kitManager.saveKitData();
        this.playTimeManager.savePlaytimeData();
        this.serverHandler.saveServerData();
        this.signHandler.cancelTasks(null);
        this.userManager.saveParticipatorData();
        this.warpManager.saveWarpData();

        BasePlugin.plugin = null;
    }

    private void registerCommands() {
        this.commandManager = new SimpleCommandManager(this); //TODO: Each module to be configurable
        this.commandManager.registerAll(new ChatModule(this));
        this.commandManager.registerAll(new EssentialModule(this));
        this.commandManager.registerAll(new InventoryModule(this));
        this.commandManager.registerAll(new TeleportModule(this));
        this.getCommand("kit").setExecutor(this.kitExecutor = new KitExecutor(this));
    }

    private void registerListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ColouredSignListener(), this);
        manager.registerEvents(new DecreasedLagListener(this), this);
        manager.registerEvents(new JoinListener(this), this);
        manager.registerEvents(new KitListener(this), this);
        manager.registerEvents(new MobstackListener(this), this);
        manager.registerEvents(new NameVerifyListener(this), this);
        manager.registerEvents(new PingListener(), this);
        manager.registerEvents(this.playTimeManager = new PlayTimeManager(this), this);
        manager.registerEvents(new PlayerLimitListener(), this);
        manager.registerEvents(new VanishListener(this), this);
    }

    public void reloadSchedulers() {
        if (this.announcementTask != null) {
            this.announcementTask.cancel();
        }

        long announcementDelay = this.serverHandler.getAnnouncementDelay() * 20L;
        BukkitRunnable announcementRunnable = this.announcementTask = new AnnouncementHandler(this);
        announcementRunnable.runTaskTimer(this, announcementDelay, announcementDelay);
    }
}
