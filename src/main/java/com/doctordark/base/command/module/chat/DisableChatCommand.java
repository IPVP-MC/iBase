package com.doctordark.base.command.module.chat;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.util.BaseUtil;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class DisableChatCommand extends BaseCommand {

    private static final long DEFAULT_DELAY = TimeUnit.MINUTES.toMillis(5L);
    private final BasePlugin plugin;

    public DisableChatCommand(BasePlugin plugin) {
        super("disablechat", "Disables the chat for non-staff.", "base.command.disablechat");
        setAliases(new String[]{"mutechat", "restrictchat", "mc", "rc"});
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long oldTicks = this.plugin.getServerHandler().getRemainingChatDisabledMillis();
        long newTicks;
        if (args.length < 1) {
            newTicks = oldTicks > 0L ? 0L : DEFAULT_DELAY;
        } else {
            StringBuilder builder = new StringBuilder();
            for (String argument : args) {
                builder.append(argument).append(" ");
            }

            newTicks = BaseUtil.parse(builder.toString());
        }

        if ((newTicks <= 0L) && (oldTicks <= 0L)) {
            sender.sendMessage(ChatColor.RED + "Global chat will continue to be unrestricted.");
            return true;
        }

        plugin.getServerHandler().setChatDisabledMillis(newTicks);
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Global chat is " + (newTicks > 0L ? ChatColor.GOLD + "now disabled for " +
                DurationFormatUtils.formatDurationWords(newTicks, true, true) : String.valueOf(ChatColor.RED) + "no longer disabled") + ChatColor.YELLOW + ".");
        return true;
    }
}
