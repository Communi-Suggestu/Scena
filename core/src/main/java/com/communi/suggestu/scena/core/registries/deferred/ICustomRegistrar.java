package com.communi.suggestu.scena.core.registries.deferred;

import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistry;
import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistryEntry;

import java.util.function.Supplier;

/**
 * An object which is capable of registering new instances to registries at runtime.
 *
 * @param <T> The type of the entry in the registry.
 */
public interface ICustomRegistrar<T extends IChiselsAndBitsRegistryEntry> extends IRegistrar<T>
{
    /**
     * Returns a new registrar for the type given in the namespace of the mod id.
     *
     * @param typeClass The type of the registry for the registrar.
     * @param modId The mod if.
     * @param <T> The type in the registry.
     * @return The registrar for a registry of the given type in the given namespace.
     */
    static <T extends IChiselsAndBitsRegistryEntry, R extends T> ICustomRegistrar<R> create(Class<T> typeClass, String modId)
    {
        return IRegistrarManager.getInstance().createCustomRegistrar(typeClass, modId);
    }

    /**
     * Register a new registry for this registrar.
     *
     * @param registryBuilder A supplier which can configure and return a registry builder to build the registry.
     * @return A supplier for the registry.
     */
    Supplier<IChiselsAndBitsRegistry<T>> makeRegistry(Supplier<IChiselsAndBitsRegistry.Builder<T>> registryBuilder);
}
