package com.communi.suggestu.scena.forge.platform.client.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.forge.platform.client.model.loader.ForgeBakedModelDelegate;
import com.communi.suggestu.scena.forge.platform.client.model.loader.ForgePlatformModelLoaderPlatformDelegate;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeModelManager implements IModelManager
{
    private static final ForgeModelManager INSTANCE = new ForgeModelManager();

    public static ForgeModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<ResourceLocation, IModelSpecificationLoader<?>> loaders = Maps.newConcurrentMap();
    private final AtomicBoolean registeredLoaders = new AtomicBoolean(false);

    private final Collection<Consumer<IItemModelPropertyRegistrar>> modelPropertyRegistrars = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean registeredModelProperties = new AtomicBoolean(false);

    private ForgeModelManager()
    {
    }

    @Override
    public UnbakedModel getUnbakedModel(final ResourceLocation unbakedModel)
    {
        return Minecraft.getInstance().getModelManager().getModelBakery().getModel(unbakedModel);
    }

    @Override
    public void registerModelLoader(final @NotNull ResourceLocation name, final @NotNull IModelSpecificationLoader<?> modelLoader)
    {
        if (registeredLoaders.get()) {
            throw new IllegalStateException("Cannot register model loader after model loading has started.");
        }

        loaders.put(name, modelLoader);
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModelRegistry(final ModelEvent.RegisterGeometryLoaders event)
    {
        getInstance().registeredLoaders.set(true);

        getInstance().loaders.forEach((name, loader) -> {
            event.register(name, new ForgePlatformModelLoaderPlatformDelegate<>(loader));
        });
    }

    @Override
    public BakedModel adaptToPlatform(final BakedModel bakedModel)
    {
        if (bakedModel instanceof ForgeBakedModelDelegate)
            return bakedModel;

        return new ForgeBakedModelDelegate(bakedModel);
    }

    @Override
    public void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback)
    {
        if (registeredModelProperties.get()) {
            throw new IllegalStateException("Cannot register item model property after model loading has started.");
        }

        modelPropertyRegistrars.add(callback);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModelRegistry(final FMLClientSetupEvent event)
    {
        getInstance().registeredModelProperties.set(true);
        getInstance().modelPropertyRegistrars.forEach(registrar -> registrar.accept(ItemProperties::register));
    }
}
