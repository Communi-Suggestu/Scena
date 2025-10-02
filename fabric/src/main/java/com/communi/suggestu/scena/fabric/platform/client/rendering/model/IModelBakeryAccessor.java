package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import net.minecraft.client.resources.model.ModelBakery;

/**
 * Fabric specific platform interface which gives access to the model bakery.
 */
public interface IModelBakeryAccessor
{

    /**
     * The model bakery.
     *
     * @return The model bakery.
     */
    default ModelBakery getModelBakery() {
        return scena$getModelBakeryInternal();
    }

    ModelBakery scena$getModelBakeryInternal();

    void scena$setModelBakeryInternal(ModelBakery modelBakery);
}
