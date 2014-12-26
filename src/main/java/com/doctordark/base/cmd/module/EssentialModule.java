package com.doctordark.base.cmd.module;

import com.doctordark.base.cmd.BaseCommand;
import com.doctordark.base.cmd.BaseCommandModule;
import com.doctordark.base.cmd.module.essential.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Module used for essential based commands.
 */
public class EssentialModule implements BaseCommandModule {

    private Set<BaseCommand> commands;

    public EssentialModule(JavaPlugin plugin) {
        commands = new HashSet<BaseCommand>();
        commands.add(new BiomeCommand());
        commands.add(new BroadcastCommand(plugin));
        commands.add(new FeedCommand());
        commands.add(new FlyCommand());
        commands.add(new GamemodeCommand());
        commands.add(new HealCommand());
        commands.add(new KillCommand());
        commands.add(new LagCommand());
        commands.add(new PositionCommand());
        commands.add(new RepairCommand());
        commands.add(new SayCommand());
        commands.add(new SpeedCommand());
        commands.add(new UptimeCommand());
        commands.add(new VanishCommand());
    }

    @Override
    public Set<BaseCommand> getCommands() {
        return commands;
    }

    @Override
    public void unregisterCommand(BaseCommand command) {
        commands.remove(command);
    }

    @Override
    public void unregisterCommands() {
        commands.clear();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
