package com.communi.suggestu.scena.core.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Event fired when a particular player joins a world.
 */
@FunctionalInterface
public interface IPlayerJoinedWorldEvent extends IEvent {

    /**
     * Invoked when a player joins a world.
     *
     * @param player The player.
     * @param level The world a player joined.
     */
    void handle(Player player, Level level);
}
