package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.ResourceLocation;

/**
 * Represents an object which is part of a registry.
 */
public interface ICustomRegistryEntry
{
    /**
     * The name of the object in the registry.
     * Namespaced using {@link ResourceLocation}.
     * @return The "id" as name of the object in the registry.
     */
    ResourceLocation getRegistryName();
}
