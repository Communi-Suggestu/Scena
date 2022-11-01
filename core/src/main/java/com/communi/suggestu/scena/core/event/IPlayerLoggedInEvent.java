package com.communi.suggestu.scena.core.event;

import net.minecraft.world.entity.player.Player;

/**
 * Event fired when a particular player logs in.
 */
@FunctionalInterface
public interface IPlayerLoggedInEvent extends IEvent {

    /**
     * Invoked when a player logs in.
     *
     * @param player The player that logs in.
     */
    void handle(final Player player);
}
