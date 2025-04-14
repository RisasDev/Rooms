package dev.risas.nokrooms.tasks;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.utilities.FileConfig;
import org.bukkit.scheduler.BukkitRunnable;

public class RoomStartingTask extends BukkitRunnable {

    private final NokRooms plugin;
    private final FileConfig configFile, languageFile;

    private final Room room;

    public RoomStartingTask(NokRooms plugin, Room room) {
        this.plugin = plugin;
        this.configFile = plugin.getConfigFile();
        this.languageFile = plugin.getLanguageFile();
        this.room = room;
    }

    @Override
    public void run() {
        room.setTask(null);

        if (room.getRoomSize() > room.getParticipants()) {
            room.sendRoomMessage(languageFile.getString("room-message.not-start"));
            return;
        }

        room.getPeopleInRoom().forEach(player -> room.getPotionEffects().forEach(player::addPotionEffect));

        room.setBusy(true);
        room.generateBorder(false);
        room.sendRoomMessage(languageFile.getString("room-message.start"));
    }

    public void start() {
        this.runTaskLater(plugin, 20L * configFile.getInt("room-settings.generate-glass-delay"));
    }
}
