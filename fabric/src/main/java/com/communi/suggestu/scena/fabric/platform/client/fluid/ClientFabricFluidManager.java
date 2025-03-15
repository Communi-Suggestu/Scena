package com.communi.suggestu.scena.fabric.platform.client.fluid;

import com.communi.suggestu.scena.core.dist.Dist;
import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidVariantRenderHandlerDelegate;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;

public class ClientFabricFluidManager {
    public static void registerFluidAndVariant(IRegistryObject<Fluid> fluidRegistration, IFluidVariantHandler handler) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), fluidRegistration.get());
            FluidVariantRendering.register(fluidRegistration.get(), new FabricFluidVariantRenderHandlerDelegate(handler));
        });
    }
}
