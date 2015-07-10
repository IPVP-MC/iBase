package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.command.module.chat.event.PlayerMessageEvent;
import com.doctordark.base.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ToggleSoundsCommand extends BaseCommand implements Listener {

    private final BasePlugin plugin;

    public ToggleSoundsCommand(BasePlugin plugin) {
        super("sounds", "Toggles messaging sounds.", "base.command.sounds");
        setAliases(new String[]{"pmsounds", "togglepmsounds", "messagingsounds"});
        setUsage("/(command) [playerName]");
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());

        boolean newMessagingSounds = !baseUser.isMessagingSounds() || (args.length >= 2 && Boolean.parseBoolean(args[1]));
        baseUser.setMessagingSounds(newMessagingSounds);

        sender.sendMessage(ChatColor.YELLOW + "Messaging sounds are now " + (newMessagingSounds ? ChatColor.GREEN + "on" : ChatColor.RED + "off") + ChatColor.YELLOW + '.');
        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player recipient = event.getRecipient();
        BaseUser recipientUser = plugin.getUserManager().getUser(recipient.getUniqueId());
        if (recipientUser.isMessagingSounds()) {
            recipient.playSound(recipient.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
        }
    }
}
