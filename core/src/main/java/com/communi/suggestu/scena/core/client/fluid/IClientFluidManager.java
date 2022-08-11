package com.communi.suggestu.scena.core.client.fluid;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import net.minecraft.world.level.material.Fluid;

/**
 * The client sided fluid manager.
 */
public interface IClientFluidManager
{

    /**
     * The current client fluid manager.
     *
     * @return The client fluid manager.
     */
    static IClientFluidManager getInstance() {
        return IClientManager.getInstance().getFluidManager();
    }

    /**
     * Gives access to the color of the fluid.
     *
     * @param fluid The fluid.
     * @return The color packed into an int.
     */
    int getFluidColor(FluidInformation fluid);

    /**
     * Gives access to the color of the fluid.
     *
     * @param fluid The fluid.
     * @return The color packed into an int.
     */
    default int getFluidColor(final Fluid fluid) {
        return getFluidColor(new FluidInformation(fluid));
    }
}
