package dev.risas.nokrooms.listeners;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.models.RoomSelection;
import dev.risas.nokrooms.utilities.ChatUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
public class RoomListener implements Listener {

    private final NokRooms plugin;

    public RoomListener(NokRooms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRoomSelectionInteract(PlayerInteractEvent event) {
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
