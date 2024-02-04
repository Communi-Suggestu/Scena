package com.communi.suggestu.scena.forge.platform.client.model.data;

import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class ForgeModelPropertyPlatformDelegate<T> implements IModelDataKey<T>
{

    private final ModelProperty<T> property;

    public ForgeModelPropertyPlatformDelegate(final ModelProperty<T> property) {this.property = property;}

    public ModelProperty<T> getProperty()
    {
        return property;
    }
}
