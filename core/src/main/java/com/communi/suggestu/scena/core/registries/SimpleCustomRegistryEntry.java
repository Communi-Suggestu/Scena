package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.ResourceLocation;

public class SimpleCustomRegistryEntry<V> implements ICustomRegistryEntry
{
    private ResourceLocation name;

    @Override
    public ResourceLocation getRegistryName()
    {
        return name;
    }

    @SuppressWarnings("unchecked")
    public V setRegistryName(final ResourceLocation name)
    {
        this.name = name;
        return (V) this;
    }
}
