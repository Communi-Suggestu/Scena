package com.communi.suggestu.scena.core.registries.deferred.impl.custom;

import com.communi.suggestu.scena.core.registries.AbstractCustomRegistryEntry;
import com.communi.suggestu.scena.core.registries.ICustomRegistry;
import com.communi.suggestu.scena.core.registries.ICustomRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.ICustomRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class CustomRegistrar<T extends ICustomRegistryEntry> implements ICustomRegistrar<T>
{

    private ICustomRegistry<T> registry               = null;
    private final Collection<Supplier<T>>    preBuildValueSuppliers = new ConcurrentLinkedDeque<>();
    private final String                     owner;

    public CustomRegistrar(final String owner) {
        this.owner = owner;
    }

    @Override
    public Supplier<ICustomRegistry<T>> makeRegistry(final Supplier<ICustomRegistry.Builder<T>> registryBuilder)
    {
        if (registry == null)
        {
            registry = registryBuilder.get().build();
            if (!(registry instanceof CustomRegistry))
                throw new IllegalArgumentException("The builder does not produce a registry which is compatible with the Forge Platform runtime!");

            preBuildValueSuppliers.forEach(s -> ((CustomRegistry<T>) registry).register(s.get()));
        }

        return () -> registry;
    }

    @Override
    public <I extends T> IRegistryObject<I> register(final String name, final Supplier<? extends I> factory)
    {
        if (registry == null) {
            preBuildValueSuppliers.add(() -> {
                final T entry = factory.get();
                if (entry instanceof AbstractCustomRegistryEntry) {
                    ((AbstractCustomRegistryEntry) entry).setRegistryName(new ResourceLocation(owner, name));
                } else if (entry.getRegistryName() == null)
                {
                    throw new IllegalStateException("Tried to create a registry entry which does not extend SimpleChiselsAndBitsRegistryEntry and also has no name set!");
                }

                return entry;
            });

            return new CustomRegistryObject<>(new ResourceLocation(owner, name), () -> registry);
        }

        final T entry = factory.get();
        if (entry instanceof AbstractCustomRegistryEntry) {
            ((AbstractCustomRegistryEntry) entry).setRegistryName(new ResourceLocation(owner, name));
        } else if (entry.getRegistryName() == null)
        {
            throw new IllegalStateException("Tried to create a registry entry which does not extend SimpleChiselsAndBitsRegistryEntry and also has no name set!");
        }

        ((CustomRegistry<T>) registry).register(entry);

        return new CustomRegistryObject<>(new ResourceLocation(owner, name), () -> registry);
    }
}
