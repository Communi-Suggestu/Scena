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

        final ResourceKey registryName = typeClass;

        if (registryName == Registries.ITEM) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) BuiltInRegistries.ITEM);
        }

        if (registryName ==  Registries.BLOCK) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) BuiltInRegistries.BLOCK);
        }

        if (registryName == Registries.FLUID) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) BuiltInRegistries.FLUID);
        }

        if (registryName == Registries.BLOCK_ENTITY_TYPE) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) BuiltInRegistries.BLOCK_ENTITY_TYPE);
        }

        if (registryName == Registries.MENU) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) BuiltInRegistries.MENU);
        }

        if (registryName == Registries.RECIPE_SERIALIZER) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) BuiltInRegistries.RECIPE_SERIALIZER);
        }

        final Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get((ResourceKey) typeClass);

        if (registry != null) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, registry);
        }

        throw new IllegalArgumentException("The registry type class: " + typeClass.location() + " is not supported.");
    }

    @Override
    public <T extends ICustomRegistryEntry> ICustomRegistry.Builder<T> simpleBuilderFor()
    {
        return CustomRegistryManager.getInstance().createNewSimpleBuilder();
    }
}
