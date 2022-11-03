package com.communi.suggestu.scena.core.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * An event fired when a player picks up an item.
 */
@FunctionalInterface
public interface IItemEntityPickupEvent extends IEvent {

    /**
     * Called when an item is picked up.
     *
     * @param itemEntity The entity to pickup.
     * @param player The player picking up the item.
     * @return {@code true} when the pickup was handled by the event and further processing should be aborted.
     */
     boolean handle(final ItemEntity itemEntity, final Player player);
}
