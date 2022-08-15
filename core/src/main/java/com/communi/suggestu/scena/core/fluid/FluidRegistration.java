package com.communi.suggestu.scena.core.fluid;

import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public record FluidRegistration(IRegistryObject<Fluid> fluid, Supplier<IFluidVariantHandler> variantHandler)
{
}
