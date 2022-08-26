package com.communi.suggestu.scena.fabric.platform.client.texture;

import com.communi.suggestu.scena.core.client.textures.ITextureManager;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public final class FabricTextureManager implements ITextureManager
{
    private static final FabricTextureManager INSTANCE = new FabricTextureManager();

    public static FabricTextureManager getInstance()
    {
        return INSTANCE;
    }

    private FabricTextureManager()
    {
    }

    public void registerTextures(final ResourceLocation atlas, final Consumer<ITextureManager.ITextureToAtlasRegistrar> callback)
    {
        ClientSpriteRegistryCallback.event(atlas).register((atlasTexture, registry) -> callback.accept(registry::register));
    }
}
