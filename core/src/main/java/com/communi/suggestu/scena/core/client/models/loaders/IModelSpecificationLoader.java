package com.communi.suggestu.scena.core.client.models.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public interface IModelSpecificationLoader<T extends IModelSpecification<T>> extends ResourceManagerReloadListener
{
    T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);
}
