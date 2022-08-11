package com.communi.suggestu.scena.core.client.models.loaders;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * General interface for any model that can be baked, superset of vanilla {@link UnbakedModel}.
 * Models can be baked to different vertex formats and with different state.
 */
public interface IModelSpecification<T extends IModelSpecification<T>>
{
    BakedModel bake(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);

    Collection<Material> getTextures(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);
}
