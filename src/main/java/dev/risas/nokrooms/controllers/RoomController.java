package dev.risas.nokrooms.controllers;

import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.utilities.cuboid.Cuboid;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
public class RoomController {

    private final Map<String, Room> rooms;

    public RoomController() {
        this.rooms = new HashMap<>();
    }

    public Room getRoom(String name) {
        return rooms.get(name);
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
}
