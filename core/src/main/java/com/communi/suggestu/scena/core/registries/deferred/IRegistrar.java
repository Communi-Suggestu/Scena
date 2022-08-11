package com.communi.suggestu.scena.core.registries.deferred;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface IRegistrar<T>
{
    /**
     * Returns a new registrar for the type given in the namespace of the mod id.
     *
     * @param typeClass The type of the registry for the registrar.
     * @param modId The mod if.
     * @param <T> The type in the registry.
     * @return The registrar for a registry of the given type in the given namespace.
     */
    static <T, R extends T> IRegistrar<R> create(ResourceKey<? extends Registry<T>> typeClass, String modId)
    {
        return IRegistrarManager.getInstance().createRegistrar(typeClass, modId);
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name    The new entry's name, it will be automatically added to this registrar's namespace.
     * @param factory A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    <I extends T> IRegistryObject<I> register(String name, Supplier<? extends I> factory);
}
