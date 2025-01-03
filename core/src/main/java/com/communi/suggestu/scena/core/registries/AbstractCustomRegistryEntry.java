package com.communi.suggestu.scena.core.registries;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public abstract class AbstractCustomRegistryEntry implements ICustomRegistryEntry
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractCustomRegistryEntry that)) return false;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRegistryName());
    }
}
