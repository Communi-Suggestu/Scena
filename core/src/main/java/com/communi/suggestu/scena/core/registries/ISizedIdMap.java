package com.communi.suggestu.scena.core.registries;

import net.minecraft.core.IdMap;

/**
 * An id map with a size.
 *
 * @param <T> The type to map to integer ids.
 */
public interface ISizedIdMap<T> extends IdMap<T>
{
    /**
     * Determines the size of the id map.
     *
     * @return The current size of the id map.
     */
    int size();
}
