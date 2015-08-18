package com.doctordark.util.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by J on 7/16/2015.
 */
public class PlayerList implements Iterable<Player> {
    private List<String> players = new ArrayList<String>();
    private List<Player> playerList = new ArrayList<Player>();

    public PlayerList() {}
    public PlayerList(Collection<String> pls) {this.players = new ArrayList<String>(pls);}

    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                if(players.size() == 0)
                    return false;
                if(index >= players.size())
                    return false;
                return true;
            }
            @Override
            public Player next() {
                index++;
                return getPlayers().get(index-1);
            }

            @Override
            public void remove() {}
        };
    }

    public int size() {
        return players.size();
    }

    public List<Player> getPlayers() {
        playerList.clear();
        for(String name : players)
            playerList.add(Bukkit.getPlayerExact(name));
        return playerList;
    }

    public boolean contains(Player player) {
        if(player == null)
            return false;
        return players.contains(player.getName());
    }

    public void add(Player player) {
        if(!(contains(player)))
            players.add(player.getName());
    }

    public void remove(Player player) {
        if(contains(player)) {
            players.remove(player.getName());
        }
    }

    public void remove(String player) {
        players.remove(player);
    }

    public void clear() {
        players.clear();
    }
}