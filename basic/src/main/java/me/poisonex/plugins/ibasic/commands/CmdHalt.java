package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CmdHalt implements CommandExecutor {

    private final IBasic plugin;

    public CmdHalt(IBasic plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <player>");
            return true;
        }

        Player targetPlayer = plugin.getServer().getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }

        UUID senderUUID = sender instanceof Player ? ((Player) sender).getUniqueId() : IBasic.CONSOLE_UUID;

        if (plugin.getFreezeManager().getHaltedPlayers().put(targetPlayer.getUniqueId(), senderUUID) == null) {
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
            sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " is now halted.");

            String bannerMessage = ChatColor.GOLD.toString() + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "&m=============================================";
            targetPlayer.sendMessage(bannerMessage);
            targetPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have been frozen by " + sender.getName() + ".");
            targetPlayer.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "You have 3 minutes to join the teamspeak server.");
            targetPlayer.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Teamspeak IP: ts.ipvp.org");
            targetPlayer.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Do not log off or you will be banned.");
            targetPlayer.sendMessage(bannerMessage);
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.NOTE_PLING, 10F, 10F);
        } else {
            plugin.getFreezeManager().getHaltedPlayers().remove(targetPlayer.getUniqueId());

            if (targetPlayer.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                targetPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
            }

            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is now un-halted.");
            if (plugin.getFreezeManager().isFrozen(targetPlayer)) {
                sender.sendMessage(ChatColor.RED + "Note that the player you un-halted is still frozen.");
            }
        }

        return true;
    }
}
