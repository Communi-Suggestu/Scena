package com.communi.suggestu.scena.core.item;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Represents a helper for managing items related to dye's.
 */
public interface IDyeItemHelper
{

    /**
     * The current dye item helper.
     *
     * @return The dye item helper.
     */
    static IDyeItemHelper getInstance() {
        return IScenaPlatform.getInstance().getDyeItemHelper();
    }

    /**
     * Gets the dye color of the item.
     *
     * @param stack The stack to get the color from.
     * @return The dye color of the item,  if the item has a color.
     */
    Optional<DyeColor> getColorFromItem(final ItemStack stack);
}
