package com.communi.suggestu.scena.forge.platform.fluid;

import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
    public Component getDescription(final FluidStack stack)
    {
        return this.delegate.getName(buildFluidInformation(stack));
    }

    @Override
    public @Nullable SoundEvent getSound(final FluidStack stack, final SoundAction action)
    {
        if (action == SoundActions.BUCKET_FILL) {
            return this.delegate.getFillSound(buildFluidInformation(stack)).orElse(null);
        } else if (action == SoundActions.BUCKET_EMPTY) {
            return this.delegate.getEmptySound(buildFluidInformation(stack)).orElse(null);
        }

        return null;
    }

    @Override
    public int getLightLevel(final FluidStack stack)
    {
        return delegate.getLuminance(buildFluidInformation(stack));
    }

    @Override
    public int getDensity(final FluidStack stack)
    {
        return delegate.getDensity(buildFluidInformation(stack));
    }

    @Override
    public int getTemperature(final FluidStack stack)
    {
        return delegate.getTemperature(buildFluidInformation(stack));
    }

    @Override
    public int getViscosity(final FluidStack stack)
    {
        return delegate.getViscosity(buildFluidInformation(stack));
    }

    @Override
    public void initializeClient(final Consumer<IClientFluidTypeExtensions> consumer)
    {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public int getTintColor(final FluidStack stack)
            {
                return delegate.getTintColor(buildFluidInformation(stack));
            }

            @Override
            public ResourceLocation getStillTexture(final FluidStack stack)
            {
                return delegate.getStillTexture(buildFluidInformation(stack)).orElseThrow();
            }

            @Override
            public ResourceLocation getFlowingTexture(final FluidStack stack)
            {
                return delegate.getFlowingTexture(buildFluidInformation(stack)).orElseThrow();
            }
        });
    }
}
