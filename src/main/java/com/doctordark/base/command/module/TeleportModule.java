package com.doctordark.base.command.module;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommandModule;
import com.doctordark.base.command.module.teleport.*;
import com.doctordark.base.command.module.teleport.warp.WarpExecutor;

public class TeleportModule extends BaseCommandModule {

    public TeleportModule(BasePlugin plugin) {
        commands.add(new BackCommand(plugin));
        commands.add(new TeleportCommand());
        commands.add(new TeleportHereCommand());
        commands.add(new TopCommand());
        commands.add(new WorldCommand());
        commands.add(new WarpExecutor(plugin));
    }
}
