package com.communi.suggestu.scena.forge.platform.client.model.data;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public class ForgeBlockModelDataPlatformDelegate implements IBlockModelData
{

    private final ModelData delegate;

    public ForgeBlockModelDataPlatformDelegate(final ModelData delegate) {this.delegate = delegate;}

    @Override
    public boolean hasProperty(final IModelDataKey<?> prop)
    {
        if (!(prop instanceof ForgeModelPropertyPlatformDelegate))
            throw new IllegalArgumentException("The given key is not a Forge platform compatible model data key.");

        final ModelProperty<?> property = ((ForgeModelPropertyPlatformDelegate<?>) prop).getProperty();

        return delegate.has(property);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getData(final IModelDataKey<T> prop)
    {
        if (!(prop instanceof ForgeModelPropertyPlatformDelegate))
            throw new IllegalArgumentException("The given key is not a Forge platform compatible model data key.");

        final ModelProperty<?> property = ((ForgeModelPropertyPlatformDelegate<?>) prop).getProperty();

        return (T) delegate.get(property);
    }

    public ModelData getDelegate()
    {
        return delegate;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (!(o instanceof final ForgeBlockModelDataPlatformDelegate that)) return false;

        return getDelegate().equals(that.getDelegate());
    }

    @Override
    public int hashCode()
    {
        return getDelegate().hashCode();
    }
}
