package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

import static com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidManager.buildFluidStack;

public record ForgeFluidVariantHandlerDelegate(FluidType delegate) implements IFluidVariantHandler
{
    @Override
    public Component getName(final FluidInformation fluidInformation)
    {
        return delegate.getDescription(buildFluidStack(fluidInformation));
    }

    @Override
    public Optional<SoundEvent> getFillSound(final FluidInformation variant)
    {
        return Optional.ofNullable(delegate.getSound(buildFluidStack(variant), SoundActions.BUCKET_FILL));
    }

    @Override
    public Optional<SoundEvent> getEmptySound(final FluidInformation variant)
    {
        return Optional.ofNullable(delegate.getSound(buildFluidStack(variant), SoundActions.BUCKET_EMPTY));
    }

    @Override
    public int getTemperature(final FluidInformation variant)
    {
        return delegate.getTemperature(buildFluidStack(variant));
    }

    @Override
    public int getViscosity(final FluidInformation variant)
    {
        return delegate.getViscosity(buildFluidStack(variant));
    }

    @Override
    public int getDensity(final FluidInformation variant)
    {
        return delegate.getDensity(buildFluidStack(variant));
    }
}
