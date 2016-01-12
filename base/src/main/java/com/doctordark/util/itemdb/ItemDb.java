package com.doctordark.util.itemdb;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ItemDb {

    /**
     * Reloads the item database from storage.
     */
    void reloadItemDatabase();

    /**
     * Gets a potion with a given id.
     *
     * @param id the id to search for
     * @return potion with given id
     */
    ItemStack getPotion(String id);

    /**
     * Gets a potion with a given id and quantity.
     *
     * @param id       the id to search for
     * @param quantity the quantity to give
     * @return potion with given id and quantity
     */
    ItemStack getPotion(String id, int quantity);

    /**
     * Gets an item with a given id.
     *
     * @param id the id to search
     * @return item with given id
     */
    ItemStack getItem(String id);

    /**
     * Gets an item with a given id and quantity.
     *
     * @param id       the id to search
     * @param quantity the quantity to get with
     * @return item with given id and quantity
     */
    ItemStack getItem(String id, int quantity);

    /**
     * Gets the name of an item.
     *
     * @param item the item to lookup
     * @return the name of the item
     */
    String getName(ItemStack item);

    /**
     * Gets the primary name of an item.
     *
     * @param item the item to lookup
     * @return the primary name of item
     */
    @Deprecated
    String getPrimaryName(ItemStack item);

    /**
     * Gets a conjoined string with alternate
     * name aliases for an item.
     *
     * @param item the item to lookup
     * @return the conjoined alias string
     */
    String getNames(ItemStack item);

    /**
     * Gets a list of matching items from given arguments for a player.
     *
     * @param player the player to match for
     * @param args   the arguments to match on
     * @return list of items that match given arguments
     */
    List<ItemStack> getMatching(Player player, String[] args);
}
