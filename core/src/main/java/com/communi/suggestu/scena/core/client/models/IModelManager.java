package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * The model manager of the platform.
 */
public interface IModelManager
{

    /**
     * The instance of the model manager for the current platform.
     *
     * @return The model manager.
     */
    static IModelManager getInstance() {
        return IRenderingManager.getInstance().getModelManager();
    }

    /**
     * Gets the model for the given resource location.
     *
     * @param unbakedModel The resource location of the model.
     * @return The model.
     */
    UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel);

    /**
     * Registers a model specification loader for the given resource location.
     *
     * @param name the name of the loader
     * @param modelLoader the loader.
     */
    void registerModelLoader(@NotNull final ResourceLocation name, @NotNull final IModelSpecificationLoader<?> modelLoader);
}
