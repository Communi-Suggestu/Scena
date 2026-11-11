package com.communi.suggestu.scena.fabric.platform.registry.registar;

import com.communi.suggestu.scena.fabric.platform.registry.registar.delegates.FabricVanillaRegistryRegistrarDelegate;
import com.communi.suggestu.scena.core.registries.ICustomRegistry;
import com.communi.suggestu.scena.core.registries.ICustomRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.ICustomRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrarManager;
import com.communi.suggestu.scena.core.registries.deferred.impl.custom.CustomRegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public final class FabricRegistrarManager implements IRegistrarManager
{
    private static final FabricRegistrarManager INSTANCE = new FabricRegistrarManager();

    public static FabricRegistrarManager getInstance()
    {
        return INSTANCE;
    }

    private FabricRegistrarManager()
    {
    }

    @Override
    public <T extends ICustomRegistryEntry, R extends T> ICustomRegistrar<R> createCustomRegistrar(
      final Class<T> typeClass, final String modId)
    {
        return CustomRegistryManager.getInstance().createNewRegistrar(typeClass, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T, R extends T> IRegistrar<R> createRegistrar(final ResourceKey<? extends Registry<T>> typeClass, final String modId)
    {
        //noinspection UnnecessaryLocalVariable -> Without this the code would not compile as the generics don't line up, but these are singletons in production
        ResourceKey registryKey = typeClass;

        if (registryKey == Registries.ITEM) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry) BuiltInRegistries.ITEM);
        }

        if (registryKey ==  Registries.BLOCK) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry) BuiltInRegistries.BLOCK);
        }

        if (registryKey == Registries.FLUID) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry) BuiltInRegistries.FLUID);
        }

        if (registryKey == Registries.BLOCK_ENTITY_TYPE) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry) BuiltInRegistries.BLOCK_ENTITY_TYPE);
        }

        if (registryKey == Registries.MENU) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry) BuiltInRegistries.MENU);
        }

        if (registryKey == Registries.RECIPE_SERIALIZER) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry) BuiltInRegistries.RECIPE_SERIALIZER);
        }

        final Registry registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue((ResourceKey) typeClass);

        if (registry != null) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, registry);
        }

        throw new IllegalArgumentException("The registry type class: " + typeClass.identifier() + " is not supported.");
    }

    @Override
    public <T extends ICustomRegistryEntry> ICustomRegistry.Builder<T> simpleBuilderFor()
    {
        return CustomRegistryManager.getInstance().createNewSimpleBuilder();
    }
}
