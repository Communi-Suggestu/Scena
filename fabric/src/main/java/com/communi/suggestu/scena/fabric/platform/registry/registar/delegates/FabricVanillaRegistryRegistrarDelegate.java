package com.communi.suggestu.scena.fabric.platform.registry.registar.delegates;

import com.communi.suggestu.scena.core.registries.SimpleCustomRegistryEntry;
import com.communi.suggestu.scena.core.registries.deferred.IRegistrar;
import com.communi.suggestu.scena.core.registries.deferred.IRegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricVanillaRegistryRegistrarDelegate<R extends T, T> implements IRegistrar<R>
{

    public final String modId;
    public final WritableRegistry<T> vanillaRegistry;

    public FabricVanillaRegistryRegistrarDelegate(final String modId, final Registry<T> vanillaRegistry) {
        this.modId = modId;

        if (!(vanillaRegistry instanceof WritableRegistry<T> writableRegistry)) {
            throw new IllegalArgumentException("Registry must be writable!");
        }
        this.vanillaRegistry = writableRegistry;
    }

    @Override
    public <I extends R> IRegistryObject<I> register(final String name, final Supplier<? extends I> factory)
    {
        final I entry = factory.get();
        if (entry instanceof SimpleCustomRegistryEntry registryEntry)
            registryEntry.setRegistryName(new ResourceLocation(modId, name));

        Registry.register(this.vanillaRegistry, new ResourceLocation(modId, name), entry);

        return new FabricVanillaRegistryRegistryObjectDelegate<>(new ResourceLocation(modId, name), entry);
    }
}
