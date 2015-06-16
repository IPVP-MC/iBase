package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import java.util.List;

public class RemoveEntityCommand extends BaseCommand {

    public RemoveEntityCommand() {
        super("removeentity", "Removes all of a specific entity.", "base.command.removeentity");
        setAliases(new String[0]);
        setUsage("/(command) <worldName> <entityType> [radius] [removeCustomNamed]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }

        World world = Bukkit.getServer().getWorld(args[0]);
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(args[1]);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Not an entity named '" + args[1] + "'.");
            return true;
        }

        if (entityType == EntityType.PLAYER) {
            sender.sendMessage(ChatColor.RED + "You cannot remove " + entityType.name() + " entities!");
            return true;
        }

        int radius = 0;
        if (args.length >= 3) {
            Integer parsed = Ints.tryParse(args[2]);
            if (parsed != null) {
                radius = parsed;
            }
        }

        boolean removeCustomNamed = false;
        if (args.length >= 4) {
            try {
                removeCustomNamed = Boolean.parseBoolean(args[3]);
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
                return true;
            }
        }

        Location location = (sender instanceof Player) ? ((Player) sender).getLocation() : null;

        int removed = 0;
        for (Chunk chunk : world.getLoadedChunks()) {
            for (Entity entity : chunk.getEntities()) {
                if (entity.getType() != entityType) continue;
                if ((radius <= 0) || (location != null && location.distanceSquared(entity.getLocation()) <= radius)) {
                    if (entity instanceof Tameable && ((Tameable) entity).isTamed()) continue;

                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        if (removeCustomNamed && livingEntity.getCustomName() != null) {
                            continue;
                        }
                    }

                    entity.remove();
                    removed++;
                }
            }
        }
        sender.sendMessage(ChatColor.YELLOW + "Removed " + removed + " of " + entityType.name() + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = Lists.newArrayList();
        if (args.length == 1) {
            for (World world : Bukkit.getServer().getWorlds()) {
                results.add(world.getName());
            }
        } else if (args.length == 2) {
            for (EntityType entityType : EntityType.values()) {
                results.add(entityType.name());
            }
        }

        return getCompletions(args, results);
    }
}
