package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.cmd.BaseCommand;

public class IgnoreCommand extends BaseCommand {

    public IgnoreCommand() {
        super("ignore", "Ignores a player from messages.", "base.command.ignore");
        this.setAliases(new String[]{});
        this.setUsage("/(command) <playerName>");
    }
}
