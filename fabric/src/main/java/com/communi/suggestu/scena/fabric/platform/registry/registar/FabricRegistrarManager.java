package com.communi.suggestu.scena.fabric.platform.registry.registar;

import com.communi.suggestu.scena.fabric.platform.registry.registar.delegates.FabricVanillaRegistryRegistrarDelegate;
import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistry;
import com.communi.suggestu.scena.core.registries.IChiselsAndBitsRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.ICustomRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrarManager;
import com.communi.suggestu.scena.core.registries.deferred.impl.custom.CustomRegistryManager;
import net.minecraft.core.Registry;
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
    public <T extends IChiselsAndBitsRegistryEntry, R extends T> ICustomRegistrar<R> createCustomRegistrar(
      final Class<T> typeClass, final String modId)
    {
        return CustomRegistryManager.getInstance().createNewRegistrar(typeClass, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T, R extends T> IRegistrar<R> createRegistrar(final ResourceKey<? extends Registry<T>> typeClass, final String modId)
    {
        final ResourceKey registryName = typeClass;

        if (registryName == Registry.ITEM_REGISTRY) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) Registry.ITEM);
        }

        if (registryName ==  Registry.BLOCK_REGISTRY) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) Registry.BLOCK);
        }

        if (registryName == Registry.FLUID_REGISTRY) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) Registry.FLUID);
        }

        if (registryName == Registry.BLOCK_ENTITY_TYPE_REGISTRY) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) Registry.BLOCK_ENTITY_TYPE);
        }

        if (registryName == Registry.MENU_REGISTRY) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) Registry.MENU);
        }

        if (registryName == Registry.RECIPE_SERIALIZER_REGISTRY) {
            return new FabricVanillaRegistryRegistrarDelegate<>(modId, (Registry<T>) Registry.RECIPE_SERIALIZER);
        }

        throw new IllegalArgumentException("The registry type class: " + typeClass.location() + " is not supported.");
    }

    @Override
    public <T extends IChiselsAndBitsRegistryEntry> IChiselsAndBitsRegistry.Builder<T> simpleBuilderFor()
    {
        return CustomRegistryManager.getInstance().createNewSimpleBuilder();
    }
}
