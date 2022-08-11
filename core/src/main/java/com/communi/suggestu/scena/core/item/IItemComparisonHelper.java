package com.communi.suggestu.scena.core.item;

import com.communi.suggestu.scena.core.IScenaPlatform;
import net.minecraft.world.item.ItemStack;

/**
 * Provides utilities to compare items and itemstacks in a platform specific way.
 *
 * Some platforms extend the functionality of itemstacks beyond item, meta and nbt.
 * And sometimes these values have to be taken into account while comparing itemstacks.
 */
public interface IItemComparisonHelper
{
    /**
     * The item comparison helper of the platform at large.
     * Some platforms extend the functionality of itemstacks beyond item, meta and nbt.
     * And sometimes these values have to be taken into account while comparing itemstacks.
     *
     * @return The item comparison helper.
     */
    static IItemComparisonHelper getInstance() {
        return IScenaPlatform.getInstance().getItemComparisonHelper();
    }

    /**
     * Indicates if the given two stacks can stack.
     * Regardless of stack size.
     *
     * @param left The left stack to check.
     * @param right The right stack to check.
     * @return True when stackable, false when not.
     */
    boolean canItemStacksStack(ItemStack left, ItemStack right);
}
