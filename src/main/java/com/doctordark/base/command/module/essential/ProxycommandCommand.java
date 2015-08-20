package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import java.util.Collections;
import java.util.List;

/**
 * Command used to execute a command from the proxy.
 */
public class ProxycommandCommand extends BaseCommand {

    private static final String PROXY_CHANNEL = "BungeeCord";

    private final BasePlugin plugin;

    public ProxycommandCommand(BasePlugin plugin) {
        super("proxycommand", "Used to execute a command from the proxy.", "base.command.proxycommand");
        setUsage("/(command) <command args..>");
        this.plugin = plugin;

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, PROXY_CHANNEL);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        String fullCommand;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("DispatchCommand");
        out.writeUTF(sender.getName());
        out.writeUTF(fullCommand = StringUtils.join(args, ' ', 0, args.length));

        PluginMessageRecipient pluginMessageRecipient;
        if (sender instanceof PluginMessageRecipient) {
            pluginMessageRecipient = ((PluginMessageRecipient) sender);
        } else {
            pluginMessageRecipient = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

            if (pluginMessageRecipient == null) {
                sender.sendMessage(ChatColor.RED + "Unable to send plugin message, no players are online.");
                return true;
            }
        }

        pluginMessageRecipient.sendPluginMessage(plugin, PROXY_CHANNEL, out.toByteArray());
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Executed proxy command " + fullCommand + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
