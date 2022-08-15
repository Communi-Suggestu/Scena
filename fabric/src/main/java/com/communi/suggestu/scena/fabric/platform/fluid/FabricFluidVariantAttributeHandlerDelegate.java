package com.communi.suggestu.scena.fabric.platform.fluid;

import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager.makeInformation;

@SuppressWarnings("UnstableApiUsage")
public class FabricFluidVariantAttributeHandlerDelegate implements FluidVariantAttributeHandler
{

    private final IFluidVariantHandler delegate;

    public FabricFluidVariantAttributeHandlerDelegate(final IFluidVariantHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getName(final FluidVariant fluidVariant)
    {
        return delegate.getName(makeInformation(fluidVariant));
    }

    @Override
    public Optional<SoundEvent> getFillSound(final FluidVariant variant)
    {
        return delegate.getFillSound(makeInformation(variant));
    }

    @Override
    public Optional<SoundEvent> getEmptySound(final FluidVariant variant)
    {
        return delegate.getEmptySound(makeInformation(variant));
    }

    @Override
    public int getLuminance(final FluidVariant variant)
    {
        return delegate.getLuminance(makeInformation(variant));
    }

    @Override
    public int getTemperature(final FluidVariant variant)
    {
        return delegate.getTemperature(makeInformation(variant));
    }

    @Override
    public int getViscosity(final FluidVariant variant, @Nullable final Level world)
    {
        return delegate.getViscosity(makeInformation(variant));
    }

    @Override
    public boolean isLighterThanAir(final FluidVariant variant)
    {
        return delegate.getDensity(makeInformation(variant)) <= 0;
    }
}
