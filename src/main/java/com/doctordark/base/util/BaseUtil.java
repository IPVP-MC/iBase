package com.doctordark.base.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for the Base plugin.
 */
public class BaseUtil {

    public static String getDisplayName(CommandSender sender) {
        return (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
    }
}
