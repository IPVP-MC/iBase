package com.doctordark.base.command.module.essential;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.command.BaseCommand;
import com.doctordark.base.user.BaseUser;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class GlintCommand extends BaseCommand {

    private static final long GLINT_COOLDOWN = TimeUnit.SECONDS.toMillis(15L);

    private final BasePlugin plugin;

    public GlintCommand(BasePlugin plugin) {
        super("glint", "Toggles enchantment glints.", "base.command.glint");
        setUsage("/(command)");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }

        Player player = (Player) sender;
        BaseUser baseUser = plugin.getUserManager().getUser(player.getUniqueId());

        long millis = System.currentTimeMillis();
        long difference = baseUser.getLastGlintUse() - millis;

        if (difference > 0L) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command for another " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            return true;
        }

        boolean newEnabled = !baseUser.isGlintEnabled();
        baseUser.setGlintEnabled(newEnabled);
        baseUser.setLastGlintUse(millis + GLINT_COOLDOWN);

        sender.sendMessage(ChatColor.AQUA + "Enchantment glint " + (newEnabled ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.AQUA + " enabled.");
        return true;
    }
}
