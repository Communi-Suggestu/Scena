package com.communi.suggestu.scena.forge.platform.client.fluid;

import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import static com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidManager.buildFluidStack;

public final class ForgeClientFluidManager implements IClientFluidManager
{
    private static final ForgeClientFluidManager INSTANCE = new ForgeClientFluidManager();

    public static ForgeClientFluidManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeClientFluidManager()
    {
    }

    @Override
    public int getFluidColor(final FluidInformation fluid)
    {
        return IClientFluidTypeExtensions.of(fluid.fluid())
                                         .getTintColor(buildFluidStack(fluid));
    }

}
