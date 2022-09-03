package com.communi.suggestu.scena.core.client.models.baked;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Defines a baked model which is compounded of multiple models and can be queried for the models it is composed of.
 */
public interface ICompoundItemBakedModel extends BakedModel
{
    /**
     * Returns the layers of this model with the given stack.
     *
     * @param stack The stack to get the model for.
     * @param fabulous If we are rendering in fabulous mode or not.
     * @return The list of models.
     */
    List<BakedModel> getLayers(final ItemStack stack, final boolean fabulous);
}
