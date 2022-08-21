package com.communi.suggestu.scena.core.client.models;

import net.minecraft.client.resources.model.Material;

public interface IUnbakedModelWithMaterialAccess
{
    Material getMaterial(final String name);
}
