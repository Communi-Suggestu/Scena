package com.communi.suggestu.scena.forge.platform.client.fluid;

import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidManager;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

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
                                         .getTintColor(ForgeFluidManager.getInstance().buildFluidStack(fluid));
    }

}
