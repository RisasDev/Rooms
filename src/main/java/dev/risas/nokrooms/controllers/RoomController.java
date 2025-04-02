package dev.risas.nokrooms.controllers;

import dev.risas.nokrooms.events.RoomEnteredEvent;
import dev.risas.nokrooms.events.RoomLeftEvent;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
@Getter
public class RoomController {

    private final Map<String, Room> rooms;

    public RoomController() {
        this.rooms = new HashMap<>();
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public Room getRoomByLocation(Location location) {
        for (Room room : rooms.values()) {
            if (room.getCuboid().contains(location)) return room;
        }
        return null;
    }

    public Room getRoomByPlayer(Player player) {
        for (Room room : rooms.values()) {
            if (room.isRoomPlayer(player)) return room;
        }
        return null;
    }

    public void createRoom(String name, Cuboid cuboid) {
        Room room = new Room(name, cuboid);
        rooms.put(name, room);
    }

    public void deleteRoom(String name) {
        rooms.remove(name);
    }

    public boolean isRoom(String name) {
        return rooms.containsKey(name);
    }

    public void checkEnterOrLeave(Player player, Room room, Location from, Location to) {
        if (!room.getCuboid().contains(from) && room.getCuboid().contains(to)) {
            Bukkit.getServer().getPluginManager().callEvent(new RoomEnteredEvent(player, room));
        }
        else if (room.getCuboid().contains(from) && !room.getCuboid().contains(to)) {
            Bukkit.getServer().getPluginManager().callEvent(new RoomLeftEvent(player, room));
        }
    }
}
