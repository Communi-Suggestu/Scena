package com.communi.suggestu.scena.core.client.models.data;

import com.communi.suggestu.scena.core.client.IClientManager;

/**
 * Builder for a new model data instances.
 */
public interface IModelDataBuilder
{

    /**
     * Creates a new instance of a builder.
     * Always creates a builder without any value.
     *
     * @return The new data builder.
     */
    static IModelDataBuilder create() {
        return IClientManager.getInstance().createNewModelDataBuilder();
    }

    /**
     * Builds the new model data instance from the builders current configuration.
     *
     * @return The model data from the current setup.
     */
    IBlockModelData build();

    /**
     * Adds a new value and a key to the builder's configuration.
     *
     * @param key The key for the property.
     * @param value The value for the property.
     * @param <T> The type of the value.
     * @return The current builder instance.
     */
    <T> IModelDataBuilder withInitial(IModelDataKey<T> key, T value);
}
