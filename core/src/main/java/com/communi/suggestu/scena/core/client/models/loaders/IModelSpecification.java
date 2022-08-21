package com.communi.suggestu.scena.core.client.models.loaders;

import com.communi.suggestu.scena.core.client.models.loaders.context.IModelBakingContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
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
    /**
     * Bakes this specification into a model.
     *
     * @param context The context to bake in.
     * @param bakery The bakery to use.
     * @param spriteGetter The sprite getter to use.
     * @param modelTransform The transformers to apply.
     * @param modelLocation The location of the model that is being baked.
     * @return The baked model.
     */
    BakedModel bake(IModelBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);

    /**
     * Retrieves the textures that are required for this model.
     *
     * @param context The context to bake in.
     * @param modelGetter The model getter to use.
     * @param missingTextureErrors The set of missing texture errors to add to.
     * @return The textures that are required for this model.
     */
    Collection<Material> getTextures(IModelBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);
}
