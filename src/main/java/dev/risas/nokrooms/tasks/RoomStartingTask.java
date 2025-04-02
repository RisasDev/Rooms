package dev.risas.nokrooms.tasks;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.utilities.ChatUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class RoomStartingTask extends BukkitRunnable {

    private final NokRooms plugin;
    private final Room room;

    public RoomStartingTask(NokRooms plugin, Room room) {
        this.plugin = plugin;
        this.room = room;
    }

    @Override
    public void run() {
        if (room.getPeopleInRoom().size() > 2) {
            room.sendRoomMessage("&cHay muchas personas en la sala para comenzar el duelo.");
            return;
        }

        room.setBusy(true);
        room.generateGlass();
        ChatUtil.sendMessage();
    }

    public void start() {
        this.runTaskLater(plugin, 10L);
    }
}
