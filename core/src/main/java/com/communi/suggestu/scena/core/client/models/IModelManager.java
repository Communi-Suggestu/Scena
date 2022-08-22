package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

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
}
