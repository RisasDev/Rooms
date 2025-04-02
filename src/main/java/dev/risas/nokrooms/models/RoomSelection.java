package dev.risas.nokrooms.models;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.utilities.ItemBuilder;
import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@Getter @Setter
public class RoomSelection {

    private Location location1, location2;

    public static final ItemStack SELECTION_WAND = new ItemBuilder(Material.WOODEN_AXE)
            .setName("&6&lSelección de room")
            .setLore(
                    "&7&m---------------------------------------",
                    "&eClic izquierdo &f#1 &eposición.",
                    "&eClic derecho &f#2 &eposición.",
                    "&7&m---------------------------------------"
            )
            .build();

    private static final String SELECTION_METADATA_KEY = "nokrooms-selection";

    public static RoomSelection createOrGetSelection(NokRooms plugin, Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (RoomSelection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }

        RoomSelection selection = new RoomSelection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(plugin, selection));
        return selection;
    }

    public Cuboid getCuboid() {
        return new Cuboid(location1, location2);
    }

    public boolean isFullSelected() {
        return location1 != null && location2 != null;
    }

    public void clear(NokRooms plugin, Player player) {
        location1 = null;
        location2 = null;
        player.removeMetadata(SELECTION_METADATA_KEY, plugin);
    }
}
