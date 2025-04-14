package dev.risas.nokrooms.tasks;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.utilities.FileConfig;
import dev.risas.nokrooms.utilities.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RoomWinnerBorderTask extends BukkitRunnable {

    private final NokRooms plugin;
    private final FileConfig languageFile;

    private final Room room;
    private final Player winner;
    private int countdown;

    public RoomWinnerBorderTask(
            NokRooms plugin,
            Room room,
            Player winner,
            int countdown) {
        this.plugin = plugin;
        this.languageFile = plugin.getLanguageFile();
        this.room = room;
        this.winner = winner;
        this.countdown = countdown;
    }

    @Override
    public void run() {
        if (!winner.isOnline() || countdown <= 0) {
            cancel();

            room.generateBorder(true);
            return;
        }

        PlayerUtil.sendActionBar(winner, languageFile.getString("room-message.action-bar")
                .replace("%time%", String.valueOf(countdown)));
        countdown--;
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, 20L);
    }
}
