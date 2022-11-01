package com.communi.suggestu.scena.core.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * An event fired when a player picks up an item.
 */
@FunctionalInterface
public interface IEntityItemPickupEvent extends IEvent {

    /**
     * Called when an item is picked up.
     *
     * @param stackToPickup The stack to pickup.
     * @param player The player picking up the item.
     * @return {@code true} when the pickup was handled by the event and further processing should be aborted.
     */
     boolean handle(final ItemStack stackToPickup, final Player player);
}
