package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.loader.IUnbakedModelLoader;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedModelDeserializer;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class FabricModelManager implements IModelManager
{
    private static final FabricModelManager INSTANCE = new FabricModelManager();

    public static FabricModelManager getInstance()
    {
        return INSTANCE;
    }

    private FabricModelManager()
    {
    }

    @Override
    public void registerModelLoader(final @NotNull ResourceLocation name, final @NotNull IUnbakedModelLoader<?> modelLoader)
    {
        UnbakedModelDeserializer.register(name, modelLoader::read);
    }

    @Override
    public void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback)
    {
        callback.accept(RangeSelectItemModelProperties.ID_MAPPER::put);
    }
}
