package com.doctordark.base.command.module;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommandModule;
import com.doctordark.base.command.module.chat.SayCommand;
import com.doctordark.base.command.module.chat.SoundsCommand;
import com.doctordark.base.command.module.essential.*;
import com.doctordark.base.command.module.teleport.warp.WarpExecutor;

public class EssentialModule extends BaseCommandModule {

    public EssentialModule(BasePlugin plugin) {
        commands.add(new AmivisCommand(plugin));
        commands.add(new AutoRestartCommand(plugin));
        commands.add(new BaseReloadCommand(plugin));
        commands.add(new BiomeCommand());
        commands.add(new CraftCommand());
        commands.add(new EnchantCommand());
        commands.add(new EntitiesCommand());
        commands.add(new FeedCommand());
        commands.add(new FlyCommand());
        commands.add(new GamemodeCommand());
        commands.add(new HatCommand());
        commands.add(new HealCommand());
        commands.add(new IpHistoryCommand(plugin));
        commands.add(new KillCommand());
        commands.add(new LagCommand());
        commands.add(new NameHistoryCommand(plugin));
        commands.add(new NearCommand());
        commands.add(new PingCommand());
        commands.add(new PlayerTimeCommand());
        commands.add(new PositionCommand());
        commands.add(new RemoveEntityCommand());
        commands.add(new RenameCommand());
        commands.add(new RepairCommand());
        commands.add(new RulesCommand(plugin));
        commands.add(new SayCommand());
        commands.add(new SetMaxPlayersCommand(plugin));
        commands.add(new SoundsCommand(plugin));
        commands.add(new SpeedCommand());
        commands.add(new SudoCommand());
        commands.add(new UptimeCommand());
        commands.add(new VanishCommand(plugin));
        commands.add(new WarpExecutor(plugin));
        commands.add(new WhoisCommand(plugin));
    }
}
