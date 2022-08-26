package com.communi.suggestu.scena.core.client.textures;

import com.communi.suggestu.scena.core.client.IClientManager;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface ITextureManager
{

    static ITextureManager getInstance()
    {
        return IClientManager.getInstance().getTextureManager();
    }

    void registerTextures(final ResourceLocation atlas, final Consumer<ITextureToAtlasRegistrar> callback);

    interface ITextureToAtlasRegistrar
    {
        void registerTextureToAtlas(ResourceLocation texture);
    }
}
