package com.communi.suggestu.scena.fabric.platform.client.rendering.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

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

}
