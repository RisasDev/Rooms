package dev.risas.nokrooms.commands;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.controllers.RoomController;
import dev.risas.nokrooms.models.RoomSelection;
import dev.risas.nokrooms.utilities.ChatUtil;
import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
public class NokRoomsCommand implements CommandExecutor {

    private final NokRooms plugin;
    private final RoomController roomController;

    public NokRoomsCommand(NokRooms plugin) {
        this.plugin = plugin;
        this.roomController = plugin.getRoomController();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            ChatUtil.sendMessage(sender, new String[]{
                    ChatUtil.NORMAL_LINE,
                    "&6&lNokRooms Commands",
                    "",
                    " &7● &e/" + label + " create <name> &7- &fCrea una room.",
                    " &7● &e/" + label + " delete <name> &7- &fElimina una room",
                    " &7● &e/" + label + " list &7- &fMuestra todas las rooms.",
                    " &7● &e/" + label + " teleport <name> &7- &fTeletransporta a una room.",
                    " &7● &e/" + label + " reload &7- &fRecarga la configuración.",
                    ChatUtil.NORMAL_LINE
            });
            return false;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create" -> {
                if (!(sender instanceof Player player)) {
                    ChatUtil.sendMessage(sender, "&cEste comando solo puede ser ejecutado por un jugador.");
                    return false;
                }

                if (args.length < 2) {
                    ChatUtil.sendMessage(player, "&cUsage: /" + label + " create <name>");
                    return false;
                }

                String roomName = args[1];

                if (roomController.isRoom(roomName)) {
                    ChatUtil.sendMessage(player, "&cLa room '" + roomName + "' ya existe.");
                    return false;
                }

                RoomSelection roomSelection = RoomSelection.createOrGetSelection(plugin, player);

                if (!roomSelection.isFullSelected()) {
                    ChatUtil.sendMessage(player, "&cPor favor, selecciona un cubo completo para la room.");
                    return false;
                }

                Cuboid cuboid = roomSelection.getCuboid();
                roomController.createRoom(roomName, cuboid);
                roomSelection.clear(plugin, player);

                ChatUtil.sendMessage(player, "&aRoom '" + roomName + "' ha sido creada correctamente.");

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
                            boolean borderX = (x == minX || x == maxX);
                            boolean borderZ = (z == minZ || z == maxZ);
                            boolean borderY = (y == minY || y == maxY);

                            if (borderX || borderZ || borderY) {
                                Block block = world.getBlockAt(x, y, z);
                                if (block.getType() != Material.AIR) continue;

                                block.setType(Material.GLASS);
                            }
                        }
                    }
                }
            }
            case "delete" -> {

            }
            case "list" -> {

            }
            case "teleport" -> {

            }
            case "reload" -> {
                plugin.onReload();
                ChatUtil.sendMessage(sender, "&aNokRooms ha sido recargado correctamente.");
            }
            default -> ChatUtil.sendMessage(sender, "&cSubComando no encontrado. Usa /" + label + " para ver los comandos.");
        }
        return false;
    }
}
