package com.communi.suggestu.scena.forge.platform.client.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public final class ForgeModelGeometryToSpecificationPlatformDelegator<T extends IModelSpecification<T>>
        implements IUnbakedGeometry<ForgeModelGeometryToSpecificationPlatformDelegator<T>>
{

    private final T delegate;

    public ForgeModelGeometryToSpecificationPlatformDelegator(final T delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public BakedModel bake(
      final IGeometryBakingContext owner,
      final ModelBakery bakery,
      final Function<Material, TextureAtlasSprite> spriteGetter,
      final ModelState modelTransform,
      final ItemOverrides overrides,
      final ResourceLocation modelLocation)
    {
        final ForgeModelBakingContextDelegate context = new ForgeModelBakingContextDelegate(bakery::getModel, owner);

        return new ForgeBakedModelDelegate(delegate.bake(
          context, bakery, spriteGetter, modelTransform, modelLocation
        ));
    }

    @Override
    public Collection<Material> getMaterials(
      final IGeometryBakingContext owner, final Function<ResourceLocation, UnbakedModel> modelGetter, final Set<Pair<String, String>> missingTextureErrors)
    {
        final ForgeModelBakingContextDelegate context = new ForgeModelBakingContextDelegate(modelGetter, owner);

        return delegate.getTextures(
          context, modelGetter, missingTextureErrors
        );
    }
}
