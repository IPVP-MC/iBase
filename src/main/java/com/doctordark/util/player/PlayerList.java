package com.doctordark.util.player;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by J on 7/16/2015.
 */
public class PlayerList implements Iterable<Player> {
    private List<UUID> playerUniqueIds = Lists.newArrayList();
    private List<Player> playerList = Lists.newArrayList();

    public PlayerList() {}

    public PlayerList(Collection<UUID> col) {
        this.playerUniqueIds = Lists.newArrayList(col);
    }

    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                if(playerUniqueIds.size() == 0)
                    return false;
                if(index >= playerUniqueIds.size())
                    return false;
                return true;
            }

            @Override
            public Player next() {
                index++;
                return getPlayers().get(index-1);
            }

            @Override
            public void remove() {
            }
        };
    }

    public int size() {
        return playerUniqueIds.size();
    }

    public List<Player> getPlayers() {
        playerList.clear();
        for (UUID uuid : playerUniqueIds) {
            playerList.add(Bukkit.getPlayer(uuid));
        }

        return playerList;
    }

    public boolean contains(Player player) {
        return player != null && playerUniqueIds.contains(player.getUniqueId());
    }

    public boolean add(Player player) {
        return !playerUniqueIds.contains(player.getUniqueId()) && playerUniqueIds.add(player.getUniqueId());
    }

    public boolean remove(Player player) {
        return playerUniqueIds.remove(player.getUniqueId());
    }

    public void remove(UUID playerUUID) {
        playerUniqueIds.remove(playerUUID);
    }

    public void clear() {
        playerUniqueIds.clear();
    }
}