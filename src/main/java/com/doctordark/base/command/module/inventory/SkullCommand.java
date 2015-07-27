package com.doctordark.base.command.module.inventory;

import com.doctordark.base.command.BaseCommand;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

/**
 * Command used to spawn skulls of players.
 */
public class SkullCommand extends BaseCommand {

    private static final ImmutableList<String> SKULL_NAMES;

    static {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
        for (SkullType skullType : SkullType.values()) {
            builder.add(skullType.name());
        }

        SKULL_NAMES = builder.build();
    }

    public SkullCommand() {
        super("skull", "Spawns a player head skull item.", "base.command.skull");
        this.setAliases(new String[]{"head", "playerhead"});
        this.setUsage("/(command) <playerName>");
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }

        Optional<SkullType> skullType = args.length > 0 ? Enums.getIfPresent(SkullType.class, args[0]) : Optional.absent();
        ItemStack stack;
        if (skullType.isPresent()) {
            stack = new ItemStack(Material.SKULL_ITEM, 1, skullType.get().getData());
        } else {
            stack = new ItemStack(Material.SKULL_ITEM, 1, SkullType.PLAYER.getData());
            String ownerName = (args.length > 0) ? args[0] : sender.getName();
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwner(ownerName);
            stack.setItemMeta(meta);
        }

        ((Player) sender).getInventory().addItem(stack);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        List<String> completions = Lists.newArrayList(SKULL_NAMES);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player && !((Player) sender).canSee(player)) {
                continue;
            }

            completions.add(player.getName());
        }

        return getCompletions(args, completions);
    }
}
