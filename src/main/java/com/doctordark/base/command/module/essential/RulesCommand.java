package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RulesCommand extends BaseCommand {

    private final BasePlugin plugin;

    public RulesCommand(BasePlugin plugin) {
        super("rules", "Shows the server rules.", "base.command.rules");
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> rules = plugin.getServerHandler().getServerRules();
        sender.sendMessage(rules.toArray(new String[rules.size()]));
        return true;
    }
}
