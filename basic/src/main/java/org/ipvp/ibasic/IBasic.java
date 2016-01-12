package org.ipvp.ibasic;

import com.sk89q.squirrelid.cache.HashMapCache;
import com.sk89q.squirrelid.cache.ProfileCache;
import com.sk89q.squirrelid.resolver.CacheForwardingService;
import com.sk89q.squirrelid.resolver.HttpRepositoryService;
import com.sk89q.squirrelid.resolver.ProfileService;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.ibasic.commands.CommandDonorJoin;
import org.ipvp.ibasic.commands.CommandFilter;
import org.ipvp.ibasic.commands.CommandSlowchat;
import org.ipvp.ibasic.freeze.CommandFreeze;
import org.ipvp.ibasic.freeze.CommandFreezeAll;
import org.ipvp.ibasic.freeze.CommandHalt;
import org.ipvp.ibasic.freeze.FreezeListener;

import java.io.File;

public class IBasic extends JavaPlugin {

    @Getter
    private CommandDonorJoin commandDonorJoin;

    @Getter
    private FreezeListener freezeListener;

    @Getter
    private ProfileCache cache;

    @Getter
    private ProfileService resolver;

    @Getter
    private boolean filterOnStartup;

    @Override
    public void onEnable() {
        File folder = this.getDataFolder();
        if (!folder.exists() && folder.mkdir()) {
            getLogger().info("Created configuration folder.");
        }

        getConfig().addDefault("filter.enable-on-startup", false);
        getConfig().options().copyDefaults(true);
        this.saveConfig();

        filterOnStartup = getConfig().getBoolean("filter.enable-on-startup", false);

        this.cache = new HashMapCache(); // Memory cache
        this.resolver = new CacheForwardingService(HttpRepositoryService.forMinecraft(), this.cache);

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(this.freezeListener = new FreezeListener(this), this);

        PluginCommand command = getCommand("donorjoin");
        command.setExecutor(this.commandDonorJoin = new CommandDonorJoin(this));
        command.setPermission("donorjoin.true");
        command.setPermissionMessage(ChatColor.RED + "You do not have permission.");

        command = getCommand("freeze");
        command.setExecutor(new CommandFreeze(this));
        command.setPermission("ibasic.freeze");
        command.setPermissionMessage(ChatColor.RED + "You do not have permission.");

        command = getCommand("freezeall");
        command.setExecutor(new CommandFreezeAll(this));
        command.setPermission("ibasic.freeze");
        command.setPermissionMessage(ChatColor.RED + "You do not have permission.");

        command = getCommand("halt");
        command.setExecutor(new CommandHalt(this));
        command.setPermission("ibasic.freeze");
        command.setPermissionMessage(ChatColor.RED + "You do not have permission.");

        command = getCommand("slowchat");
        command.setExecutor(new CommandSlowchat(this));
        command.setPermission("slowchat.true");
        command.setPermissionMessage(ChatColor.RED + "You do not have permission.");

        command = getCommand("delay");
        command.setExecutor(new CommandFilter(this));
        command.setPermission("filter.true");
        command.setPermissionMessage(ChatColor.RED + "You do not have permission.");
    }
}
