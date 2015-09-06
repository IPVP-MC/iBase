package com.doctordark.base.command.module.essential;

import com.doctordark.base.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EntitiesCommand extends BaseCommand {

    public EntitiesCommand() {
        super("entities", "Checks the entity count in environments.");
        setUsage("/(command) <playerName>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Collection<World> worlds = Bukkit.getWorlds();
        for (World world : worlds) {
            sender.sendMessage(ChatColor.GRAY + world.getEnvironment().name());

            EntityType[] types = EntityType.values();
            for (EntityType entityType : types) {
                if (entityType != EntityType.UNKNOWN) {
                    Class<? extends Entity> entityClass = entityType.getEntityClass();
                    if (entityClass != null) {
                        int amount = world.getEntitiesByClass(entityClass).size();
                        if (amount >= 20) {
                            sender.sendMessage(ChatColor.YELLOW + " " + entityType.getName() + " with " + amount);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
