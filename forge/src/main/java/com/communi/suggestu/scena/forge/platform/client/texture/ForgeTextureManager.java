package com.communi.suggestu.scena.forge.platform.client.texture;

import com.communi.suggestu.scena.core.client.textures.ITextureManager;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeTextureManager implements ITextureManager
{
    private static final ForgeTextureManager INSTANCE = new ForgeTextureManager();

    public static ForgeTextureManager getInstance()
    {
        return INSTANCE;
    }

    private final AtomicBoolean registered = new AtomicBoolean(false);
    private final Multimap<ResourceLocation, Consumer<ITextureManager.ITextureToAtlasRegistrar>> registrars = Multimaps.synchronizedMultimap(HashMultimap.create());

    private ForgeTextureManager()
    {
    }

    @SubscribeEvent
    public static void onTextureStitchOf(final TextureStitchEvent.Pre event) {
        getInstance().registered.set(true);
        if (!getInstance().registrars.containsKey(event.getAtlas().location())) {
            return;
        }

        getInstance().registrars.get(event.getAtlas().location()).forEach(registrar -> registrar.accept(event::addSprite));
    }

    @Override
    public void registerTextures(final ResourceLocation atlas, final Consumer<ITextureToAtlasRegistrar> callback)
    {
        if (registered.get()) {
            throw new IllegalStateException("Cannot register textures after the TextureStitchEvent.Pre event has fired.");
        }

        registrars.put(atlas, callback);
    }
}
