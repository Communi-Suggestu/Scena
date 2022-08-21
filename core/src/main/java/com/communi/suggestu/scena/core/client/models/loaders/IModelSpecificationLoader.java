package com.communi.suggestu.scena.core.client.models.loaders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public interface IModelSpecificationLoader<T extends IModelSpecification<T>> extends ResourceManagerReloadListener
{
    T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);

    @Override
    default void onResourceManagerReload(@NotNull ResourceManager pResourceManager) {}
}
