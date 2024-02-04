package com.communi.suggestu.scena.forge.platform.registry.registrar;

import com.communi.suggestu.scena.core.IScenaPlatform;
import com.communi.suggestu.scena.core.registries.ICustomRegistry;
import com.communi.suggestu.scena.core.registries.ICustomRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.ICustomRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrarManager;
import com.communi.suggestu.scena.core.registries.deferred.impl.custom.CustomRegistryManager;
import com.communi.suggestu.scena.forge.platform.ForgeScenaPlatform;
import com.communi.suggestu.scena.forge.platform.registry.registrar.delegates.ForgeDeferredRegisterPlatformDelegate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ForgeRegistrarManager implements IRegistrarManager
{
    private static final ForgeRegistrarManager INSTANCE = new ForgeRegistrarManager();

    public static ForgeRegistrarManager getInstance()
    {
        return INSTANCE;
    }

    private ForgeRegistrarManager()
    {
    }

    @Override
    public <T extends ICustomRegistryEntry, R extends T> ICustomRegistrar<R> createCustomRegistrar(
      final Class<T> typeClass, final String modId)
    {
        return CustomRegistryManager.getInstance().createNewRegistrar(typeClass, modId);
    }

    @Override
    public <T, R extends T> IRegistrar<R> createRegistrar(final ResourceKey<? extends Registry<T>> key, final String modId)
    {
        final DeferredRegister register = DeferredRegister.create((ResourceKey) key, modId);

        if (!(IScenaPlatform.getInstance() instanceof ForgeScenaPlatform forgeScenaPlatform))
            throw new IllegalStateException("Platform is not a Forge platform");

        register.register(forgeScenaPlatform.getModBus());

        return new ForgeDeferredRegisterPlatformDelegate(
          register
        );
    }

    @Override
    public <T extends ICustomRegistryEntry> ICustomRegistry.Builder<T> simpleBuilderFor()
    {
        return CustomRegistryManager.getInstance().createNewSimpleBuilder();
    }
}
