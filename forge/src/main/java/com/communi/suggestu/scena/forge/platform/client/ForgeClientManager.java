package com.communi.suggestu.scena.forge.platform.client;

import com.communi.suggestu.scena.core.client.IClientManager;
import com.communi.suggestu.scena.core.client.event.IClientEvents;
import com.communi.suggestu.scena.core.client.fluid.IClientFluidManager;
import com.communi.suggestu.scena.core.client.key.IKeyBindingManager;
import com.communi.suggestu.scena.core.client.models.data.IModelDataBuilder;
import com.communi.suggestu.scena.core.client.models.data.IModelDataKey;
import com.communi.suggestu.scena.core.client.models.data.IModelDataManager;
import com.communi.suggestu.scena.core.client.rendering.IColorManager;
import com.communi.suggestu.scena.core.client.rendering.IRenderingManager;
import com.communi.suggestu.scena.core.client.screens.IScreenManager;
import com.communi.suggestu.scena.core.client.textures.ITextureManager;
import com.communi.suggestu.scena.forge.platform.client.color.ForgeColorManager;
import com.communi.suggestu.scena.forge.platform.client.event.ForgeClientEvents;
import com.communi.suggestu.scena.forge.platform.client.fluid.ForgeClientFluidManager;
import com.communi.suggestu.scena.forge.platform.client.key.ForgeKeyBindingManager;
import com.communi.suggestu.scena.forge.platform.client.model.data.ForgeModelDataManager;
import com.communi.suggestu.scena.forge.platform.client.model.data.ForgeModelDataMapBuilderPlatformDelegate;
import com.communi.suggestu.scena.forge.platform.client.model.data.ForgeModelPropertyPlatformDelegate;
import com.communi.suggestu.scena.forge.platform.client.rendering.ForgeRenderingManager;
import com.communi.suggestu.scena.forge.platform.client.screens.ForgeScreenManager;
import com.communi.suggestu.scena.forge.platform.client.texture.ForgeTextureManager;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

public class ForgeClientManager implements IClientManager
{
    private static final ForgeClientManager INSTANCE = new ForgeClientManager();

    public static ForgeClientManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeClientManager()
    {
    }

    @Override
    public @NotNull IRenderingManager getRenderingManager()
    {
        return ForgeRenderingManager.getInstance();
    }

    @Override
    public @NotNull IModelDataBuilder createNewModelDataBuilder()
    {
        return new ForgeModelDataMapBuilderPlatformDelegate();
    }

    @Override
    public @NotNull <T> IModelDataKey<T> createNewModelDataKey()
    {
        return new ForgeModelPropertyPlatformDelegate<>(new ModelProperty<>());
    }

    @Override
    public @NotNull IModelDataManager getModelDataManager()
    {
        return ForgeModelDataManager.getInstance();
    }

    @Override
    public @NotNull IColorManager getColorManager()
    {
        return ForgeColorManager.getInstance();
    }

    @Override
    public @NotNull IKeyBindingManager getKeyBindingManager()
    {
        return ForgeKeyBindingManager.getInstance();
    }

    @Override
    public @NotNull IClientFluidManager getFluidManager()
    {
        return ForgeClientFluidManager.getInstance();
    }

    @Override
    public @NotNull ITextureManager getTextureManager()
    {
        return ForgeTextureManager.getInstance();
    }

    @Override
    public @NotNull IClientEvents getClientEvents() {
        return ForgeClientEvents.getInstance();
    }

    @Override
    public @NotNull IScreenManager getScreenManager() {
        return ForgeScreenManager.getInstance();
    }
}
