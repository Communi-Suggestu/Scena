package com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class FabricModelSpecificationUnbakedModelDelegate<S extends IModelSpecification<S>> implements UnbakedModel
{

    private final IModelSpecification<S> specification;

    public FabricModelSpecificationUnbakedModelDelegate(final IModelSpecification<S> specification) {this.specification = specification;}

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Sets.newHashSet();
    }

    @Override
    public Collection<Material> getMaterials(
            final @NotNull Function<ResourceLocation, UnbakedModel> modelGetter, final @NotNull Set<Pair<String, String>> missingTextureErrors)
    {
        return specification.getTextures(modelGetter, missingTextureErrors);
    }

    @Nullable
    @Override
    public BakedModel bake(
            final @NotNull ModelBakery modelBakery, final @NotNull Function<Material, TextureAtlasSprite> function, final @NotNull ModelState modelState, final @NotNull ResourceLocation resourceLocation)
    {
        return new FabricBakedModelDelegate(specification.bake(modelBakery, function, modelState, resourceLocation));
    }
}
