package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.Identifier;

public class SimpleCustomRegistryEntry<V> implements ICustomRegistryEntry
{
    private Identifier name;

    @Override
    public Identifier getRegistryName()
    {
        return name;
    }

    @SuppressWarnings("unchecked")
    public V setRegistryName(final Identifier name)
    {
        this.name = name;
        return (V) this;
    }
}
