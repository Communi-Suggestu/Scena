package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCustomRegistryEntry implements IChiselsAndBitsRegistryEntry
{
    private ResourceLocation registryName;

    @Override
    public ResourceLocation getRegistryName()
    {
        return registryName;
    }

    public void setRegistryName(final ResourceLocation registryName)
    {
        if (this.registryName != null)
        {
            throw new IllegalStateException("Can not set the registry name twice on the same object. Currently: %s tried to set it to: %s".formatted(this.registryName,
              registryName));
        }

        this.registryName = registryName;
    }
}
