package com.doctordark.util.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

/**
 * Created by J on 7/16/2015.
 */
public class PlayerCache {
    public String player;
    public Location loc;
    public GameMode gameMode;
    public boolean allowFlight;
    public boolean flying;
    public ItemStack[] inv;
    public ItemStack[] armor;
    public double health;
    public int food;
    public int level;
    public float xp;
    public int fireTicks;
    public Collection<PotionEffect> potions;

    public PlayerCache(Player player){
        this.player = player.getName();
        this.loc = player.getLocation();
        this.gameMode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.flying = player.isFlying();
        this.inv = player.getInventory().getContents();
        this.armor = player.getInventory().getArmorContents();
        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.level = player.getLevel();
        this.xp = player.getExp();
        this.fireTicks = player.getFireTicks();
        this.potions = player.getActivePotionEffects();
    }

    public PlayerCache(Location loc, GameMode gameMode, boolean allowFlight, boolean flying, ItemStack[] inv, ItemStack[] armor,
                       double health, int food, int level, float xp, int fireTicks, Collection<PotionEffect> potions) {
        this.loc = loc;
        this.gameMode = gameMode;
        this.allowFlight = allowFlight;
        this.flying = flying;
        this.inv = inv;
        this.armor = armor;
        this.health = health;
        this.food = food;
        this.level = level;
        this.xp = xp;
        this.fireTicks = fireTicks;
        this.potions = potions;
    }

    public void apply(Player player) {
        PlayerUtil.wipe(player);
        if(loc!=null) {
            player.teleport(loc);
        }
        player.setGameMode(gameMode);
        player.setAllowFlight(allowFlight);
        player.setFlying(flying);
        player.getInventory().setContents(inv);
        player.getInventory().setArmorContents(armor);
        player.setHealth(health);
        player.setFoodLevel(food);
        player.setLevel(level);
        player.setExp(xp);
        player.setFireTicks(fireTicks);
        player.getActivePotionEffects().clear();
        for(PotionEffect effect : potions){
            player.addPotionEffect(effect);
        }
    }
}