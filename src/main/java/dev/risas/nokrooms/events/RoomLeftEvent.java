package dev.risas.nokrooms.events;

import dev.risas.nokrooms.models.Room;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class RoomLeftEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Room room;

    public RoomLeftEvent(Player player, Room room) {
        this.player = player;
        this.room = room;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
