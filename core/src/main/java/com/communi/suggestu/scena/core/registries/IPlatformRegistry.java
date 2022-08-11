package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A registry on the game platform for a specific type.
 *
 * @param <T> The type stored in the registry, like ITEM, or BLOCK
 */
public interface IPlatformRegistry<T>
{
    /**
     * Gives all values stored in the current registry.
     *
     * @return All values in the registry.
     */
    Collection<T> getValues();

    /**
     * Gives all keys stored in the current registry.
     *
     * @return All keys stored in the registry.
     */
    Set<ResourceLocation> getKeys();

    /**
     * Returns the value associated with the given key, if it is present.
     * If an unknown key is passed in, an empty optional is returned.
     *
     * @param key The key to lookup.
     * @return An optional with the value for the given key, or empty if unknown.
     */
    Optional<T> getValue(final ResourceLocation key);

    /**
     * Returns the key associated with the value.
     * Does not guarantee that the given object is stored in this registry.
     *
     * @param value The value to get the key for.
     * @return The key for the value.
     */
    ResourceLocation getKey(final T value);

}
