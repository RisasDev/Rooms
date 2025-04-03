package dev.risas.nokrooms.models;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.tasks.RoomStartingTask;
import dev.risas.nokrooms.utilities.ChatUtil;
import dev.risas.nokrooms.utilities.SerializeUtil;
import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */

@Getter @Setter
public class Room {

    private final String name;
    private final Cuboid cuboid;
    private List<PotionEffect> potionEffects;
    private List<Player> peopleInRoom;
    private boolean busy, finished;
    private RoomStartingTask task;

    public Room(String name, Cuboid cuboid) {
        this.name = name;
        this.cuboid = cuboid;
        this.potionEffects = new ArrayList<>();
        this.peopleInRoom = new ArrayList<>();
    }

    public Room(String name, ConfigurationSection section) {
        this.name = name;
        this.cuboid = SerializeUtil.deserializeCuboid(section.getString("cuboid"));
        this.potionEffects = SerializeUtil.deserializePotionEffects(section.getStringList("potionEffects"));
        this.peopleInRoom = new ArrayList<>();
    }

    public Player getOpponent(Player player) {
        return peopleInRoom.stream()
                .filter(roomPlayer -> !roomPlayer.equals(player))
                .findFirst()
                .orElse(null);
    }

    public void addPlayer(Player player) {
        peopleInRoom.add(player);
    }

    public void removePlayer(Player player) {
        peopleInRoom.remove(player);
    }

    public boolean isRoomPlayer(Player player) {
        return peopleInRoom.contains(player);
    }

    public int getRoomSize() {
        return peopleInRoom.size();
    }

    public void addPotionEffect(PotionEffectType type, int amplifier) {
        potionEffects.add(new PotionEffect(type, PotionEffect.INFINITE_DURATION, amplifier - 1));
    }

    public void removePotionEffect(PotionEffectType type) {
        potionEffects.removeIf(potionEffect -> potionEffect.getType().equals(type));
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        return potionEffects.stream().anyMatch(potionEffect -> potionEffect.getType().equals(type));
    }

    public void generateBorder(boolean remove) {
        World world = cuboid.getWorld();
        int minX = cuboid.getX1();
        int maxX = cuboid.getX2();
        int minY = cuboid.getY1();
        int maxY = cuboid.getY2();
        int minZ = cuboid.getZ1();
        int maxZ = cuboid.getZ2();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x != minX && x != maxX && z != minZ && z != maxZ && y != minY && y != maxY) continue;

                    Block block = world.getBlockAt(x, y, z);
                    Material currentType = block.getType();

                    if (remove) {
                        if (currentType == Material.GLASS) {
                            block.setType(Material.AIR);
                        }
                    }
                    else {
                        if (currentType == Material.AIR) {
                            block.setType(Material.GLASS);
                        }
                    }
                }
            }
        }
    }

    public void startTask(NokRooms plugin) {
        this.task = new RoomStartingTask(plugin, this);
        this.task.start();
    }

    public void stopTask() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public boolean isStartingTask() {
        return this.task != null;
    }

    public void sendRoomMessage(String message) {
        peopleInRoom.forEach(player -> ChatUtil.sendMessage(player, message));
    }
}
