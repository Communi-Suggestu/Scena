package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.models.loader.IUnbakedModelLoader;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
     * Registers a new callback for item model property registration.
     *
     * @param callback The callback.
     */
    void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback);

    interface IItemModelPropertyRegistrar {
        /**
         * Register a new item model property to this registrar.
         *
         * @param name The name of the property.
         * @param clampedItemPropertyFunction The function to get the property value.
         */
        void registerItemModelProperty(@NotNull final ResourceLocation name, @NotNull final MapCodec<? extends RangeSelectItemModelProperty> clampedItemPropertyFunction);
    }
}
