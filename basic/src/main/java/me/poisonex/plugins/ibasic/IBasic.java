package me.poisonex.plugins.ibasic;

import lombok.Getter;
import me.poisonex.plugins.ibasic.commands.CmdDonorJoin;
import me.poisonex.plugins.ibasic.commands.CmdFilter;
import me.poisonex.plugins.ibasic.freeze.CmdFreeze;
import me.poisonex.plugins.ibasic.freeze.CmdFreezeall;
import me.poisonex.plugins.ibasic.commands.CmdHalt;
import me.poisonex.plugins.ibasic.commands.CmdSlowChat;
import me.poisonex.plugins.ibasic.freeze.FreezeListener;
import me.poisonex.plugins.ibasic.freeze.FreezeManager;
import me.poisonex.plugins.ibasic.listeners.ServerKickListener;
import me.poisonex.plugins.ibasic.listeners.SpecialListener;
import me.poisonex.plugins.ibasic.utils.UUIDManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IBasic extends JavaPlugin {

    public static final UUID CONSOLE_UUID = UUID.fromString("4490945f-2fe4-43bd-985d-7861c729e9ee");

    @Getter
    public final List<String> donatorList = new ArrayList<>();

    @Getter
    private FreezeManager freezeManager;

    @Getter
    private UUIDManager uuidManager;

    @Override
    public void onEnable() {
        File folder = this.getDataFolder();
        if (!folder.exists() && folder.mkdir()) {
            getLogger().info("Created plugin folder");
        }

        this.reloadConfig();

        this.donatorList.clear();
        this.donatorList.addAll(this.getConfig().getStringList("donorList"));

        this.freezeManager = new FreezeManager(this);
        this.uuidManager = new UUIDManager(this);

        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new FreezeListener(this), this);
        manager.registerEvents(new ServerKickListener(this), this);
        manager.registerEvents(new SpecialListener(this), this);

        PluginCommand donorJoinCommand = this.getCommand("donorjoin");
        donorJoinCommand.setPermission("donorjoin.true");
        donorJoinCommand.setExecutor(new CmdDonorJoin(this));

        PluginCommand filterCommand = this.getCommand("filter");
        filterCommand.setExecutor(new CmdFilter(this));
        filterCommand.setPermission("filter.true");

        PluginCommand freezeCommand = this.getCommand("freeze");
        freezeCommand.setExecutor(new CmdFreeze(this));
        freezeCommand.setPermission("ibasic.freeze");

        PluginCommand freezeAllCommand = this.getCommand("freezeall");
        freezeAllCommand.setExecutor(new CmdFreezeall(this));
        freezeAllCommand.setPermission("ibasic.freeze");

        PluginCommand haltCommand = this.getCommand("halt");
        haltCommand.setExecutor(new CmdHalt(this));
        haltCommand.setPermission("ibasic.freeze");

        PluginCommand slowChatCommand = this.getCommand("slowchat");
        slowChatCommand.setExecutor(new CmdSlowChat(this));
        slowChatCommand.setPermission("slowchat.true");

        // getServer().getPluginManager().registerEvents(new AntiBotListener(), this); // Not being used rn
        // getServer().getPluginManager().registerEvents(new ChatListener(), this);

        getLogger().info("iBasic -> Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("iBasic -> Disabled");
    }
}
