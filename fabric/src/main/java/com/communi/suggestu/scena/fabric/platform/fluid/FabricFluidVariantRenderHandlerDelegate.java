package com.communi.suggestu.scena.fabric.platform.fluid;

import com.communi.suggestu.scena.core.fluid.IFluidVariantHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

import static com.communi.suggestu.scena.fabric.platform.fluid.FabricFluidManager.makeInformation;

@SuppressWarnings({"UnstableApiUsage", "resource"})
public class FabricFluidVariantRenderHandlerDelegate implements FluidVariantRenderHandler
{
    private final IFluidVariantHandler delegate;

    public FabricFluidVariantRenderHandlerDelegate(final IFluidVariantHandler delegate) {this.delegate = delegate;}

    @Override
    public @Nullable TextureAtlasSprite[] getSprites(final FluidVariant fluidVariant)
    {
        return new TextureAtlasSprite[] {
            Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(delegate.getStillTexture(makeInformation(fluidVariant)).orElseThrow()),
            delegate.getFlowingTexture(makeInformation(fluidVariant)).map(texture -> Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture)).orElse(null)
        };
    }

    @Override
    public int getColor(final FluidVariant fluidVariant, @Nullable final BlockAndTintGetter view, @Nullable final BlockPos pos)
    {
        return delegate.getTintColor(makeInformation(fluidVariant));
    }
}
