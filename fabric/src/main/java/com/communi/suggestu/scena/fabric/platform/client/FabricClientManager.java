package com.communi.suggestu.scena.fabric.platform.client;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.client.event.IClientEvents;
import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.client.key.IKeyBindingManager;
import com.communi.suggestu.scena.core.client.models.data.IModelDataBuilder;
import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import com.communi.suggestu.scena.core.client.models.data.IModelDataManager;
import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.textures.ITextureManager;
import com.communi.suggestu.scena.fabric.platform.client.events.FabricClientEvents;
import com.communi.suggestu.scena.fabric.platform.client.keys.FabricKeyBindingManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.FabricClientFluidManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.FabricColorManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.FabricRenderingManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.data.FabricModelDataManager;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.data.ModelDataBuilder;
import com.communi.suggestu.scena.fabric.platform.client.rendering.model.data.ModelDataKey;
import com.communi.suggestu.scena.fabric.platform.client.texture.FabricTextureManager;
import org.jetbrains.annotations.NotNull;

public final class FabricClientManager implements IClientManager
{
    private static final FabricClientManager INSTANCE = new FabricClientManager();

    public static FabricClientManager getInstance()
    {
        return INSTANCE;
    }

    private FabricClientManager()
    {
    }

    @Override
    public @NotNull IRenderingManager getRenderingManager()
    {
        return FabricRenderingManager.getInstance();
    }

    @Override
    public @NotNull IModelDataBuilder createNewModelDataBuilder()
    {
        return new ModelDataBuilder();
    }

    @Override
    public @NotNull <T> IModelDataKey<T> createNewModelDataKey()
    {
        return new ModelDataKey<>();
    }

    @Override
    public @NotNull IModelDataManager getModelDataManager()
    {
        return FabricModelDataManager.getInstance();
    }

    @Override
    public @NotNull IColorManager getColorManager()
    {
        return FabricColorManager.getInstance();
    }

    @Override
    public @NotNull IKeyBindingManager getKeyBindingManager()
    {
        return FabricKeyBindingManager.getInstance();
    }

    @Override
    public @NotNull IClientFluidManager getFluidManager()
    {
        return FabricClientFluidManager.getInstance();
    }

    @Override
    public @NotNull ITextureManager getTextureManager()
    {
        return FabricTextureManager.getInstance();
    }

    @Override
    public @NotNull IClientEvents getClientEvents() {
        return FabricClientEvents.getInstance();
    }
}
