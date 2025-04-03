package dev.risas.nokrooms.listeners;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.controllers.RoomController;
import dev.risas.nokrooms.events.RoomEnteredEvent;
import dev.risas.nokrooms.events.RoomLeftEvent;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.models.RoomSelection;
import dev.risas.nokrooms.utilities.ChatUtil;
import dev.risas.nokrooms.utilities.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
public class RoomListener implements Listener {

    private final NokRooms plugin;
    private final FileConfig languageFile;
    private final RoomController roomController;

    public RoomListener(NokRooms plugin) {
        this.plugin = plugin;
        this.languageFile = plugin.getLanguageFile();
        this.roomController = plugin.getRoomController();
    }

    @EventHandler
    public void onRoomEnteredEvent(RoomEnteredEvent event) {
        Player player = event.getPlayer();

        Room room = event.getRoom();
        if (room.isBusy()) return;

        room.addPlayer(player);
        ChatUtil.sendMessage(player, languageFile.getString("room-message.join")
                .replace("%room-name%", room.getName()));

        if (room.getRoomSize() >= 2 && !room.isStartingTask()) {
            room.startTask(plugin);
        }
    }

    @EventHandler
    public void onRoomLeftEvent(RoomLeftEvent event) {
        Player player = event.getPlayer();

        Room room = event.getRoom();
        room.removePlayer(player);
        ChatUtil.sendMessage(player, languageFile.getString("room-message.leave")
                .replace("%room-name%", room.getName()));

        if (room.isBusy()) {
            roomController.endRoom(room.getOpponent(player), player, room);
        }
        else if (room.isStartingTask() && !room.isBusy()) {
            room.stopTask();
        }
        else if (room.getRoomSize() == 2 && !room.isStartingTask()) {
            room.startTask(plugin);
        }
    }

    @EventHandler
    public void onRoomCheckRegionEvent(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null) return;

        Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) return;

        Player player = event.getPlayer();

        Room room = Optional.ofNullable(roomController.getRoomByLocation(to))
                .orElseGet(() -> roomController.getRoomByLocation(from));
        if (room == null) return;

        roomController.checkEnterOrLeave(player, room, from, to);
    }

    @EventHandler
    public void onPlayerRoomDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Room room = roomController.getRoomByPlayer(player);
        if (room == null || !room.isBusy()) return;

        roomController.endRoom(player.getKiller(), player, room);
    }

    @EventHandler
    public void onPlayerRoomQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Room room = roomController.getRoomByPlayer(player);
        if (room == null) return;

        Bukkit.getPluginManager().callEvent(new RoomLeftEvent(player, room));
    }

    @EventHandler
    public void onRoomSelectionEvent(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) return;

        ItemStack item = event.getItem();
        if (item == null || !item.isSimilar(RoomSelection.SELECTION_WAND)) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Player player = event.getPlayer();
        Location location = clickedBlock.getLocation();

        int position = 0;

        RoomSelection selection = RoomSelection.createOrGetSelection(plugin, player);

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            position = 2;
            selection.setLocation1(location);
        }
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            position = 1;
            selection.setLocation2(location);
        }

        event.setCancelled(true);

        String message = "&e" + (position == 1 ? "Primera" : "Segunda") + " localización &f" +
                clickedBlock.getX() + "," +
                clickedBlock.getY() + "," +
                clickedBlock.getZ() + " &eha sido establecida.";

        if (selection.isFullSelected()) {
            message += " &7(&6" + selection.getCuboid().volume() + " bloques&7)";
        }

        ChatUtil.sendMessage(player, message);
    }
}
