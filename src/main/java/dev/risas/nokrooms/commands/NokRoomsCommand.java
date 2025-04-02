package dev.risas.nokrooms.commands;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.controllers.RoomController;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.models.RoomSelection;
import dev.risas.nokrooms.utilities.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Risas
 * @date 01-04-2025
 * @discord https://risas.me/discord
 */
public class NokRoomsCommand implements CommandExecutor, TabCompleter {

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
                    " &7● &e/" + label + " create <room> &7- &fCrea una room.",
                    " &7● &e/" + label + " delete <room> &7- &fElimina una room",
                    " &7● &e/" + label + " list &7- &fMuestra todas las rooms.",
                    " &7● &e/" + label + " teleport <room> &7- &fTeletransporta a una room.",
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
                    ChatUtil.sendMessage(player, "&cUsage: /" + label + " create <room>");
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

                roomController.createRoom(roomName, roomSelection.getCuboid());
                roomSelection.clear(plugin, player);

                ChatUtil.sendMessage(player, "&aRoom '" + roomName + "' ha sido creada correctamente.");
            }
            case "delete" -> {
                if (args.length < 2) {
                    ChatUtil.sendMessage(sender, "&cUsage: /" + label + " delete <room>");
                    return false;
                }

                String roomName = args[1];

                if (!roomController.isRoom(roomName)) {
                    ChatUtil.sendMessage(sender, "&cLa room '" + roomName + "' no existe.");
                    return false;
                }

                Room room = roomController.getRoom(roomName);

                if (room.isBusy()) {
                    ChatUtil.sendMessage(sender, "&cNo puedes eliminar la room '" + roomName + "' ya que esta en uso.");
                    return false;
                }

                roomController.deleteRoom(room);
                ChatUtil.sendMessage(sender, "&aRoom '" + roomName + "' eliminado.");
            }
            case "list" -> {
                ChatUtil.sendMessage(sender, ChatUtil.NORMAL_LINE);
                ChatUtil.sendMessage(sender, "&6&lRooms");
                ChatUtil.sendMessage(sender, "");

                if (roomController.getRooms().isEmpty()) {
                    ChatUtil.sendMessage(sender, "&cNo hay rooms creadas");
                }
                else {
                    for (Room room : roomController.getRooms().values()) {
                        ChatUtil.sendMessage(sender, " &7● &e" + room.getName());
                    }
                }

                ChatUtil.sendMessage(sender, ChatUtil.NORMAL_LINE);
            }
            case "teleport" -> {
                if (!(sender instanceof Player player)) {
                    ChatUtil.sendMessage(sender, "&cEste comando solo puede ser ejecutado por un jugador.");
                    return false;
                }

                if (args.length < 2) {
                    ChatUtil.sendMessage(player, "&cUsage: /" + label + " teleport <room>");
                    return false;
                }

                String roomName = args[1];

                if (!roomController.isRoom(roomName)) {
                    ChatUtil.sendMessage(player, "&cLa room '" + roomName + "' no existe.");
                    return false;
                }

                Room room = roomController.getRoom(roomName);
                player.teleport(room.getCuboid().getCenter());

                ChatUtil.sendMessage(player, "&eTe has teletransportado al room '" + roomName + "'.");
            }
            case "reload" -> {
                plugin.onReload();
                ChatUtil.sendMessage(sender, "&aNokRooms ha sido recargado correctamente.");
            }
            default -> ChatUtil.sendMessage(sender, "&cComando no encontrado. Usa /" + label + " para ver los comandos.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? List.of("create", "delete", "list", "teleport", "reload") : null;
    }
}
