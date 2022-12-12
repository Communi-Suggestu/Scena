package com.communi.suggestu.scena.fabric.platform.fluid;

import com.communi.suggestu.scena.core.dist.DistExecutor;
import com.communi.suggestu.scena.core.fluid.FluidInformation;
import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

import static com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager.makeVariant;

@SuppressWarnings("UnstableApiUsage")
public class FabricFluidVariantHandlerDelegate implements IFluidVariantHandler
{
    private final FluidVariantAttributeHandler delegate;

    public FabricFluidVariantHandlerDelegate(final FluidVariantAttributeHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component getName(final FluidInformation fluidInformation)
    {
        return delegate.getName(makeVariant(fluidInformation));
    }

    @Override
    public Optional<SoundEvent> getFillSound(final FluidInformation variant)
    {
        return delegate.getFillSound(makeVariant(variant));
    }

    @Override
    public Optional<SoundEvent> getEmptySound(final FluidInformation variant)
    {
        return delegate.getEmptySound(makeVariant(variant));
    }

    @Override
    public int getLuminance(final FluidInformation variant)
    {
        return delegate.getLuminance(makeVariant(variant));
    }

    @Override
    public int getTemperature(final FluidInformation variant)
    {
        return delegate.getTemperature(makeVariant(variant));
    }

    @Override
    public int getViscosity(final FluidInformation variant)
    {
        return delegate.getViscosity(makeVariant(variant), null);
    }

    @Override
    public int getDensity(final FluidInformation variant)
    {
        if (delegate.isLighterThanAir(makeVariant(variant))) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int getTintColor(final FluidInformation variant)
    {
        return DistExecutor.unsafeRunForDist(
                () -> () -> FluidVariantRendering.getColor(makeVariant(variant)),
                () -> () -> 0xffffff
        );
    }

    @Override
    public Optional<ResourceLocation> getStillTexture(final FluidInformation variant)
    {

        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    final FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.fluid());
                    if (handler instanceof FabricFluidVariantRenderHandlerDelegate renderDelegate) {
                        return renderDelegate.getDelegate().getStillTexture(variant);
                    }

                    return Optional.ofNullable(FluidVariantRendering.getSprites(makeVariant(variant))).map(sprites -> sprites[0]).map(TextureAtlasSprite::contents).map(SpriteContents::name);
                },
                () -> Optional::empty
        );
    }

    @Override
    public Optional<ResourceLocation> getFlowingTexture(final FluidInformation variant)
    {
        return DistExecutor.unsafeRunForDist(
                () -> () -> {
                    final FluidVariantRenderHandler handler = FluidVariantRendering.getHandlerOrDefault(variant.fluid());
                    if (handler instanceof FabricFluidVariantRenderHandlerDelegate renderDelegate) {
                        return renderDelegate.getDelegate().getFlowingTexture(variant);
                    }

                    return Optional.ofNullable(FluidVariantRendering.getSprites(makeVariant(variant))).map(sprites -> sprites[1]).map(TextureAtlasSprite::contents).map(SpriteContents::name);
                },
                () -> Optional::empty
        );
    }
}
