package me.poisonex.plugins.ibasic;

import me.poisonex.plugins.ibasic.commands.CmdDonorJoin;
import me.poisonex.plugins.ibasic.commands.CmdFilter;
import me.poisonex.plugins.ibasic.commands.CmdFreeze;
import me.poisonex.plugins.ibasic.commands.CmdSlowChat;
import me.poisonex.plugins.ibasic.listeners.ServerKickListener;
import me.poisonex.plugins.ibasic.listeners.SpecialListener;
import me.poisonex.plugins.ibasic.utils.UUIDManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin {
    public static final UUID CONSOLE_UUID = UUID.fromString("4490945f-2fe4-43bd-985d-7861c729e9ee");

    public CmdDonorJoin cmdDonorJoin;
    public CmdFreeze cmdFreeze;

    public UUIDManager uuidManager;

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        this.cmdDonorJoin = new CmdDonorJoin(this);
        this.cmdFreeze = new CmdFreeze(this);

        this.uuidManager = new UUIDManager(this);

        getServer().getPluginManager().registerEvents(new ServerKickListener(this), this);
        getServer().getPluginManager().registerEvents(new SpecialListener(this), this);
//		getServer().getPluginManager().registerEvents(new AntiBotListener(), this); // Not being used ig
//		getServer().getPluginManager().registerEvents(new ChatListener(), this);

        getCommand("donorjoin").setExecutor(this.cmdDonorJoin);
        getCommand("slowchat").setExecutor(new CmdSlowChat(this));
        getCommand("freeze").setExecutor(this.cmdFreeze);
        getCommand("freezeall").setExecutor(this.cmdFreeze);
        getCommand("halt").setExecutor(this.cmdFreeze);
//		getCommand("help").setExecutor(new CmdText(this, "help"));
//		getCommand("tutorial").setExecutor(new CmdText(this, "tutorial"));
        getCommand("filter").setExecutor(new CmdFilter(this));

        getLogger().info("iBasic -> Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("iBasic -> Disabled");
    }
}
