package com.doctordark.base.command.module;

import com.doctordark.base.command.BaseCommandModule;
import com.doctordark.base.command.module.inventory.*;

public class InventoryModule extends BaseCommandModule {

    public InventoryModule() {
        commands.add(new ClearInvCommand());
        commands.add(new GiveCommand());
        commands.add(new IdCommand());
        commands.add(new InvSeeCommand());
        commands.add(new ItemCommand());
        commands.add(new MoreCommand());
        commands.add(new SkullCommand());
    }
}
