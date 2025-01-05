package com.communi.suggestu.scena.fabric.platform.client.rendering;

import net.minecraft.world.item.ItemStack;

/**
 * Mixin interface API to handle GUI graphics tooltip handling.
 */
public interface IGuiGraphicsTooltipHandler {

    /**
     * Sets the current item stack.
     *
     * @param stack the item stack
     */
    void scena$setCurrentStack(ItemStack stack);
}
