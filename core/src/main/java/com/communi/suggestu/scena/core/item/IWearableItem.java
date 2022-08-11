package com.communi.suggestu.scena.core.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Wearable;

/**
 * Represents an item which can be worn and which has control over the slot it belongs into.
 */
public interface IWearableItem extends Wearable
{

    /**
     * The slot for the wearable item.
     *
     * @return The slot.
     */
    EquipmentSlot getSlot();
}
