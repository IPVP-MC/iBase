package com.doctordark.base.kit.argument;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.kit.Kit;
import com.doctordark.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An {@link CommandArgument} used to open an {@link Kit} GUI.
 */
public class KitGuiArgument extends CommandArgument {

    private final BasePlugin plugin;

    public KitGuiArgument(BasePlugin plugin) {
        super("gui", "Opens the kit gui");
        this.plugin = plugin;
        this.aliases = new String[]{"menu"};
        this.permission = "hcf.command.kit.argument." + getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may open kit GUI's.");
            return true;
        }

        Collection<Kit> kits = plugin.getKitManager().getKits();

        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }

        Player player = (Player) sender;
        player.openInventory(plugin.getKitManager().getGui(player));

        sender.sendMessage(ChatColor.YELLOW + "You have opened the kit GUI. Middle click to preview, or click to acquire a kit.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
