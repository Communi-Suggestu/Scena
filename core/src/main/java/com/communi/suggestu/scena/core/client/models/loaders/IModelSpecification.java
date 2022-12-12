package com.communi.suggestu.scena.core.client.models.loaders;

import com.communi.suggestu.scena.core.client.models.loaders.context.IModelBakingContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
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
     * @param baker The bakery to use.
     * @param spriteGetter The sprite getter to use.
     * @param modelState The transformers to apply.
     * @param modelLocation The location of the model that is being baked.
     * @return The baked model.
     */
    BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation);
}
