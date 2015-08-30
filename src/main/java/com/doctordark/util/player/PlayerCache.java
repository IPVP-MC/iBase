package com.doctordark.util.player;

import com.doctordark.util.InventoryUtils;
import com.google.common.collect.Sets;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.UUID;

public class PlayerCache {

    public UUID playerUUID;
    public Location location;
    public GameMode gameMode;
    public boolean allowFlight;
    public boolean flying;
    public ItemStack[] inventory;
    public ItemStack[] armor;
    public double health;
    public int food;
    public int level;
    public float xp;
    public int fireTicks;
    public Collection<PotionEffect> potions;

    public PlayerCache(Player player) {
        this.playerUUID = player.getUniqueId();
        this.location = player.getLocation();
        this.gameMode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.flying = player.isFlying();
        this.inventory = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.level = player.getLevel();
        this.xp = player.getExp();
        this.fireTicks = player.getFireTicks();
        this.potions = Sets.newHashSet(player.getActivePotionEffects());
    }

    public PlayerCache(Location location, GameMode gamemode, boolean allowFlight, boolean flying, ItemStack[] inventory, ItemStack[] armor,
                       double health, int food, int level, float xp, int fireTicks, Collection<PotionEffect> potions) {
        this.location = location;
        this.gameMode = gamemode;
        this.allowFlight = allowFlight;
        this.flying = flying;
        this.inventory = InventoryUtils.deepClone(inventory);
        this.armor = InventoryUtils.deepClone(armor);
        this.health = health;
        this.food = food;
        this.level = level;
        this.xp = xp;
        this.fireTicks = fireTicks;
        this.potions = potions;
    }

    public void apply(Player player) {
        PlayerUtil.wipe(player);
        if (location != null) {
            player.teleport(location);
        }

        player.setGameMode(gameMode);
        player.setAllowFlight(allowFlight);
        player.setFlying(flying);
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setLevel(level);
        player.setExp(xp);
        player.setFireTicks(fireTicks);
        player.getActivePotionEffects().clear();
        for (PotionEffect effect : potions) {
            player.addPotionEffect(effect);
        }
    }
}