package com.doctordark.util.player;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerList implements Iterable<Player> {

    private final List<UUID> playerUniqueIds;
    private final List<Player> playerList = new ArrayList<>();

    public PlayerList() {
        this.playerUniqueIds = new ArrayList<>();
    }

    public PlayerList(Iterable<UUID> iterable) {
        this.playerUniqueIds = Lists.newArrayList(iterable);
    }

    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return !playerUniqueIds.isEmpty() && index < playerUniqueIds.size();
            }

            @Override
            public Player next() {
                index++;
                return getPlayers().get(index - 1);
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