package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.communi.suggestu.scena.forge.platform.fluid.ForgeFluidManager.buildFluidInformation;

public class ForgeFluidTypeDelegate extends FluidType
{
    private final IFluidVariantHandler delegate;

    public ForgeFluidTypeDelegate(final IFluidVariantHandler delegate)
    {
        super(Properties.create());
        this.delegate = delegate;
    }

    @Override
    public @NotNull Component getDescription(final @NotNull FluidStack stack)
    {
        return this.delegate.getName(buildFluidInformation(stack));
    }

    @Override
    public @Nullable SoundEvent getSound(final @NotNull FluidStack stack, final @NotNull SoundAction action)
    {
        if (action == SoundActions.BUCKET_FILL) {
            return this.delegate.getFillSound(buildFluidInformation(stack)).orElse(null);
        } else if (action == SoundActions.BUCKET_EMPTY) {
            return this.delegate.getEmptySound(buildFluidInformation(stack)).orElse(null);
        }

        return null;
    }

    @Override
    public int getDensity(final @NotNull FluidStack stack)
    {
        return delegate.getDensity(buildFluidInformation(stack));
    }

    @Override
    public int getTemperature(final @NotNull FluidStack stack)
    {
        return delegate.getTemperature(buildFluidInformation(stack));
    }

    @Override
    public int getViscosity(final @NotNull FluidStack stack)
    {
        return delegate.getViscosity(buildFluidInformation(stack));
    }
}
