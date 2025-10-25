package com.communi.suggestu.scena.forge.platform.client.model;

import com.communi.suggestu.scena.core.client.models.IModelManager;
import com.communi.suggestu.scena.core.client.models.loader.IUnbakedModelLoader;
import com.communi.suggestu.scena.forge.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public final class ForgeModelManager implements IModelManager
{
    private static final ForgeModelManager INSTANCE = new ForgeModelManager();

    public static ForgeModelManager getInstance()
    {
        return INSTANCE;
    }

    private final Collection<Consumer<IItemModelPropertyRegistrar>> modelPropertyRegistrars = Collections.synchronizedCollection(Lists.newArrayList());
    private final AtomicBoolean registeredModelProperties = new AtomicBoolean(false);

    private ForgeModelManager()
    {
    }

    @Override
    public void registerItemModelProperty(final Consumer<IItemModelPropertyRegistrar> callback)
    {
        if (registeredModelProperties.get()) {
            throw new IllegalStateException("Cannot register item model property after model loading has started.");
        }

        modelPropertyRegistrars.add(callback);
    }

    @SubscribeEvent
    public void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        getInstance().registeredModelProperties.set(true);
        getInstance().modelPropertyRegistrars.forEach(registrar -> registrar.accept(event::register));
    }
}
