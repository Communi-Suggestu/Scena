package com.communi.suggestu.scena.core.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;

/**
 * Represents an item which can be worn and which has control over the slot it belongs into.
 */
public interface IWearableItem extends Equipable
{
    /**
     * The slot for the wearable item.
     *
     * @return The slot.
     * @implNote By default, this method returns the same value as {@link #getEquipmentSlot()}.
     */
    default EquipmentSlot getSlot() {
        return this.getEquipmentSlot();
    }
}
