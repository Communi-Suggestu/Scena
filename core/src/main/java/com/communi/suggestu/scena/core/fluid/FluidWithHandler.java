package com.communi.suggestu.scena.core.fluid;

import net.minecraft.world.level.material.Fluid;

/**
 * A custom fluid which has a variant handler.
 */
public abstract class FluidWithHandler extends Fluid
{

    /**
     * The variant handler for this fluid.
     *
     * @return The variant handler for this fluid.
     */
    public abstract IFluidVariantHandler getVariantHandler();
}
