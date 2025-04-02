package dev.risas.nokrooms;

import dev.risas.nokrooms.commands.NokRoomsCommand;
import dev.risas.nokrooms.controllers.RoomController;
import dev.risas.nokrooms.listeners.RoomListener;
import dev.risas.nokrooms.utilities.FileConfig;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class NokRooms extends JavaPlugin {

    private FileConfig configFile;
    private RoomController roomController;

    @Override
    public void onEnable() {
        this.configFile = new FileConfig(this, "config.yml");
        this.roomController = new RoomController();

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new RoomListener(this), this);

        Objects.requireNonNull(this.getCommand("nokrooms")).setExecutor(new NokRoomsCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void onReload() {
        this.configFile.reload();
    }
}
