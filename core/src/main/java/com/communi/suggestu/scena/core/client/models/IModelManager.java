package com.communi.suggestu.scena.core.client.models;

import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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

    /**
     * Registers a new callback for item model property registration.
     *
     * @param callback The callback.
     */
    void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback);

    /**
     * Adapts a given baked model to the current platform.
     * Adaptation might not be necessary in all cases, but this method allows the underlying platform to adapt a default vanilla {@link BakedModel},
     * to platform specific implementations, unlocking additional functionality.
     *
     * @param bakedModel The baked model to adapt.
     * @return The adapted model.
     */
    BakedModel adaptToPlatform(final BakedModel bakedModel);

    interface IItemModelPropertyRegistrar {
        /**
         * Register a new item model property to this registrar.
         *
         * @param item The item to register the property for.
         * @param name The name of the property.
         * @param clampedItemPropertyFunction The function to get the property value.
         */
        void registerItemModelProperty(@NotNull final Item item, @NotNull final ResourceLocation name, @NotNull final ClampedItemPropertyFunction clampedItemPropertyFunction);
    }
}
