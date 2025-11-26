package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.Identifier;

/**
 * Represents an object which is part of a registry.
 */
public interface ICustomRegistryEntry
{
    /**
     * The name of the object in the registry.
     * Namespaced using {@link Identifier}.
     * @return The "id" as name of the object in the registry.
     */
    Identifier getRegistryName();
}
