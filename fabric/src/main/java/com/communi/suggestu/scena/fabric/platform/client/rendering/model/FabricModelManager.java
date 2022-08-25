package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.loader.FabricPlatformModelLoaderPlatformDelegate;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

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
    public UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel)
    {
        final IModelBakeryAccessor accessor = (IModelBakeryAccessor) Minecraft.getInstance().getModelManager();
        return accessor.getModelBakery().getModel(unbakedModel);
    }

    @Override
    public void registerModelLoader(final @NotNull ResourceLocation name, final @NotNull IModelSpecificationLoader<?> modelLoader)
    {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> new FabricPlatformModelLoaderPlatformDelegate<>(name, modelLoader));
    }
}
