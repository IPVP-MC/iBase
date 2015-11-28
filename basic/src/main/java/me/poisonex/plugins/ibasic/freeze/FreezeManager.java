package me.poisonex.plugins.ibasic.freeze;

import lombok.Getter;
import lombok.Setter;
import me.poisonex.plugins.ibasic.IBasic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {

    /**
     * Halted -> Halter
     */
    @Getter
    private final Map<UUID, UUID> haltedPlayers = new HashMap<>();

    @Getter
    private final Set<UUID> frozenPlayers = new HashSet<>();

    @Getter
    @Setter
    private boolean serverFrozen;

    public FreezeManager(IBasic plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String header = ChatColor.GOLD.toString() + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "=============================================";
                for (UUID uuid : haltedPlayers.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    player.sendMessage(header);
                    player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have been frozen by " + haltedPlayers.get(player.getUniqueId()) + ".");
                    player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "You have 3 minutes to join the teamspeak server.");
                    player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Teamspeak IP: ts.ipvp.org");
                    player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Do not log off or you will be banned.");
                    player.sendMessage(header);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10F, 10F);
                }
            }
        }.runTaskTimer(plugin, 160L, 160L);
    }

    public boolean isFrozen(Player player) {
        return !player.hasPermission("freeze.bypass") && (this.serverFrozen || this.frozenPlayers.contains(player.getUniqueId()) || !this.haltedPlayers.containsKey(player.getUniqueId()));
    }
}
