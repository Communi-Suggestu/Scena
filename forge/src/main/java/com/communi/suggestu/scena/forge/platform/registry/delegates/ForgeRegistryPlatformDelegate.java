package com.communi.suggestu.scena.forge.platform.registry.delegates;

import com.communi.suggestu.scena.core.registries.IPlatformRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class ForgeRegistryPlatformDelegate<T> implements IPlatformRegistry<T>
{

    private final IForgeRegistry<T> delegate;

    public ForgeRegistryPlatformDelegate(final IForgeRegistry<T> delegate) {this.delegate = delegate;}

    @Override
    public Collection<T> getValues()
    {
        return delegate.getValues();
    }

    @Override
    public Set<ResourceLocation> getKeys()
    {
        return delegate.getKeys();
    }

    @Override
    public Optional<T> getValue(final ResourceLocation key)
    {
        if (!delegate.containsKey(key))
            return Optional.empty();

        return Optional.ofNullable(delegate.getValue(key));
    }

    @Override
    public ResourceLocation getKey(final T value)
    {
        return delegate.getKey(value);
    }
}
