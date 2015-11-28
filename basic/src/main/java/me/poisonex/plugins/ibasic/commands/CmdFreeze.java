package me.poisonex.plugins.ibasic.commands;

import me.poisonex.plugins.ibasic.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class CmdFreeze implements CommandExecutor, Listener {
    private Main plugin;

    private Set<UUID> frozenPlayers;
    private WeakHashMap<Player, UUID> haltedPlayers;
    private boolean serverFrozen;

    public CmdFreeze(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

        this.frozenPlayers = new HashSet<UUID>();
        this.haltedPlayers = new WeakHashMap<Player, UUID>();

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Player> players = haltedPlayers.keySet().iterator();

                while (players.hasNext()) {
                    Player p = players.next();

                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&m============================================="));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou have been frozen by " + haltedPlayers.get(p) + "."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lYou have 3 minutes to join the teamspeak server."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lTeamspeak IP: ts.ipvp.org"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lDo not log off or you will be banned."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&m============================================="));
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 10F, 10F);
                }
            }
        }.runTaskTimer(this.plugin, 8 * 20, 8 * 20);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("ibasic.freeze")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <player>");
                return true;
            }

            Player targetPlayer = this.plugin.getServer().getPlayer(args[0]);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            if (this.frozenPlayers.remove(targetPlayer.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is now unfrozen.");
                targetPlayer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You are un-frozen.");
            } else {
                this.frozenPlayers.add(targetPlayer.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " is now frozen.");
                targetPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are frozen.");
            }
        } else if (cmd.getName().equalsIgnoreCase("freezeall")) {
            this.serverFrozen = !this.serverFrozen;
            sender.sendMessage(ChatColor.GRAY + "Server Freeze Status - " + (this.serverFrozen ? ChatColor.GREEN + "Active" : ChatColor.RED + "Deactivated") + ChatColor.GRAY + ".");
        } else if (cmd.getName().equalsIgnoreCase("halt")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Correct Usage: /" + cmd.getName() + " <player>");
                return true;
            }

            Player targetPlayer = this.plugin.getServer().getPlayer(args[0]);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            UUID senderUUID = sender instanceof Player ? ((Player) sender).getUniqueId() : Main.CONSOLE_UUID;
            if (this.haltedPlayers.put(targetPlayer, senderUUID) == null) {
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
                sender.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " is now halted.");

                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&m============================================="));
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou have been frozen by " + this.haltedPlayers.get(targetPlayer) + "."));
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lYou have 3 minutes to join the teamspeak server."));
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lTeamspeak IP: ts.ipvp.org"));
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lDo not log off or you will be banned."));
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&l&m============================================="));
                targetPlayer.playSound(targetPlayer.getLocation(), Sound.NOTE_PLING, 10F, 10F);
            } else {
                this.haltedPlayers.remove(targetPlayer);

                if (targetPlayer.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    targetPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
                }

                sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " is now un-halted.");
                if (this.isFrozen(targetPlayer)) {
                    sender.sendMessage(ChatColor.RED + "Note that the player you un-halted is still frozen.");
                }
            }
        }

        return true;
    }

    private boolean isFrozen(Player p) {
        if (p.hasPermission("freeze.bypass")) {
            return false;
        }

        return this.serverFrozen || this.frozenPlayers.contains(p.getUniqueId()) || this.haltedPlayers.get(p) != null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
            e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.frozenPlayers.remove(e.getPlayer().getUniqueId());

        if (this.haltedPlayers.remove(e.getPlayer()) != null) {
            if (e.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
                e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }

            for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                if (p.hasPermission("ibasic.freeze")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + e.getPlayer().getName() + " has logged off."));
                }
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (StringUtils.containsIgnoreCase("Kicked for flying or related", e.getReason())) {
            if (this.isFrozen(e.getPlayer()) || this.haltedPlayers.get(e.getPlayer()) != null) {
                e.setCancelled(true);
//				this.frozenPlayers.remove(e.getPlayer().getName());
            }
        } else if (this.haltedPlayers.remove(e.getPlayer()) != null) {
            if (e.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
                e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }

            for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                if (p.hasPermission("ibasic.freeze")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + e.getPlayer().getName() + " has logged off."));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
            if (this.isFrozen(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (this.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if (this.isFrozen(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamager(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (this.isFrozen(p)) {
                e.setCancelled(true);

                if (e instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) e).getDamager();

                    if (damager instanceof Player) {
                        Player d = (Player) damager;

                        if (this.haltedPlayers.get(p) != null) {
                            d.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player has been frozen by a staff member and is being checked for possible hacks."));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player d = (Player) e.getDamager();

            if (this.isFrozen(d)) {
                e.setCancelled(true);
            }
        }
    }
}
