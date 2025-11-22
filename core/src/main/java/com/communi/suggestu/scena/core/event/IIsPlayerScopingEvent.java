package com.communi.suggestu.scena.core.event;

import net.minecraft.world.entity.player.Player;

/**
 * Event to check whether the player is scoping or not.
 * <p>
 *     Fired from {@link Player#isScoping()} and overrides its default behaviour.
 * </p>
 */
public interface IIsPlayerScopingEvent extends IEvent
{

    /**
     * Invoked by the event handler to check whether a player is scoping.
     * <p>
     *     If at least one handler returns true then the player is considered scoping.
     * </p>
     * @param player the player to check for.
     * @return True when scoping, false when not.
     */
    boolean isScoping(Player player);
}
