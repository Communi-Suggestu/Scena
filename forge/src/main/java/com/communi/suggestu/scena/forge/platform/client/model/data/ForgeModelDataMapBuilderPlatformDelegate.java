package com.communi.suggestu.scena.forge.platform.client.model.data;

import com.communi.suggestu.scena.core.client.models.data.IBlockModelData;
import com.communi.suggestu.scena.core.client.models.data.IModelDataBuilder;
import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class ForgeModelDataMapBuilderPlatformDelegate implements IModelDataBuilder
{

    private final ModelData.Builder delegate;

    public ForgeModelDataMapBuilderPlatformDelegate() {
        delegate = ModelData.builder();
    }

    @Override
    public IBlockModelData build()
    {
        return new ForgeBlockModelDataPlatformDelegate(delegate.build());
    }

    @Override
    public <T> IModelDataBuilder withInitial(final IModelDataKey<T> key, final T value)
    {
        if (!(key instanceof ForgeModelPropertyPlatformDelegate))
            throw new IllegalArgumentException("The given key is not a Forge platform compatible model data key.");

        final ModelProperty<T> property = ((ForgeModelPropertyPlatformDelegate<T>) key).getProperty();
        delegate.with(property, value);

        return this;
    }
}
