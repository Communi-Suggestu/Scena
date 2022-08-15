package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.Optional;

import static com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidManager.buildFluidStack;

public class ForgeFluidVariantHandlerDelegate implements IFluidVariantHandler
{

    private final FluidType delegate;

    public ForgeFluidVariantHandlerDelegate(final FluidType delegate) {this.delegate = delegate;}

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
    public int getLuminance(final FluidInformation variant)
    {
        return delegate.getLightLevel(buildFluidStack(variant));
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

    @Override
    public int getTintColor(final FluidInformation variant)
    {
        return IClientFluidTypeExtensions.of(delegate).getTintColor(buildFluidStack(variant));
    }

    @Override
    public Optional<ResourceLocation> getStillTexture(final FluidInformation variant)
    {
        return Optional.ofNullable(IClientFluidTypeExtensions.of(delegate).getStillTexture(buildFluidStack(variant)));
    }

    @Override
    public Optional<ResourceLocation> getFlowingTexture(final FluidInformation variant)
    {
        return Optional.ofNullable(IClientFluidTypeExtensions.of(delegate).getFlowingTexture(buildFluidStack(variant)));
    }
}
