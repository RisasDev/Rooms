package dev.risas.nokrooms.models;

import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
public class Room {

    private final String name;
    private final Cuboid cuboid;
    private List<Player> peopleInRoom;
    private boolean busy;

    public Room(String name, Cuboid cuboid) {
        this.name = name;
        this.cuboid = cuboid;
    }

    public void addPlayer(Player player) {
        peopleInRoom.add(player);
    }

    public void removePlayer(Player player) {
        peopleInRoom.remove(player);
    }
}
