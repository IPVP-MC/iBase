package me.poisonex.plugins.ibasic.listeners;

import com.google.common.collect.Iterables;
import lombok.Getter;
import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SpecialListener {

    public SpecialListener(IBasic plugin) {
        List<String> textCommands = Arrays.asList("help", "tutorial");
        for (String textCommand : textCommands) {
            plugin.getCommand(textCommand).setExecutor(new TextBasedCommand(plugin, textCommand));
        }
    }

    public static class TextBasedCommand implements CommandExecutor {

        @Getter
        private String name;

        private File file;

        @Getter
        private final String[] lines;

        public TextBasedCommand(IBasic plugin, String name) {
            this.name = name;
            this.file = new File(plugin.getDataFolder(), this.name + ".txt");
            this.lines = Iterables.toArray(IBasic.readLines(plugin, file), String.class);
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            sender.sendMessage(this.lines);
            return true;
        }
    }
}
