package dev.risas.nokrooms.controllers;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.events.RoomEnteredEvent;
import dev.risas.nokrooms.events.RoomLeftEvent;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.utilities.ChatUtil;
import dev.risas.nokrooms.utilities.FileConfig;
import dev.risas.nokrooms.utilities.SerializeUtil;
import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
@Getter
public class RoomController {

    private final FileConfig configFile, languageFile;
    private final Map<String, Room> rooms;

    public RoomController(NokRooms plugin) {
        this.configFile = plugin.getConfigFile();
        this.languageFile = plugin.getLanguageFile();
        this.rooms = new HashMap<>();
        this.onLoad();
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

    public Room getRoomByLocation(Location location) {
        return rooms.values().stream()
                .filter(room -> room.getCuboid().contains(location))
                .findFirst()
                .orElse(null);
    }

    public Room getRoomByPlayer(Player player) {
        return rooms.values().stream()
                .filter(room -> room.isRoomPlayer(player))
                .findFirst()
                .orElse(null);
    }

    public void createRoom(String name, Cuboid cuboid) {
        Room room = new Room(name, cuboid);
        rooms.put(name, room);

        saveRoom(room, false);
    }

    public void deleteRoom(Room room) {
        rooms.remove(room.getName());
        saveRoom(room, true);
    }

    public boolean isRoom(String name) {
        return rooms.containsKey(name);
    }

    public void checkEnterOrLeave(Player player, Room room, Location from, Location to) {
        if (!room.getCuboid().contains(from) && room.getCuboid().contains(to)) {
            Bukkit.getServer().getPluginManager().callEvent(new RoomEnteredEvent(player, room));
        }
        else if (room.getCuboid().contains(from) && !room.getCuboid().contains(to) && room.isRoomPlayer(player)) {
            Bukkit.getServer().getPluginManager().callEvent(new RoomLeftEvent(player, room));
        }
    }

    public void endRoom(Player winner, Player loser, Room room) {
        String roomName = room.getName();

        if (winner != null) {
            languageFile.getStringList("room-message.winner")
                    .forEach(message -> ChatUtil.sendMessage(winner, message
                            .replace("%room-name%", roomName)));
        }
        if (loser != null) {
            languageFile.getStringList("room-message.loser")
                    .forEach(message -> ChatUtil.sendMessage(loser, message
                            .replace("%room-name%", roomName)));
        }

        room.getPeopleInRoom().stream()
                .filter(Objects::nonNull)
                .forEach(roomPlayer ->
                        room.getPotionEffects().forEach(potionEffect ->
                                roomPlayer.removePotionEffect(potionEffect.getType()))
                );

        room.generateBorder(true);
        room.setBusy(false);
        room.getPeopleInRoom().clear();
    }

    public void saveRoom(Room room, boolean delete) {
        String roomName = room.getName();

        if (delete) {
            configFile.set("rooms." + roomName, null);
        }
        else {
            configFile.set("rooms." + roomName + ".cuboid", SerializeUtil.serializeCuboid(room.getCuboid()));
            configFile.set("rooms." + roomName + ".potionEffects", SerializeUtil.serializePotionEffects(room.getPotionEffects()));
        }

        configFile.save();
    }

    public void onLoad() {
        ConfigurationSection roomsSection = configFile.getConfiguration().getConfigurationSection("rooms");
        if (roomsSection == null) return;

        for (String roomName : roomsSection.getKeys(false)) {
            ConfigurationSection roomSection = roomsSection.getConfigurationSection(roomName);
            if (roomSection == null) continue;

            Room room = new Room(roomName, roomSection);
            rooms.put(roomName, room);
        }
    }

    public void onDisable() {
        rooms.values().stream()
                .filter(Room::isBusy)
                .forEach(room -> endRoom(null, null, room));
    }
}
