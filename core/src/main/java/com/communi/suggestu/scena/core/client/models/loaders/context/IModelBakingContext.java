package com.communi.suggestu.scena.core.client.models.loaders.context;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * The context in which the model is baked
 */
public interface IModelBakingContext
{

    /**
     * Retrieves an unbaked model from the context.
     * This can be used to get a parent context.
     * <p>
     * Note the game keeps track of all unbaked models that are requested to be loaded, and will throw
     * an exception if a circular dependency is detected.
     * @param unbakedModel The name of the unbaked model to load.
     * @return The unbaked model, or null if it could not be found.
     */
    UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel);

    /**
     * Retries a material from a supported super model or context.
     *
     * @param name The name of the material.
     * @return The material, or empty if it could not be found.
     */
    Optional<Material> getMaterial(final String name);

    /**
     * Indicates if this context is baking a model that is rendered using 3D Light.
     *
     * @return {@code true} if the model is rendered using 3D Light, {@code false} otherwise.
     */
    boolean isGui3d();

    /**
     * Indicates if this context is baking a model that is rendered using a blocks lighting layout.
     *
     * @return {@code true} if the model is rendered using a blocks lighting layout, {@code false} otherwise.
     */
    boolean useBlockLight();

    /**
     * Indicates if this context is using ambient occlusion.
     *
     * @return {@code true} if ambient occlusion is used, {@code false} otherwise.
     */
    boolean useAmbientOcclusion();

    /**
     * The item transforms which should be applied to the model.
     *
     * @return The item transforms.
     */
    ItemTransforms getTransforms();

    /**
     * The item overrides which should be applied to the model.
     *
     * @param baker The model baker.
     * @return The item overrides.
     */
    ItemOverrides getItemOverrides(ModelBaker baker);
}
