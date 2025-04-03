package dev.risas.nokrooms.commands;

import dev.risas.nokrooms.NokRooms;
import dev.risas.nokrooms.controllers.RoomController;
import dev.risas.nokrooms.models.Room;
import dev.risas.nokrooms.models.RoomSelection;
import dev.risas.nokrooms.utilities.ChatUtil;
import dev.risas.nokrooms.utilities.JavaUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
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
                    " &7● &e/" + label + " effect add <room> <efecto> <nivel> &7- &fAñade un efecto a una room.",
                    " &7● &e/" + label + " effect remove <room> <efecto> &7- &fElimina un efecto de una room.",
                    " &7● &e/" + label + " wand &7- &fTe da la herramienta para crear rooms.",
                    " &7● &e/" + label + " reload &7- &fRecarga la configuración.",
                    ChatUtil.NORMAL_LINE
            });
            return false;
        }

        switch (args[0].toLowerCase()) {
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
            case "effect" -> {
                String subCommand = args[1].toLowerCase();

                if (args.length < 4) {
                    ChatUtil.sendMessage(sender, "&cUsage: /" + label + " effect <add|remove> <nombre> <efecto> [nivel]");
                    return true;
                }

                String roomName = args[2];
                Room room = roomController.getRoom(roomName);

                if (room == null) {
                    ChatUtil.sendMessage(sender, "&cLa room '" + roomName + "' no existe.");
                    return true;
                }

                String effect = args[3];
                PotionEffectType type = PotionEffectType.getByName(effect);

                if (type == null) {
                    ChatUtil.sendMessage(sender, "&cEl efecto '" + effect + "' no existe.");
                    return true;
                }

                switch (subCommand) {
                    case "add" -> {
                        if (args.length < 5) {
                            ChatUtil.sendMessage(sender, "&cUsage: /" + label + " effect add <nombre> <efecto> <nivel>");
                            return true;
                        }

                        String levelStr = args[4];
                        Integer level = JavaUtil.getInteger(levelStr);

                        if (level == null || level < 1) {
                            ChatUtil.sendMessage(sender, "&cEl nivel debe ser un número entero mayor a 0.");
                            return true;
                        }

                        room.addPotionEffect(type, level);
                        roomController.saveRoom(room, false);
                        ChatUtil.sendMessage(sender, "&eEfecto '&f" + effect + "&e' añadido a la room '&6" + roomName + "&e' con nivel &f" + level + "&e.");
                    }
                    case "remove" -> {
                        if (!room.hasPotionEffect(type)) {
                            ChatUtil.sendMessage(sender, "&cLa room '" + roomName + "' no tiene el efecto '" + effect + "'.");
                            return true;
                        }

                        room.removePotionEffect(type);
                        roomController.saveRoom(room, false);
                        ChatUtil.sendMessage(sender, "&eEfecto '&f" + effect + "&e' eliminado de la room '&6" + roomName + "&e'.");
                    }
                    default -> ChatUtil.sendMessage(sender, "&cEl comando " + args[1] + " no existe.");
                }
            }
            case "wand" -> {
                if (!(sender instanceof Player player)) {
                    ChatUtil.sendMessage(sender, "&cEste comando solo puede ser ejecutado por un jugador.");
                    return false;
                }

                player.getInventory().addItem(RoomSelection.SELECTION_WAND);
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
        if (args.length == 1) {
            return List.of("create", "delete", "list", "teleport", "effect", "wand", "reload");
        }

        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "delete", "teleport" -> List.copyOf(roomController.getRooms().keySet());
                case "effect" -> List.of("add", "remove");
                default -> null;
            };
        }

        if (args.length == 3 && "effect".equalsIgnoreCase(args[0])) {
            return switch (args[1].toLowerCase()) {
                case "add", "remove" -> List.copyOf(roomController.getRooms().keySet());
                default -> null;
            };
        }

        if (args.length == 4 && "effect".equalsIgnoreCase(args[0])) {
            return switch (args[1].toLowerCase()) {
                case "add" -> Arrays.stream(PotionEffectType.values())
                        .map(PotionEffectType::getName)
                        .toList();
                case "remove" -> {
                    Room room = roomController.getRoom(args[2]);
                    yield (room != null) ?
                            room.getPotionEffects().stream()
                                    .map(effect -> effect.getType().getName())
                                    .toList()
                            : null;
                }
                default -> null;
            };
        }
        return null;
    }
}
