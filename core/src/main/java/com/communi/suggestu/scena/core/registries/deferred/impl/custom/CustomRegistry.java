package com.communi.suggestu.scena.core.registries.deferred.impl.custom;

import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistry;
import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistryEntry;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class CustomRegistry<T extends IChiselsAndBitsRegistryEntry> implements IChiselsAndBitsRegistry<T>
{
    private final BiMap<ResourceLocation, T> registerMap = HashBiMap.create();

    @Override
    public Collection<T> getValues()
    {
        synchronized (registerMap) {
            return registerMap.values();
        }
    }

    @Override
    public Set<ResourceLocation> getNames()
    {
        synchronized (registerMap) {
            return registerMap.keySet();
        }
    }

    @Override
    public Optional<T> get(final ResourceLocation name)
    {
        synchronized (registerMap) {
            return Optional.ofNullable(registerMap.get(name));
        }
    }

    @Override
    public void forEach(final Consumer<T> consumer)
    {
        synchronized (registerMap) {
            registerMap.values().forEach(consumer);
        }
    }

    public void register(final T value) {
        synchronized (registerMap) {
            registerMap.put(value.getRegistryName(), value);
        }
    }

    public static final class Builder<E extends IChiselsAndBitsRegistryEntry> implements IChiselsAndBitsRegistry.Builder<E> {

        @Override
        public IChiselsAndBitsRegistry<E> build()
        {
            return new CustomRegistry<>();
        }
    }
}

