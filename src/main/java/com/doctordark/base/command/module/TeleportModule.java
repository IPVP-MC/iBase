package com.doctordark.base.command.module;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommandModule;
import com.doctordark.base.command.module.teleport.BackCommand;
import com.doctordark.base.command.module.teleport.TeleportAllCommand;
import com.doctordark.base.command.module.teleport.TeleportCommand;
import com.doctordark.base.command.module.teleport.TeleportHereCommand;
import com.doctordark.base.command.module.teleport.TopCommand;
import com.doctordark.base.command.module.teleport.WorldCommand;
import com.doctordark.base.command.module.teleport.warp.WarpExecutor;

public class TeleportModule extends BaseCommandModule {

    public TeleportModule(BasePlugin plugin) {
        commands.add(new BackCommand(plugin));
        commands.add(new TeleportCommand());
        commands.add(new TeleportAllCommand());
        commands.add(new TeleportHereCommand());
        commands.add(new TopCommand());
        commands.add(new WorldCommand());
        commands.add(new WarpExecutor(plugin));
    }
}
