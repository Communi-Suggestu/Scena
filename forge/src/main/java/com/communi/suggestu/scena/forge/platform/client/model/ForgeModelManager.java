package com.communi.suggestu.scena.forge.platform.client.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecification;
import com.communi.suggestu.scena.core.client.models.loaders.IModelSpecificationLoader;
import com.communi.suggestu.scena.forge.platform.client.model.loader.ForgePlatformModelLoaderPlatformDelegate;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeModelManager implements IModelManager
{
    private static final ForgeModelManager INSTANCE = new ForgeModelManager();

    public static ForgeModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Map<ResourceLocation, IModelSpecificationLoader<?>> loaders = Maps.newConcurrentMap();
    private final AtomicBoolean registered = new AtomicBoolean(false);

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
        if (registered.get()) {
            throw new IllegalStateException("Cannot register model loader after model loading has started.");
        }

        loaders.put(name, modelLoader);
    }

    @Override
    public void registerItemModelProperty(final @NotNull Item item, final @NotNull ResourceLocation name, final @NotNull ClampedItemPropertyFunction clampedItemPropertyFunction)
    {
        ItemProperties.register(item, name, clampedItemPropertyFunction);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onModelRegistry(final ModelEvent.RegisterGeometryLoaders event)
    {
        getInstance().registered.set(true);

        final ModContainer container = ModLoadingContext.get().getActiveContainer();

        getInstance().loaders.forEach((name, loader) -> {
            ModLoadingContext.get().setActiveContainer(ModList.get().getModContainerById(name.getNamespace()).orElseThrow());
            event.register(name.getPath(), new ForgePlatformModelLoaderPlatformDelegate<>(loader));
        });

        ModLoadingContext.get().setActiveContainer(container);
    }
}
