package com.communi.suggestu.scena.fabric.platform.client.rendering;

import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;

import static com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager.makeVariant;

public final class FabricClientFluidManager implements IClientFluidManager
{
    private static final FabricClientFluidManager INSTANCE = new FabricClientFluidManager();

    public static FabricClientFluidManager getInstance()
    {
        return INSTANCE;
    }

    private FabricClientFluidManager()
    {
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public int getFluidColor(final FluidInformation fluid)
    {
        return DistExecutor.unsafeRunForDist(
                () -> () -> FluidVariantRendering.getColor(makeVariant(fluid)),
                () -> () -> 0xffffff
        );
    }
}
