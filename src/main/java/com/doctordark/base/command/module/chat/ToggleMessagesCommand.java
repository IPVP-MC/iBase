package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.doctordark.base.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ToggleMessagesCommand extends BaseCommand {

    private final BasePlugin plugin;

    public ToggleMessagesCommand(BasePlugin plugin) {
        super("togglemessages", "Toggles private messages.", "base.command.togglemessages");
        setAliases(new String[]{"togglepm", "toggleprivatemessages"});
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player recipient = event.getRecipient();
        BaseUser recipientUser = plugin.getUserManager().getUser(recipient.getUniqueId());
        if (recipientUser.isToggledMessages()) {
            CommandSender sender = event.getSender();
            event.setCancelled(true);
            sender.sendMessage(ChatColor.RED + "That player has private messages toggled.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        boolean newToggled = !baseUser.isToggledMessages();
        baseUser.setToggledMessages(newToggled);

        sender.sendMessage(ChatColor.GREEN + "You have toggled private messages on.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
