package com.communi.suggestu.scena.core.registries.deferred;

import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistry;
import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistryEntry;
import com.communi.suggestu.scena.core.registries.IPlatformRegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Manages the deferred registration system for the underlying platform.
 */
public interface IRegistrarManager
{
    /**
     * Gives access to the deferred registrar manager.
     *
     * @return The deferred registrar manager.
     */
    static IRegistrarManager getInstance() {
        return IPlatformRegistryManager.getInstance().getRegistrarManager();
    }

    /**
     * Returns a new registrar for the type given in the namespace of the mod id.
     *
     * @param typeClass The type of the registry for the registrar.
     * @param modId The mod if.
     * @param <T> The type in the registry.
     * @return The registrar for a registry of the given type in the given namespace.
     */
    <T extends IChiselsAndBitsRegistryEntry, R extends T> ICustomRegistrar<R> createCustomRegistrar(Class<T> typeClass, String modId);

    /**
     * Returns a new registrar for the type given in the namespace of the mod id.
     *
     * @param <T> The type in the registry.
     * @param typeClass The type of the registry for the registrar.
     * @param modId The mod if.
     * @return The registrar for a registry of the given type in the given namespace.
     */
    <T, R extends T> IRegistrar<R> createRegistrar(ResourceKey<? extends Registry<T>> typeClass, String modId);

    /**
     * Creates a new registry builder for the given registry type.
     *
     * @param <T> The type contained in the registry.
     * @return The registry builder.
     */
    <T extends IChiselsAndBitsRegistryEntry> IChiselsAndBitsRegistry.Builder<T> simpleBuilderFor();
}
