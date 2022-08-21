package com.communi.suggestu.scena.forge.platform.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.context.IModelBakingContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.BlockGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.Optional;
import java.util.function.Function;

public class ForgeModelBakingContextDelegate implements IModelBakingContext
{

    final Function<ResourceLocation, UnbakedModel> unbakedModelGetter;
    final IGeometryBakingContext delegate;

    public ForgeModelBakingContextDelegate(final Function<ResourceLocation, UnbakedModel> unbakedModelGetter, final IGeometryBakingContext delegate) {
        this.unbakedModelGetter = unbakedModelGetter;
        this.delegate = delegate;}

    @Override
    public UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel)
    {
        return unbakedModelGetter.apply(unbakedModel);
    }

    @Override
    public Optional<Material> getMaterial(final String name)
    {
        if (delegate.hasMaterial(name))
        {
            return Optional.of(delegate.getMaterial(name));
        }

        return Optional.empty();
    }

    @Override
    public boolean isGui3d()
    {
        return delegate.isGui3d();
    }

    @Override
    public boolean useBlockLight()
    {
        return delegate.useBlockLight();
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return delegate.useAmbientOcclusion();
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return delegate.getTransforms();
    }

    @Override
    public ItemOverrides getItemOverrides()
    {
        if (delegate instanceof BlockGeometryBakingContext geometryBakingContext) {
            return geometryBakingContext.owner.getOverrides(
                    Minecraft.getInstance().getModelManager().getModelBakery(),
                    geometryBakingContext.owner,
                    material -> Minecraft.getInstance().getTextureAtlas(material.atlasLocation()).apply(material.texture())
            );
        }

        return ItemOverrides.EMPTY;
    }
}
